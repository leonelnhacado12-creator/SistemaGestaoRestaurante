package mz.uem.restaurante.model;

public class Mesa {
    private int id;
    private int numero;
    private int lugares;
    private EstadoMesa estado;
    public Mesa(int id, int numero, int lugares, EstadoMesa estado) { this.id=id; this.numero=numero; this.lugares=lugares; this.estado=estado; }
    public int getId(){return id;} public void setId(int v){id=v;}
    public int getNumero(){return numero;} public void setNumero(int v){numero=v;}
    public int getLugares(){return lugares;} public void setLugares(int v){lugares=v;}
    public EstadoMesa getEstado(){return estado;} public void setEstado(EstadoMesa v){estado=v;}
    @Override public String toString(){return "Mesa "+numero+" ("+estado+")";}
}
