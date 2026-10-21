package mz.uem.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.IngredienteReceita;

/** Persiste a ficha técnica de cada produto do cardápio. */
public class ReceitaProdutoDAO {
    public List<IngredienteReceita> listar(int produtoId) throws SQLException {
        List<IngredienteReceita> resultado = new ArrayList<>();
        String sql = "SELECT r.produto_id,r.item_stock_id,s.nome,s.unidade,r.quantidade "
                + "FROM receitas_produto r JOIN stock_itens s ON s.id=r.item_stock_id WHERE r.produto_id=? ORDER BY s.nome";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resultado.add(new IngredienteReceita(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getDouble(5)));
            }
        }
        return resultado;
    }

    public void substituir(int produtoId, List<IngredienteReceita> ingredientes) throws SQLException {
        try (Connection c = Conexao.obter()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement apagar = c.prepareStatement("DELETE FROM receitas_produto WHERE produto_id=?")) {
                    apagar.setInt(1, produtoId); apagar.executeUpdate();
                }
                try (PreparedStatement inserir = c.prepareStatement("INSERT INTO receitas_produto(produto_id,item_stock_id,quantidade) VALUES(?,?,?)")) {
                    for (IngredienteReceita ingrediente : ingredientes) {
                        inserir.setInt(1, produtoId); inserir.setInt(2, ingrediente.getItemStockId()); inserir.setDouble(3, ingrediente.getQuantidade()); inserir.addBatch();
                    }
                    inserir.executeBatch();
                }
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
            finally { c.setAutoCommit(true); }
        }
    }
}
