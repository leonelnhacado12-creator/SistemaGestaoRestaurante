package mz.uem.restaurante.model;

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private double preco;
    private boolean disponivel;

    public Produto(int id, String nome, String categoria, double preco, boolean disponivel) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.disponivel = disponivel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    @Override
    public String toString() { return nome + " - " + String.format("%.2f MT", preco); }
}
