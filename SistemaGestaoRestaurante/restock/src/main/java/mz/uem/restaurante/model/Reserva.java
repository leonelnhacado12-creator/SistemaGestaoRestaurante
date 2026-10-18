package mz.uem.restaurante.model;

import java.time.LocalDateTime;

public class Reserva {
    private int id; private Mesa mesa; private String cliente; private String telefone; private LocalDateTime dataHora; private int pessoas; private EstadoReserva estado;
    public Reserva(int id, Mesa mesa, String cliente, String telefone, LocalDateTime dataHora, int pessoas, EstadoReserva estado){this.id=id;this.mesa=mesa;this.cliente=cliente;this.telefone=telefone;this.dataHora=dataHora;this.pessoas=pessoas;this.estado=estado;}
    public int getId(){return id;} public Mesa getMesa(){return mesa;} public String getCliente(){return cliente;} public String getTelefone(){return telefone;} public LocalDateTime getDataHora(){return dataHora;} public int getPessoas(){return pessoas;} public EstadoReserva getEstado(){return estado;}
    public void setEstado(EstadoReserva v){estado=v;} public void setMesa(Mesa v){mesa=v;}
}
