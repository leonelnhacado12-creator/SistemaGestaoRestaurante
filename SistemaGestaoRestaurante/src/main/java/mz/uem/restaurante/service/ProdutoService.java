package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.Produto;

public class ProdutoService {
    private final List<Produto> produtos = new ArrayList<>();
    private int proximoId = 1;

    public ProdutoService() {
        criar("Hambúrguer Especial", "Comida", 350, true);
        criar("Frango à Zambeziana", "Comida", 550, true);
        criar("Pizza Média", "Comida", 600, true);
        criar("Sumo Natural", "Bebida", 150, true);
        criar("Água", "Bebida", 80, true);
    }

    public Produto criar(String nome, String categoria, double preco, boolean disponivel) {
        Produto p = new Produto(proximoId++, nome, categoria, preco, disponivel);
        produtos.add(p);
        return p;
    }

    public List<Produto> listar() { return new ArrayList<>(produtos); }

    public boolean atualizar(int id, String nome, String categoria, double preco, boolean disponivel) {
        Produto p = buscar(id);
        if (p == null) return false;
        p.setNome(nome); p.setCategoria(categoria); p.setPreco(preco); p.setDisponivel(disponivel);
        return true;
    }

    public boolean remover(int id) {
        return produtos.removeIf(p -> p.getId() == id);
    }

    public Produto buscar(int id) {
        return produtos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
}
