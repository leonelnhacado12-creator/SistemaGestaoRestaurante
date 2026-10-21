package mz.uem.restaurante.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Cliente cliente;
    private List<ItemPedido> itens = new ArrayList<>();
    private LocalDateTime dataHora;
    private StatusPedido status;
    private double total;
    private double subtotal;
    private double desconto;
    private String formaPagamento = "Não informado";
    private EstadoPagamento estadoPagamento = EstadoPagamento.EM_VALIDACAO;

    public Pedido(int id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == produto.getId()) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                calcularTotal();
                return;
            }
        }
        itens.add(new ItemPedido(produto, quantidade));
        calcularTotal();
    }

    public void alterarQuantidade(int indice, int quantidade) {
        if (indice >= 0 && indice < itens.size()) {
            if (quantidade <= 0) itens.remove(indice);
            else itens.get(indice).setQuantidade(quantidade);
            calcularTotal();
        }
    }

    public void calcularTotal() {
        subtotal = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
        total = subtotal - desconto;
    }

    /** Reconstitui um pedido lido do repositório sem o voltar a persistir. */
    public static Pedido reconstituir(int id, Cliente cliente, LocalDateTime dataHora, StatusPedido status,
            double subtotal, double desconto, double total, String formaPagamento, EstadoPagamento estadoPagamento) {
        Pedido pedido = new Pedido(id, cliente);
        pedido.dataHora = dataHora;
        pedido.status = status;
        pedido.subtotal = subtotal;
        pedido.desconto = desconto;
        pedido.total = total;
        pedido.formaPagamento = formaPagamento == null || formaPagamento.isBlank() ? "Não informado" : formaPagamento;
        pedido.estadoPagamento = estadoPagamento == null ? EstadoPagamento.EM_VALIDACAO : estadoPagamento;
        return pedido;
    }

    public void definirResumoFinanceiro(double subtotal, double desconto, double total, String formaPagamento) {
        this.subtotal = subtotal;
        this.desconto = desconto;
        this.total = total;
        this.formaPagamento = formaPagamento == null ? "Não informado" : formaPagamento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public double getTotal() { return total; }
    public double getSubtotal() { return subtotal; }
    public double getDesconto() { return desconto; }
    public String getFormaPagamento() { return formaPagamento; }
    public EstadoPagamento getEstadoPagamento() { return estadoPagamento; }
    public void setEstadoPagamento(EstadoPagamento estadoPagamento) { this.estadoPagamento = estadoPagamento; }
}
