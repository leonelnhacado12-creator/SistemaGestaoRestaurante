package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.util.*;
import mz.uem.restaurante.dao.PromocaoDAO;
import mz.uem.restaurante.model.Promocao;
import mz.uem.restaurante.validation.Validador;

public class PromocaoService {
    private final List<Promocao> promocoes=new ArrayList<>(); private int proximoId=1; private PromocaoDAO dao;
    public PromocaoService(){criar("Promoção de Estudante",10,true);criar("Happy Hour",15,true);}
    public void activarBaseDados(PromocaoDAO novoDao)throws SQLException{dao=novoDao;List<Promocao> atuais=dao.listar();if(atuais.isEmpty()){for(Promocao p:new ArrayList<>(promocoes))dao.guardar(new Promocao(0,p.getNome(),p.getDescontoPercentual(),p.isAtiva()));atuais=dao.listar();}promocoes.clear();promocoes.addAll(atuais);proximoId=promocoes.stream().mapToInt(Promocao::getId).max().orElse(0)+1;}
    public Promocao criar(String nome,double desconto,boolean ativa){nome=Validador.obrigatorio(nome,"Nome da promoção");desconto=Validador.percentual(desconto);Promocao p=new Promocao(0,nome,desconto,ativa);try{if(dao!=null)dao.guardar(p);else p.setId(proximoId++);}catch(SQLException e){throw new IllegalStateException("Não foi possível guardar a promoção: "+e.getMessage(),e);}promocoes.add(p);return p;}

    public List<Promocao> listar(){return new ArrayList<>(promocoes);} public Promocao buscar(int id){return promocoes.stream().filter(p->p.getId()==id).findFirst().orElse(null);}
    public boolean atualizar(int id,String nome,double desconto,boolean ativa){Promocao p=buscar(id);if(p==null)return false;nome=Validador.obrigatorio(nome,"Nome da promoção");desconto=Validador.percentual(desconto);p.setNome(nome);p.setDescontoPercentual(desconto);p.setAtiva(ativa);try{if(dao!=null)dao.guardar(p);}catch(SQLException e){throw new IllegalStateException("Não foi possível actualizar a promoção: "+e.getMessage(),e);}return true;}
    public boolean remover(int id){try{if(dao!=null)dao.remover(id);}catch(SQLException e){throw new IllegalStateException("Não foi possível remover a promoção: "+e.getMessage(),e);}return promocoes.removeIf(p->p.getId()==id);}
}
