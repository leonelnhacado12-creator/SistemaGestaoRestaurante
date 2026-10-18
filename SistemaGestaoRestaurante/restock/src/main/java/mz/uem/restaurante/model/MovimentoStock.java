package mz.uem.restaurante.model;
import java.time.LocalDateTime;
public class MovimentoStock {
 private final int id, itemId; private final String itemNome, tipo, motivo, utilizador; private final double quantidade, saldoDepois; private final LocalDateTime data;
 public MovimentoStock(int id,int itemId,String itemNome,String tipo,double quantidade,double saldoDepois,String motivo,String utilizador,LocalDateTime data){this.id=id;this.itemId=itemId;this.itemNome=itemNome;this.tipo=tipo;this.quantidade=quantidade;this.saldoDepois=saldoDepois;this.motivo=motivo;this.utilizador=utilizador;this.data=data;}
 public int getId(){return id;} public int getItemId(){return itemId;} public String getItemNome(){return itemNome;} public String getTipo(){return tipo;} public double getQuantidade(){return quantidade;} public double getSaldoDepois(){return saldoDepois;} public String getMotivo(){return motivo;} public String getUtilizador(){return utilizador;} public LocalDateTime getData(){return data;}
}
