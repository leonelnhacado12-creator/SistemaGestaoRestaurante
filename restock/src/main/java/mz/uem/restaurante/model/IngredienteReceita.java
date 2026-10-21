package mz.uem.restaurante.model;

/** Quantidade de um item de inventário necessária para produzir uma unidade do produto. */
public class IngredienteReceita {
    private final int produtoId;
    private final int itemStockId;
    private final String itemStockNome;
    private final String unidade;
    private final double quantidade;

    public IngredienteReceita(int produtoId, int itemStockId, String itemStockNome, String unidade, double quantidade) {
        this.produtoId = produtoId;
        this.itemStockId = itemStockId;
        this.itemStockNome = itemStockNome;
        this.unidade = unidade;
        this.quantidade = quantidade;
    }
    public int getProdutoId() { return produtoId; }
    public int getItemStockId() { return itemStockId; }
    public String getItemStockNome() { return itemStockNome; }
    public String getUnidade() { return unidade; }
    public double getQuantidade() { return quantidade; }
}
