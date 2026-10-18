package mz.uem.restaurante.model;

public interface Pagamento {
    boolean pagar(double valor);
    String getDescricao();
}
