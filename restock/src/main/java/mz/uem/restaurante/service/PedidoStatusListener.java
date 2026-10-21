package mz.uem.restaurante.service;

import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.StatusPedido;

/** Evento de domínio emitido quando um pedido é criado ou muda de estado. */
@FunctionalInterface
public interface PedidoStatusListener {
    void aoAlterar(Pedido pedido, StatusPedido estadoAnterior);
}
