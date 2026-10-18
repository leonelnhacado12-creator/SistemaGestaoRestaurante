package mz.uem.restaurante.model;

public class PagamentoDinheiro implements Pagamento {
    @Override public boolean pagar(double valor) { return valor > 0; }
    @Override public String getDescricao() { return "Dinheiro"; }
}
