package mz.uem.restaurante.model;

/** Pagamento móvel através da carteira e-Mola. */
public class PagamentoEMola implements Pagamento {
    @Override public boolean pagar(double valor) { return valor > 0; }
    @Override public String getDescricao() { return "e-Mola"; }
}
