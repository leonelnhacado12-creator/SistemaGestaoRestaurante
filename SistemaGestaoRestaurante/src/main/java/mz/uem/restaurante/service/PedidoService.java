package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.StatusPedido;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private int proximoId = 1;

    public Pedido criarPedido(mz.uem.restaurante.model.Cliente cliente) {
        Pedido p = new Pedido(proximoId++, cliente);
        pedidos.add(p);
        return p;
    }

    public List<Pedido> listar() { return new ArrayList<>(pedidos); }

    public Pedido buscar(int id) {
        return pedidos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public void atualizarStatus(int id, StatusPedido status) {
        Pedido p = buscar(id);
        if (p != null) p.setStatus(status);
    }
}
