package mz.uem.restaurante.model;

public class PagamentoCartao implements Pagamento {
    @Override public boolean pagar(double valor) { return valor > 0; }
    @Override public String getDescricao() { return "Cartão"; }
}
