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
        total = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public double getTotal() { return total; }
}
