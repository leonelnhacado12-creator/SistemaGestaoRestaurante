package mz.uem.restaurante.model;

public class PagamentoMPesa implements Pagamento {
    @Override public boolean pagar(double valor) { return valor > 0; }
    @Override public String getDescricao() { return "M-Pesa"; }
}
