package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.util.*;
import mz.uem.restaurante.dao.ProdutoDAO;
import mz.uem.restaurante.model.Produto;
import mz.uem.restaurante.validation.Validador;

public class ProdutoService {
    private final List<Produto> produtos = new ArrayList<>();
    private int proximoId = 1;
    private ProdutoDAO dao;

    public ProdutoService() {
        produtos.add(new Produto(proximoId++, "Hambúrguer Especial", "Comida", 350, true));
        produtos.add(new Produto(proximoId++, "Frango à Zambeziana", "Comida", 550, true));
        produtos.add(new Produto(proximoId++, "Pizza Média", "Comida", 600, true));
        produtos.add(new Produto(proximoId++, "Sumo Natural", "Bebida", 150, true));
        produtos.add(new Produto(proximoId++, "Água", "Bebida", 80, true));
    }
    public void activarBaseDados(ProdutoDAO novoDao) throws SQLException {
        dao = novoDao;
        List<Produto> existentes = dao.listar();
        if (existentes.isEmpty()) { for (Produto p : new ArrayList<>(produtos)) dao.guardar(new Produto(0,p.getNome(),p.getCategoria(),p.getPreco(),p.isDisponivel())); existentes = dao.listar(); }
        produtos.clear(); produtos.addAll(existentes);
        proximoId = produtos.stream().mapToInt(Produto::getId).max().orElse(0)+1;
    }
    public boolean usaBaseDados() { return dao != null; }
    public Produto criar(String nome, String categoria, double preco, boolean disponivel) {
        nome = Validador.obrigatorio(nome, "Nome do produto");
        categoria = Validador.obrigatorio(categoria, "Categoria");
        preco = Validador.positivo(preco, "Preço");
        Produto p = new Produto(0, nome, categoria, preco, disponivel);
        try { if (dao != null) dao.guardar(p); else p.setId(proximoId++); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível guardar o produto na base de dados: "+ex.getMessage(), ex); }
        produtos.add(p); return p;
    }
    public List<Produto> listar() { return new ArrayList<>(produtos); }
    public boolean atualizar(int id, String nome, String categoria, double preco, boolean disponivel) {
        Produto p=buscar(id); if(p==null)return false;
        nome = Validador.obrigatorio(nome, "Nome do produto");
        categoria = Validador.obrigatorio(categoria, "Categoria");
        preco = Validador.positivo(preco, "Preço");
        p.setNome(nome);p.setCategoria(categoria);p.setPreco(preco);p.setDisponivel(disponivel);
        try { if(dao!=null)dao.guardar(p); } catch(SQLException ex){throw new IllegalStateException("Não foi possível actualizar o produto: "+ex.getMessage(),ex);} return true;
    }
    public boolean remover(int id) {
        try { if(dao!=null)dao.remover(id); } catch(SQLException ex){throw new IllegalStateException("Não foi possível remover o produto: "+ex.getMessage(),ex);}
        return produtos.removeIf(p->p.getId()==id);
    }
    public Produto buscar(int id) { return produtos.stream().filter(p->p.getId()==id).findFirst().orElse(null); }
}
