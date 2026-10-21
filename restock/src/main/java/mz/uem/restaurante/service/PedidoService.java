package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.StatusPedido;
import mz.uem.restaurante.model.EstadoPagamento;
import mz.uem.restaurante.dao.PedidoDAO;
import java.sql.SQLException;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private int proximoId = 1;
    private PedidoDAO dao = new PedidoDAO();
    private final List<PedidoStatusListener> ouvintes = new ArrayList<>();
    private final List<PagamentoPedidoListener> ouvintesPagamento = new ArrayList<>();
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
        return confirmar(pedido, formaPagamento, subtotal, desconto, total, EstadoPagamento.CONFIRMADO);
    }

    public int confirmar(Pedido pedido, String formaPagamento, double subtotal, double desconto, double total, EstadoPagamento estadoPagamento) throws SQLException {
        pedido.setEstadoPagamento(estadoPagamento);
        if (!persistenciaActiva) {
            pedido.definirResumoFinanceiro(subtotal, desconto, total, formaPagamento);
            if (!pedidos.contains(pedido)) pedidos.add(pedido);
            informar(pedido, null);
            informarPagamento(pedido, null);
            return pedido.getId();
        }
        int id = dao.guardar(pedido, formaPagamento, subtotal, desconto, total, estadoPagamento);
        pedido.setId(id);
        pedido.definirResumoFinanceiro(subtotal, desconto, total, formaPagamento);
        if (!pedidos.contains(pedido)) pedidos.add(pedido);
        informar(pedido, null);
        informarPagamento(pedido, null);
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

    public void adicionarOuvinte(PedidoStatusListener ouvinte) {
        if (ouvinte != null) ouvintes.add(ouvinte);
    }

    public void adicionarOuvintePagamento(PagamentoPedidoListener ouvinte) {
        if (ouvinte != null) ouvintesPagamento.add(ouvinte);
    }

    public void atualizarStatus(int id, StatusPedido status) {
        Pedido p = buscar(id);
        if (p != null) {
            if (status != StatusPedido.PENDENTE && p.getEstadoPagamento() != EstadoPagamento.CONFIRMADO) {
                throw new IllegalStateException("O pagamento deste pedido ainda não foi confirmado. Valide-o antes de iniciar a preparação.");
            }
            StatusPedido anterior = p.getStatus();
            if (anterior == status) return;
            p.setStatus(status);
            if (persistenciaActiva) try { dao.atualizarEstado(id, status); } catch (SQLException ex) { throw new IllegalStateException("Não foi possível actualizar o estado: " + ex.getMessage(), ex); }
            informar(p, anterior);
        }
    }

    public void atualizarEstadoPagamento(int id, EstadoPagamento estadoPagamento) {
        Pedido pedido = buscar(id);
        if (pedido == null) throw new IllegalArgumentException("Pedido não encontrado.");
        EstadoPagamento anterior = pedido.getEstadoPagamento();
        if (anterior == estadoPagamento) return;
        if (anterior == EstadoPagamento.CONFIRMADO || anterior == EstadoPagamento.RECUSADO) {
            throw new IllegalStateException("O pagamento deste pedido já foi tratado e não pode voltar a ser alterado.");
        }
        if (persistenciaActiva) try { dao.atualizarEstadoPagamento(id, estadoPagamento); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível actualizar o pagamento: " + ex.getMessage(), ex); }
        pedido.setEstadoPagamento(estadoPagamento);
        informarPagamento(pedido, anterior);
    }

    private void informar(Pedido pedido, StatusPedido anterior) {
        ouvintes.forEach(ouvinte -> ouvinte.aoAlterar(pedido, anterior));
    }

    private void informarPagamento(Pedido pedido, EstadoPagamento anterior) {
        ouvintesPagamento.forEach(ouvinte -> ouvinte.aoAlterar(pedido, anterior));
    }
}
