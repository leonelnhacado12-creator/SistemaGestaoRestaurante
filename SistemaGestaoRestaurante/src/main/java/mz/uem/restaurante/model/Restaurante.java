package mz.uem.restaurante.model;

public class Restaurante {
    private String nome;
    private String horario;
    private boolean aberto;

    public Restaurante(String nome, String horario, boolean aberto) {
        this.nome = nome;
        this.horario = horario;
        this.aberto = aberto;
    }

    public String getNome() { return nome; }
    public String getHorario() { return horario; }
    public boolean isAberto() { return aberto; }
    public void setNome(String nome) { this.nome = nome; }
    public void setHorario(String horario) { this.horario = horario; }
    public void setAberto(boolean aberto) { this.aberto = aberto; }
}
