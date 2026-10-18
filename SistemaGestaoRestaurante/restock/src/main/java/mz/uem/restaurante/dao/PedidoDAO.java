package mz.uem.restaurante.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.*;

/** Grava pedido, linhas e pagamento numa única transacção. */
public class PedidoDAO {
    public int guardar(Pedido pedido, String formaPagamento, double subtotal, double desconto, double total) throws SQLException {
        try (Connection c=Conexao.obter()) {
            c.setAutoCommit(false);
            try {
                long id;
                try(PreparedStatement p=c.prepareStatement("INSERT INTO pedidos(cliente_id,nome_cliente,estado,subtotal,desconto,total,forma_pagamento) VALUES(?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                    if (pedido.getCliente().getId() > 0) p.setInt(1, pedido.getCliente().getId()); else p.setNull(1, Types.INTEGER);
                    p.setString(2,pedido.getCliente().getNome());p.setString(3,pedido.getStatus().name());p.setDouble(4,subtotal);p.setDouble(5,desconto);p.setDouble(6,total);p.setString(7,formaPagamento);p.executeUpdate();
                    try(ResultSet r=p.getGeneratedKeys()){if(!r.next())throw new SQLException("Não foi possível obter o número do pedido.");id=r.getLong(1);}
                }
                try(PreparedStatement item=c.prepareStatement("INSERT INTO itens_pedido(pedido_id,produto_id,nome_produto,quantidade,preco_unitario,subtotal) VALUES(?,?,?,?,?,?)")) {
                    for(ItemPedido it:pedido.getItens()){item.setLong(1,id);item.setInt(2,it.getProduto().getId());item.setString(3,it.getProduto().getNome());item.setInt(4,it.getQuantidade());item.setDouble(5,it.getProduto().getPreco());item.setDouble(6,it.getSubtotal());item.addBatch();}
                    item.executeBatch();
                }
                try(PreparedStatement pagamento=c.prepareStatement("INSERT INTO pagamentos(pedido_id,forma,valor,estado) VALUES(?,?,?,'CONFIRMADO')")){pagamento.setLong(1,id);pagamento.setString(2,formaPagamento);pagamento.setDouble(3,total);pagamento.executeUpdate();}
                // Diminui stock apenas se houver stock suficiente; para produtos sem stock cadastrado assume 100 unidades.
                try(PreparedStatement stock=c.prepareStatement("UPDATE produtos SET stock=stock-? WHERE id=? AND stock>=?")){
                    for(ItemPedido it:pedido.getItens()){stock.setInt(1,it.getQuantidade());stock.setInt(2,it.getProduto().getId());stock.setInt(3,it.getQuantidade());int n=stock.executeUpdate();if(n==0)throw new SQLException("Stock insuficiente para: "+it.getProduto().getNome());}
                }
                c.commit(); return Math.toIntExact(id);
            } catch(SQLException|RuntimeException ex){c.rollback();throw ex;} finally {c.setAutoCommit(true);}
        }
    }
    public void atualizarEstado(int id, StatusPedido estado) throws SQLException {try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("UPDATE pedidos SET estado=? WHERE id=?")){p.setString(1,estado.name());p.setInt(2,id);p.executeUpdate();}}

    /** Carrega pedidos, itens e resumo de pagamento para manter o histórico após reiniciar. */
    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT id,cliente_id,nome_cliente,data_hora,estado,subtotal,desconto,total,forma_pagamento FROM pedidos ORDER BY data_hora DESC, id DESC";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int clienteId = rs.getInt("cliente_id");
                Cliente cliente = new Cliente(rs.wasNull() ? 0 : clienteId, rs.getString("nome_cliente"), "", "", "");
                Timestamp data = rs.getTimestamp("data_hora");
                Pedido pedido = Pedido.reconstituir(rs.getInt("id"), cliente,
                        data == null ? LocalDateTime.now() : data.toLocalDateTime(),
                        StatusPedido.valueOf(rs.getString("estado")), rs.getDouble("subtotal"),
                        rs.getDouble("desconto"), rs.getDouble("total"), rs.getString("forma_pagamento"));
                carregarItens(c, pedido);
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    private void carregarItens(Connection c, Pedido pedido) throws SQLException {
        String sql = "SELECT produto_id,nome_produto,quantidade,preco_unitario FROM itens_pedido WHERE pedido_id=? ORDER BY id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pedido.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(rs.getInt("produto_id"), rs.getString("nome_produto"), "", rs.getDouble("preco_unitario"), true);
                    pedido.adicionarItem(produto, rs.getInt("quantidade"));
                }
            }
        }
    }
}
