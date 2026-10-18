package mz.uem.restaurante.model;

/** Pagamento móvel através da carteira mKesh. */
public class PagamentoMKesh implements Pagamento {
    @Override public boolean pagar(double valor) { return valor > 0; }
    @Override public String getDescricao() { return "mKesh"; }
}
