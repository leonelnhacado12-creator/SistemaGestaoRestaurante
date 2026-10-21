package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mz.uem.restaurante.dao.ReceitaProdutoDAO;
import mz.uem.restaurante.model.IngredienteReceita;

/** Regras da ficha técnica: cada ingrediente aparece uma única vez e consome uma quantidade positiva. */
public class ReceitaService {
    private ReceitaProdutoDAO dao;
    private final Map<Integer, List<IngredienteReceita>> temporarias = new HashMap<>();

    public void activarBaseDados(ReceitaProdutoDAO novoDao) { dao = novoDao; }
    public List<IngredienteReceita> listar(int produtoId) {
        try { return dao != null ? dao.listar(produtoId) : List.copyOf(temporarias.getOrDefault(produtoId, List.of())); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível carregar a ficha técnica: " + ex.getMessage(), ex); }
    }
    public void substituir(int produtoId, List<IngredienteReceita> ingredientes) {
        if (produtoId <= 0) throw new IllegalArgumentException("Seleccione um produto válido.");
        List<IngredienteReceita> copia = ingredientes == null ? List.of() : new ArrayList<>(ingredientes);
        validar(copia);
        try {
            if (dao != null) dao.substituir(produtoId, copia);
            else temporarias.put(produtoId, copia);
        } catch (SQLException ex) { throw new IllegalStateException("Não foi possível guardar a ficha técnica: " + ex.getMessage(), ex); }
    }
    private void validar(List<IngredienteReceita> ingredientes) {
        java.util.Set<Integer> usados = new java.util.HashSet<>();
        for (IngredienteReceita ingrediente : ingredientes) {
            if (ingrediente.getItemStockId() <= 0 || !Double.isFinite(ingrediente.getQuantidade()) || ingrediente.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Cada ingrediente deve ter uma quantidade maior que zero.");
            }
            if (!usados.add(ingrediente.getItemStockId())) throw new IllegalArgumentException("O mesmo ingrediente não pode ser repetido na receita.");
        }
    }
}
