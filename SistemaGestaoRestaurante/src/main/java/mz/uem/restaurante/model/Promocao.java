package mz.uem.restaurante.model;

public class Promocao {
    private int id;
    private String nome;
    private double descontoPercentual;
    private boolean ativa;

    public Promocao(int id, String nome, double descontoPercentual, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.descontoPercentual = descontoPercentual;
        this.ativa = ativa;
    }

    public double aplicarDesconto(double valor) {
        return ativa ? valor * (1 - descontoPercentual / 100.0) : valor;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getDescontoPercentual() { return descontoPercentual; }
    public boolean isAtiva() { return ativa; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescontoPercentual(double d) { this.descontoPercentual = d; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    @Override
    public String toString() { return nome + " (" + descontoPercentual + "%)"; }
}
