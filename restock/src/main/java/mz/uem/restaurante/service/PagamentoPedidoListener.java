package mz.uem.restaurante.service;

import mz.uem.restaurante.model.EstadoPagamento;
import mz.uem.restaurante.model.Pedido;

/** Evento emitido quando a equipa confirma ou recusa o pagamento de um pedido. */
@FunctionalInterface
public interface PagamentoPedidoListener {
    void aoAlterar(Pedido pedido, EstadoPagamento estadoAnterior);
}
