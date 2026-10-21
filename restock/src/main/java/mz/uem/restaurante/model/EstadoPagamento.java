package mz.uem.restaurante.model;

/** Estado verificável do pagamento antes de a cozinha iniciar o pedido. */
public enum EstadoPagamento {
    EM_VALIDACAO("A aguardar confirmação"),
    CONFIRMADO("Confirmado"),
    RECUSADO("Recusado");

    private final String descricao;
    EstadoPagamento(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
