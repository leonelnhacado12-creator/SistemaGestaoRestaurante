package mz.uem.restaurante.service;
import java.sql.SQLException;import java.util.*;import mz.uem.restaurante.dao.ClienteDAO;import mz.uem.restaurante.model.Cliente;import mz.uem.restaurante.validation.Validador;
public class ClienteService{
 private final List<Cliente> clientes=new ArrayList<>();private int prox=1;private ClienteDAO dao;
 public void activarBaseDados(ClienteDAO d)throws SQLException{dao=d;clientes.clear();clientes.addAll(d.listar());prox=clientes.stream().mapToInt(Cliente::getId).max().orElse(0)+1;}
 public List<Cliente> listar(String filtro){String f=filtro==null?"":filtro.toLowerCase();return clientes.stream().filter(c->c.getNome().toLowerCase().contains(f)||c.getTelefone().toLowerCase().contains(f)).toList();}
 public Cliente buscar(int id){return clientes.stream().filter(c->c.getId()==id).findFirst().orElse(null);}
 public Cliente guardar(int id,String nome,String telefone){nome=Validador.obrigatorio(nome,"Nome do cliente");telefone=telefone==null?"":telefone.trim();String user=nome.toLowerCase().replaceAll("[^a-z0-9]","");Cliente c=id>0?buscar(id):null;if(c==null)c=new Cliente(0,nome,user,"",telefone);else{c.setNome(nome);c.setUsername(user);c.setTelefone(telefone);}try{if(dao!=null)dao.guardar(c);else if(c.getId()==0)c.setId(prox++);}catch(SQLException e){throw new IllegalStateException("Erro ao guardar cliente: "+e.getMessage(),e);}if(!clientes.contains(c))clientes.add(c);return c;}
 public boolean remover(int id){try{if(dao!=null)dao.remover(id);}catch(SQLException e){throw new IllegalStateException("Erro ao remover cliente: "+e.getMessage(),e);}return clientes.removeIf(c->c.getId()==id);}
}
