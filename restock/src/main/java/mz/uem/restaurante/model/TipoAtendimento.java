package mz.uem.restaurante.model;

public enum TipoAtendimento {
    RECLAMACAO("Reclamação"), FEEDBACK("Feedback"), DUVIDA("Dúvida"), OUTRO("Outro");

    private final String descricao;
    TipoAtendimento(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
    @Override public String toString() { return descricao; }
}
