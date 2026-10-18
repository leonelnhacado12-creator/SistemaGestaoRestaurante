package mz.uem.restaurante.model;

import java.time.LocalDate;

public class LoteStock {
 private int id; private ItemStock item; private Fornecedor fornecedor; private String lote; private double quantidade; private LocalDate validade;
 public LoteStock(int id,ItemStock item,Fornecedor fornecedor,String lote,double quantidade,LocalDate validade){this.id=id;this.item=item;this.fornecedor=fornecedor;this.lote=lote;this.quantidade=quantidade;this.validade=validade;}
 public int getId(){return id;} public ItemStock getItem(){return item;} public Fornecedor getFornecedor(){return fornecedor;} public String getLote(){return lote;} public double getQuantidade(){return quantidade;} public LocalDate getValidade(){return validade;}
 public boolean isExpirado(){return validade!=null && validade.isBefore(LocalDate.now());} public boolean expiraEm(int dias){return validade!=null && !isExpirado() && !validade.isAfter(LocalDate.now().plusDays(dias));}
}
