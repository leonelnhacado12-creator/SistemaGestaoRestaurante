package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.StatusPedido;
import mz.uem.restaurante.dao.PedidoDAO;
import java.sql.SQLException;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private int proximoId = 1;
    private PedidoDAO dao = new PedidoDAO();
    private boolean persistenciaActiva;
    public void activarPersistencia(boolean activa) { persistenciaActiva = activa; }
    public void activarBaseDados(PedidoDAO novoDao) throws SQLException {
        dao = novoDao;
        pedidos.clear();
        pedidos.addAll(dao.listar());
        proximoId = pedidos.stream().mapToInt(Pedido::getId).max().orElse(0) + 1;
        persistenciaActiva = true;
    }
    public int confirmar(Pedido pedido, String formaPagamento, double subtotal, double desconto, double total) throws SQLException {
        if (!persistenciaActiva) {
            pedido.definirResumoFinanceiro(subtotal, desconto, total, formaPagamento);
            if (!pedidos.contains(pedido)) pedidos.add(pedido);
            return pedido.getId();
        }
        int id = dao.guardar(pedido, formaPagamento, subtotal, desconto, total);
        pedido.setId(id);
        pedido.definirResumoFinanceiro(subtotal, desconto, total, formaPagamento);
        if (!pedidos.contains(pedido)) pedidos.add(pedido);
        return id;
    }

    public Pedido criarPedido(mz.uem.restaurante.model.Cliente cliente) {
        Pedido p = new Pedido(proximoId++, cliente);
        return p;
    }

    public List<Pedido> listar() { return new ArrayList<>(pedidos); }

    public double totalFaturado() {
        return pedidos.stream().mapToDouble(Pedido::getTotal).sum();
    }

    public Pedido buscar(int id) {
        return pedidos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public void atualizarStatus(int id, StatusPedido status) {
        Pedido p = buscar(id);
        if (p != null) { p.setStatus(status); if (persistenciaActiva) try { dao.atualizarEstado(id, status); } catch (SQLException ex) { throw new IllegalStateException("Não foi possível actualizar o estado: " + ex.getMessage(), ex); } }
    }
}
