package mz.uem.restaurante.dao;
import java.sql.*;import java.util.*;import mz.uem.restaurante.database.Conexao;import mz.uem.restaurante.model.Cliente;
public class ClienteDAO {
 public List<Cliente> listar()throws SQLException{List<Cliente> a=new ArrayList<>();try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("SELECT id,nome,COALESCE(username,''),'' AS senha,COALESCE(telefone,'') FROM clientes ORDER BY nome");ResultSet r=p.executeQuery()){while(r.next())a.add(new Cliente(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5)));}return a;}
 public Cliente guardar(Cliente x)throws SQLException{String q=x.getId()>0?"UPDATE clientes SET nome=?,telefone=?,username=? WHERE id=?":"INSERT INTO clientes(nome,telefone,username) VALUES(?,?,?)";try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)){p.setString(1,x.getNome());p.setString(2,x.getTelefone());p.setString(3,x.getUsername());if(x.getId()>0)p.setInt(4,x.getId());p.executeUpdate();if(x.getId()<=0)try(ResultSet k=p.getGeneratedKeys()){if(k.next())x.setId(k.getInt(1));}}return x;}
 public void remover(int id)throws SQLException{try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("DELETE FROM clientes WHERE id=?")){p.setInt(1,id);p.executeUpdate();}}
}
