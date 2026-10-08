package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.Promocao;

public class PromocaoService {
    private final List<Promocao> promocoes = new ArrayList<>();
    private int proximoId = 1;

    public PromocaoService() {
        criar("Promoção de Estudante", 10, true);
        criar("Happy Hour", 15, true);
    }

    public Promocao criar(String nome, double desconto, boolean ativa) {
        Promocao p = new Promocao(proximoId++, nome, desconto, ativa);
        promocoes.add(p);
        return p;
    }

    public List<Promocao> listar() { return new ArrayList<>(promocoes); }

    public Promocao buscar(int id) {
        return promocoes.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public boolean atualizar(int id, String nome, double desconto, boolean ativa) {
        Promocao p = buscar(id);
        if (p == null) return false;
        p.setNome(nome); p.setDescontoPercentual(desconto); p.setAtiva(ativa);
        return true;
    }

    public boolean remover(int id) { return promocoes.removeIf(p -> p.getId() == id); }
}
