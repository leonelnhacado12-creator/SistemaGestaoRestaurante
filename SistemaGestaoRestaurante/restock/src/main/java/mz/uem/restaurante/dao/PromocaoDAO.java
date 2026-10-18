package mz.uem.restaurante.dao;

import java.sql.*;
import java.util.*;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.Promocao;

public class PromocaoDAO {
    public List<Promocao> listar() throws SQLException { List<Promocao> out=new ArrayList<>();try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("SELECT id,nome,desconto_percentual,ativa FROM promocoes ORDER BY id");ResultSet r=p.executeQuery()){while(r.next())out.add(new Promocao(r.getInt(1),r.getString(2),r.getDouble(3),r.getBoolean(4)));}return out; }
    public Promocao guardar(Promocao x) throws SQLException {String sql=x.getId()>0?"UPDATE promocoes SET nome=?,desconto_percentual=?,ativa=? WHERE id=?":"INSERT INTO promocoes(nome,desconto_percentual,ativa) VALUES(?,?,?)";try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){p.setString(1,x.getNome());p.setDouble(2,x.getDescontoPercentual());p.setBoolean(3,x.isAtiva());if(x.getId()>0)p.setInt(4,x.getId());p.executeUpdate();if(x.getId()<=0)try(ResultSet k=p.getGeneratedKeys()){if(k.next())x.setId(k.getInt(1));}}return x;}
    public void remover(int id)throws SQLException{try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("DELETE FROM promocoes WHERE id=?")){p.setInt(1,id);p.executeUpdate();}}
}
