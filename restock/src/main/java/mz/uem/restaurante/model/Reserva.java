package mz.uem.restaurante.model;

import java.time.LocalDateTime;

public class Reserva {
    private int id; private Mesa mesa; private String cliente; private String telefone; private LocalDateTime dataHora; private int pessoas; private EstadoReserva estado; private boolean lembreteEnviado;
    public Reserva(int id, Mesa mesa, String cliente, String telefone, LocalDateTime dataHora, int pessoas, EstadoReserva estado){this(id, mesa, cliente, telefone, dataHora, pessoas, estado, false);}
    public Reserva(int id, Mesa mesa, String cliente, String telefone, LocalDateTime dataHora, int pessoas, EstadoReserva estado, boolean lembreteEnviado){this.id=id;this.mesa=mesa;this.cliente=cliente;this.telefone=telefone;this.dataHora=dataHora;this.pessoas=pessoas;this.estado=estado;this.lembreteEnviado=lembreteEnviado;}
    public int getId(){return id;} public void setId(int v){id=v;} public Mesa getMesa(){return mesa;} public String getCliente(){return cliente;} public String getTelefone(){return telefone;} public LocalDateTime getDataHora(){return dataHora;} public int getPessoas(){return pessoas;} public EstadoReserva getEstado(){return estado;}
    public boolean isLembreteEnviado(){return lembreteEnviado;}
    public void setEstado(EstadoReserva v){estado=v;} public void setMesa(Mesa v){mesa=v;} public void setLembreteEnviado(boolean v){lembreteEnviado=v;}
}
