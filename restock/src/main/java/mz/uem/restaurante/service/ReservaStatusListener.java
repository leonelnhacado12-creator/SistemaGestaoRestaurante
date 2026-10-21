package mz.uem.restaurante.service;

import mz.uem.restaurante.model.EstadoReserva;
import mz.uem.restaurante.model.Reserva;

/** Evento emitido quando a reserva é submetida ou tratada pela equipa. */
@FunctionalInterface
public interface ReservaStatusListener {
    void aoAlterar(Reserva reserva, EstadoReserva estadoAnterior);
}
