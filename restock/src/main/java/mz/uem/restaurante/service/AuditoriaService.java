package mz.uem.restaurante.service;

import java.sql.*; import java.time.LocalDateTime; import java.util.*; import mz.uem.restaurante.database.Conexao; import mz.uem.restaurante.model.RegistoAuditoria;

public class AuditoriaService {
 public void registar(String utilizador,String operacao,String entidade,String detalhe){try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("INSERT INTO auditoria(utilizador,operacao,entidade,detalhe) VALUES(?,?,?,?)")){p.setString(1,utilizador);p.setString(2,operacao);p.setString(3,entidade);p.setString(4,detalhe);p.executeUpdate();}catch(SQLException ignored){}}
 public List<RegistoAuditoria> listar(){List<RegistoAuditoria> r=new ArrayList<>();try(Connection c=Conexao.obter();PreparedStatement p=c.prepareStatement("SELECT id,utilizador,operacao,entidade,detalhe,data_hora FROM auditoria ORDER BY data_hora DESC LIMIT 500");ResultSet s=p.executeQuery()){while(s.next())r.add(new RegistoAuditoria(s.getLong(1),s.getString(2),s.getString(3),s.getString(4),s.getString(5),s.getTimestamp(6).toLocalDateTime()));}catch(SQLException ignored){}return r;}
}
