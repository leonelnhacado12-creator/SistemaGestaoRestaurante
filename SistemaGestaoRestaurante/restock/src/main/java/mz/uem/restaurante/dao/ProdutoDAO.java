package mz.uem.restaurante.dao;

import java.sql.*;
import java.util.*;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.Produto;

public class ProdutoDAO {
    public List<Produto> listar() throws SQLException {
        List<Produto> out = new ArrayList<>();
        try (Connection c=Conexao.obter(); PreparedStatement p=c.prepareStatement("SELECT id,nome,categoria,preco,disponivel FROM produtos ORDER BY id"); ResultSet r=p.executeQuery()) {
            while(r.next()) out.add(new Produto(r.getInt("id"),r.getString("nome"),r.getString("categoria"),r.getDouble("preco"),r.getBoolean("disponivel")));
        } return out;
    }
    public Produto guardar(Produto x) throws SQLException {
        String sql = x.getId()>0 ? "UPDATE produtos SET nome=?,categoria=?,preco=?,disponivel=? WHERE id=?" : "INSERT INTO produtos(nome,categoria,preco,disponivel) VALUES(?,?,?,?)";
        try(Connection c=Conexao.obter(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,x.getNome());p.setString(2,x.getCategoria());p.setDouble(3,x.getPreco());p.setBoolean(4,x.isDisponivel());if(x.getId()>0)p.setInt(5,x.getId());p.executeUpdate();
            if(x.getId()<=0)try(ResultSet k=p.getGeneratedKeys()){if(k.next())x.setId(k.getInt(1));}
        } return x;
    }
    public void remover(int id) throws SQLException { try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("DELETE FROM produtos WHERE id=?")){p.setInt(1,id);p.executeUpdate();} }
}
