package mz.uem.restaurante.model;

import java.time.LocalDateTime;

/** Uma mensagem da conversa entre o cliente e a equipa de atendimento. */
public class MensagemAtendimento {
    private long id;
    private final String cliente;
    private final String telefone;
    private final TipoAtendimento tipo;
    private final RemetenteAtendimento remetente;
    private final String mensagem;
    private final LocalDateTime dataHora;

    public MensagemAtendimento(long id, String cliente, String telefone, TipoAtendimento tipo,
            RemetenteAtendimento remetente, String mensagem, LocalDateTime dataHora) {
        this.id = id;
        this.cliente = cliente;
        this.telefone = telefone == null ? "" : telefone;
        this.tipo = tipo;
        this.remetente = remetente;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCliente() { return cliente; }
    public String getTelefone() { return telefone; }
    public TipoAtendimento getTipo() { return tipo; }
    public RemetenteAtendimento getRemetente() { return remetente; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataHora() { return dataHora; }
}
