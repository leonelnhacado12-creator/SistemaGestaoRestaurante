package mz.uem.restaurante.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.*;

/** Grava pedido, linhas e pagamento numa única transacção. */
public class PedidoDAO {
    public int guardar(Pedido pedido, String formaPagamento, double subtotal, double desconto, double total, EstadoPagamento estadoPagamento) throws SQLException {
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
                try(PreparedStatement pagamento=c.prepareStatement("INSERT INTO pagamentos(pedido_id,forma,valor,estado) VALUES(?,?,?,?)")){pagamento.setLong(1,id);pagamento.setString(2,formaPagamento);pagamento.setDouble(3,total);pagamento.setString(4,estadoPagamento.name());pagamento.executeUpdate();}
                if (estadoPagamento == EstadoPagamento.CONFIRMADO) {
                    baixarStock(c, pedido);
                    baixarIngredientesReceita(c, id);
                }
                c.commit(); return Math.toIntExact(id);
            } catch(SQLException|RuntimeException ex){c.rollback();throw ex;} finally {c.setAutoCommit(true);}
        }
    }
    public void atualizarEstado(int id, StatusPedido estado) throws SQLException {try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("UPDATE pedidos SET estado=? WHERE id=?")){p.setString(1,estado.name());p.setInt(2,id);p.executeUpdate();}}
    public void atualizarEstadoPagamento(int id, EstadoPagamento estado) throws SQLException {
        try (Connection c = Conexao.obter()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement pagamento = c.prepareStatement("UPDATE pagamentos SET estado=? WHERE pedido_id=?")) {
                    pagamento.setString(1, estado.name()); pagamento.setInt(2, id);
                    if (pagamento.executeUpdate() == 0) throw new SQLException("Pagamento do pedido não encontrado.");
                }
                if (estado == EstadoPagamento.CONFIRMADO) {
                    baixarStock(c, id);
                    baixarIngredientesReceita(c, id);
                }
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
            finally { c.setAutoCommit(true); }
        }
    }

    /** Carrega pedidos, itens e resumo de pagamento para manter o histórico após reiniciar. */
    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.id,p.cliente_id,p.nome_cliente,p.data_hora,p.estado,p.subtotal,p.desconto,p.total,p.forma_pagamento,COALESCE(c.telefone,'') AS telefone,COALESCE(pg.estado,'CONFIRMADO') AS estado_pagamento "
                + "FROM pedidos p LEFT JOIN clientes c ON c.id=p.cliente_id LEFT JOIN pagamentos pg ON pg.pedido_id=p.id ORDER BY p.data_hora DESC, p.id DESC";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int clienteId = rs.getInt("cliente_id");
                Cliente cliente = new Cliente(rs.wasNull() ? 0 : clienteId, rs.getString("nome_cliente"), "", "", rs.getString("telefone"));
                Timestamp data = rs.getTimestamp("data_hora");
                Pedido pedido = Pedido.reconstituir(rs.getInt("id"), cliente,
                        data == null ? LocalDateTime.now() : data.toLocalDateTime(),
                        StatusPedido.valueOf(rs.getString("estado")), rs.getDouble("subtotal"),
                        rs.getDouble("desconto"), rs.getDouble("total"), rs.getString("forma_pagamento"), EstadoPagamento.valueOf(rs.getString("estado_pagamento")));
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

    private void baixarStock(Connection c, Pedido pedido) throws SQLException {
        try (PreparedStatement stock = c.prepareStatement("UPDATE produtos SET stock=stock-? WHERE id=? AND stock>=?")) {
            for (ItemPedido it : pedido.getItens()) {
                stock.setInt(1, it.getQuantidade()); stock.setInt(2, it.getProduto().getId()); stock.setInt(3, it.getQuantidade());
                if (stock.executeUpdate() == 0) throw new SQLException("Stock insuficiente para: " + it.getProduto().getNome());
            }
        }
    }

    private void baixarStock(Connection c, int pedidoId) throws SQLException {
        String sql = "SELECT produto_id,nome_produto,quantidade FROM itens_pedido WHERE pedido_id=?";
        try (PreparedStatement itens = c.prepareStatement(sql)) {
            itens.setInt(1, pedidoId);
            try (ResultSet rs = itens.executeQuery(); PreparedStatement stock = c.prepareStatement("UPDATE produtos SET stock=stock-? WHERE id=? AND stock>=?")) {
                while (rs.next()) {
                    int produtoId = rs.getInt("produto_id"); int quantidade = rs.getInt("quantidade");
                    stock.setInt(1, quantidade); stock.setInt(2, produtoId); stock.setInt(3, quantidade);
                    if (produtoId == 0 || stock.executeUpdate() == 0) throw new SQLException("Stock insuficiente para: " + rs.getString("nome_produto"));
                }
            }
        }
    }

    /** Baixa ingredientes de todas as fichas técnicas do pedido e grava o movimento na mesma transacção. */
    private void baixarIngredientesReceita(Connection c, long pedidoId) throws SQLException {
        String receita = "SELECT r.item_stock_id,s.nome,SUM(r.quantidade * i.quantidade) AS consumo "
                + "FROM itens_pedido i JOIN receitas_produto r ON r.produto_id=i.produto_id "
                + "JOIN stock_itens s ON s.id=r.item_stock_id WHERE i.pedido_id=? GROUP BY r.item_stock_id,s.nome";
        try (PreparedStatement ps = c.prepareStatement(receita)) {
            ps.setLong(1, pedidoId);
            try (ResultSet ingredientes = ps.executeQuery();
                 PreparedStatement bloquear = c.prepareStatement("SELECT quantidade FROM stock_itens WHERE id=? FOR UPDATE");
                 PreparedStatement actualizar = c.prepareStatement("UPDATE stock_itens SET quantidade=? WHERE id=?");
                 PreparedStatement movimento = c.prepareStatement("INSERT INTO movimentos_stock(item_id,tipo,quantidade,saldo_depois,motivo,utilizador) VALUES(?,'SAÍDA',?,?,?,?)")) {
                while (ingredientes.next()) {
                    int itemId = ingredientes.getInt("item_stock_id");
                    double consumo = ingredientes.getDouble("consumo");
                    bloquear.setInt(1, itemId);
                    double saldo;
                    try (ResultSet actual = bloquear.executeQuery()) {
                        if (!actual.next()) throw new SQLException("Ingrediente da receita não encontrado: " + ingredientes.getString("nome"));
                        saldo = actual.getDouble(1);
                    }
                    if (saldo < consumo) throw new SQLException("Ingrediente insuficiente para preparar o pedido: " + ingredientes.getString("nome"));
                    double novoSaldo = saldo - consumo;
                    actualizar.setDouble(1, novoSaldo); actualizar.setInt(2, itemId); actualizar.executeUpdate();
                    movimento.setInt(1, itemId); movimento.setDouble(2, consumo); movimento.setDouble(3, novoSaldo);
                    movimento.setString(4, "Receita do pedido #" + pedidoId); movimento.setString(5, "sistema"); movimento.executeUpdate();
                }
            }
        }
    }
}
