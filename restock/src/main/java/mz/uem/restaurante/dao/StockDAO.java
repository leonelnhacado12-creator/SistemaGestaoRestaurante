package mz.uem.restaurante.dao;

import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.ItemStock;
import mz.uem.restaurante.model.MovimentoStock;
import java.sql.*; import java.util.*;

/** Persistência do inventário e registo de movimentos. */
public class StockDAO {
 public List<ItemStock> listar(String filtro) throws SQLException {
  List<ItemStock> out=new ArrayList<>(); String q="SELECT id,nome,categoria,unidade,quantidade,stock_minimo FROM stock_itens WHERE nome LIKE ? OR categoria LIKE ? ORDER BY nome";
  try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement(q)){String f="%"+(filtro==null?"":filtro.trim())+"%";p.setString(1,f);p.setString(2,f);try(ResultSet r=p.executeQuery()){while(r.next())out.add(new ItemStock(r.getInt("id"),r.getString("nome"),r.getString("categoria"),r.getString("unidade"),r.getDouble("quantidade"),r.getDouble("stock_minimo")));}} return out;
 }
 public ItemStock guardar(ItemStock x) throws SQLException {
  String sql=x.getId()>0?"UPDATE stock_itens SET nome=?,categoria=?,unidade=?,quantidade=?,stock_minimo=? WHERE id=?":"INSERT INTO stock_itens(nome,categoria,unidade,quantidade,stock_minimo) VALUES(?,?,?,?,?)";
  try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){p.setString(1,x.getNome());p.setString(2,x.getCategoria());p.setString(3,x.getUnidade());p.setDouble(4,x.getQuantidade());p.setDouble(5,x.getStockMinimo());if(x.getId()>0)p.setInt(6,x.getId());p.executeUpdate();if(x.getId()<=0)try(ResultSet k=p.getGeneratedKeys()){if(k.next())x.setId(k.getInt(1));}}return x;
 }
 public void eliminar(int id) throws SQLException {try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("DELETE FROM stock_itens WHERE id=?")){p.setInt(1,id);p.executeUpdate();}}
 public void movimentar(int id,double quantidade,boolean entrada,String motivo,String utilizador) throws SQLException {
  if(quantidade<=0)throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
  try(Connection c=Conexao.obter()){c.setAutoCommit(false);try{double saldo;String nome;try(PreparedStatement p=c.prepareStatement("SELECT nome,quantidade FROM stock_itens WHERE id=? FOR UPDATE")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("Item de stock não encontrado.");nome=r.getString(1);saldo=r.getDouble(2);}}double novo=entrada?saldo+quantidade:saldo-quantidade;if(novo<0)throw new IllegalArgumentException("Stock insuficiente. Saldo actual: "+saldo);try(PreparedStatement p=c.prepareStatement("UPDATE stock_itens SET quantidade=? WHERE id=?")){p.setDouble(1,novo);p.setInt(2,id);p.executeUpdate();}try(PreparedStatement p=c.prepareStatement("INSERT INTO movimentos_stock(item_id,tipo,quantidade,saldo_depois,motivo,utilizador) VALUES(?,?,?,?,?,?)")){p.setInt(1,id);p.setString(2,entrada?"ENTRADA":"SAÍDA");p.setDouble(3,quantidade);p.setDouble(4,novo);p.setString(5,motivo);p.setString(6,utilizador);p.executeUpdate();}c.commit();}catch(Exception ex){c.rollback();if(ex instanceof SQLException se)throw se;if(ex instanceof RuntimeException re)throw re;throw new SQLException(ex);}finally{c.setAutoCommit(true);}}
 }
 public List<MovimentoStock> listarMovimentos(int limite) throws SQLException {List<MovimentoStock> out=new ArrayList<>();String q="SELECT m.id,m.item_id,s.nome,m.tipo,m.quantidade,m.saldo_depois,m.motivo,m.utilizador,m.data_hora FROM movimentos_stock m JOIN stock_itens s ON s.id=m.item_id ORDER BY m.id DESC LIMIT ?";try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement(q)){p.setInt(1,Math.max(1,limite));try(ResultSet r=p.executeQuery()){while(r.next()){Timestamp t=r.getTimestamp("data_hora");out.add(new MovimentoStock(r.getInt("id"),r.getInt("item_id"),r.getString("nome"),r.getString("tipo"),r.getDouble("quantidade"),r.getDouble("saldo_depois"),r.getString("motivo"),r.getString("utilizador"),t==null?null:t.toLocalDateTime()));}}}return out;}
}
