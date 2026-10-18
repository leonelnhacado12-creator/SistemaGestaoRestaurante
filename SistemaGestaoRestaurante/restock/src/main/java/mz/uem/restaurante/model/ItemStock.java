package mz.uem.restaurante.model;

/** Ingrediente ou material controlado pelo inventário. */
public class ItemStock {
    private int id; private String nome, categoria, unidade; private double quantidade, stockMinimo;
    public ItemStock(int id, String nome, String categoria, String unidade, double quantidade, double stockMinimo) {
        this.id=id; this.nome=nome; this.categoria=categoria; this.unidade=unidade; this.quantidade=quantidade; this.stockMinimo=stockMinimo;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getNome(){return nome;} public void setNome(String v){nome=v;}
    public String getCategoria(){return categoria;} public void setCategoria(String v){categoria=v;}
    public String getUnidade(){return unidade;} public void setUnidade(String v){unidade=v;}
    public double getQuantidade(){return quantidade;} public void setQuantidade(double v){quantidade=v;}
    public double getStockMinimo(){return stockMinimo;} public void setStockMinimo(double v){stockMinimo=v;}
    public boolean isStockBaixo(){return quantidade<=stockMinimo;}
}
