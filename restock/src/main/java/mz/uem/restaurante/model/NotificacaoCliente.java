package mz.uem.restaurante.model;

import java.time.LocalDateTime;

/** Mensagem interna que o cliente pode consultar ao acompanhar o seu pedido. */
public class NotificacaoCliente {
    private long id;
    private final int pedidoId;
    private final String cliente;
    private final String telefone;
    private final String mensagem;
    private final LocalDateTime dataHora;
    private boolean lida;

    public NotificacaoCliente(long id, int pedidoId, String cliente, String telefone,
            String mensagem, LocalDateTime dataHora, boolean lida) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.cliente = cliente;
        this.telefone = telefone == null ? "" : telefone;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
        this.lida = lida;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public String getCliente() { return cliente; }
    public String getTelefone() { return telefone; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataHora() { return dataHora; }
    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }
}
