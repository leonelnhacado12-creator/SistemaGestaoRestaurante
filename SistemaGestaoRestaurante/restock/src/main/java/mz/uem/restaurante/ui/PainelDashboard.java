package mz.uem.restaurante.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import mz.uem.restaurante.model.ItemStock;
import mz.uem.restaurante.service.DadosSistema;

/** Dashboard editável no GUI Builder; os dados são injetados após a criação visual. */
public class PainelDashboard extends javax.swing.JPanel {
    private DadosSistema dados;
    private Consumer<String> navegar;

    public PainelDashboard() {
        initComponents();
        configurarVisual();
        configurarEventos();
    }

    public PainelDashboard(DadosSistema dados, Consumer<String> navegar) { this(); configurar(dados, navegar); }

    public final void configurar(DadosSistema dados, Consumer<String> navegar) { this.dados = dados; this.navegar = navegar; atualizar(); }

    private void configurarVisual() {
        setBackground(Ui.FUNDO); pnlProdutos.setBackground(Color.WHITE); pnlPedidos.setBackground(Color.WHITE); pnlFaturacao.setBackground(Color.WHITE); pnlStock.setBackground(Color.WHITE); pnlAtalhos.setBackground(Color.WHITE);
        for (javax.swing.JPanel painel : new javax.swing.JPanel[]{pnlProdutos, pnlPedidos, pnlFaturacao, pnlStock, pnlAtalhos}) painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)), BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        lblTitulo.setForeground(Ui.PRIMARIA); lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        for (javax.swing.JLabel valor : new javax.swing.JLabel[]{lblProdutosValor, lblPedidosValor, lblFaturacaoValor, lblStockValor}) valor.setFont(new Font("SansSerif", Font.BOLD, 23));
        lblProdutosValor.setForeground(Ui.PRIMARIA); lblPedidosValor.setForeground(Ui.DESTAQUE); lblFaturacaoValor.setForeground(new Color(102, 78, 167)); lblStockValor.setForeground(Ui.ALERTA);
        estilizar(btnVendas, Ui.DESTAQUE); estilizar(btnPedidos, Ui.PRIMARIA); estilizar(btnProdutos, new Color(102, 78, 167)); estilizar(btnStock, new Color(93, 109, 126));
    }
    private void estilizar(javax.swing.JButton botao, Color cor) { botao.setBackground(cor); botao.setForeground(Color.WHITE); botao.setFocusPainted(false); }
    private void configurarEventos() { btnVendas.addActionListener(e -> abrir("vendas")); btnPedidos.addActionListener(e -> abrir("pedidos")); btnProdutos.addActionListener(e -> abrir("produtos")); btnStock.addActionListener(e -> abrir("stock")); }
    private void abrir(String destino) { if (navegar != null) navegar.accept(destino); }

    public void atualizar() {
        if (dados == null) return;
        lblProdutosValor.setText(String.valueOf(dados.produtoService.listar().size()));
        lblPedidosValor.setText(String.valueOf(dados.pedidoService.listar().size()));
        lblFaturacaoValor.setText(String.format("%.2f MT", dados.pedidoService.totalFaturado()));
        try { lblStockValor.setText(String.valueOf(dados.stockService.listar("").stream().filter(ItemStock::isStockBaixo).count())); } catch (RuntimeException ex) { lblStockValor.setText("—"); }
        lblEstadoBd.setText(dados.isBaseDadosActiva() ? "● Base de dados ligada" : "● Base de dados indisponível");
        lblEstadoBd.setToolTipText(dados.estadoBaseDados() + " | " + dados.resumoPagamentos());
        lblEstadoBd.setForeground(dados.isBaseDadosActiva() ? Ui.DESTAQUE : Ui.ALERTA);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel(); lblSubtitulo = new javax.swing.JLabel(); lblEstadoBd = new javax.swing.JLabel();
        pnlProdutos = new javax.swing.JPanel(); lblProdutos = new javax.swing.JLabel(); lblProdutosValor = new javax.swing.JLabel();
        pnlPedidos = new javax.swing.JPanel(); lblPedidos = new javax.swing.JLabel(); lblPedidosValor = new javax.swing.JLabel();
        pnlFaturacao = new javax.swing.JPanel(); lblFaturacao = new javax.swing.JLabel(); lblFaturacaoValor = new javax.swing.JLabel();
        pnlStock = new javax.swing.JPanel(); lblStock = new javax.swing.JLabel(); lblStockValor = new javax.swing.JLabel();
        pnlAtalhos = new javax.swing.JPanel(); lblAtalhos = new javax.swing.JLabel(); btnVendas = new javax.swing.JButton(); btnPedidos = new javax.swing.JButton(); btnProdutos = new javax.swing.JButton(); btnStock = new javax.swing.JButton();
        lblTitulo.setText("Visão geral do restaurante"); lblSubtitulo.setText("Acompanhe vendas, pedidos e inventário em tempo real."); lblEstadoBd.setText("Estado da base de dados");
        lblProdutos.setText("Produtos activos"); lblProdutosValor.setText("0"); lblPedidos.setText("Pedidos registados"); lblPedidosValor.setText("0"); lblFaturacao.setText("Faturação"); lblFaturacaoValor.setText("0,00 MT"); lblStock.setText("Alertas de stock"); lblStockValor.setText("0"); lblAtalhos.setText("Atalhos rápidos"); btnVendas.setText("Nova venda"); btnPedidos.setText("Ver pedidos"); btnProdutos.setText("Gerir produtos"); btnStock.setText("Inventário");
        javax.swing.GroupLayout produtosLayout = new javax.swing.GroupLayout(pnlProdutos); pnlProdutos.setLayout(produtosLayout); produtosLayout.setHorizontalGroup(produtosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblProdutos).addComponent(lblProdutosValor)); produtosLayout.setVerticalGroup(produtosLayout.createSequentialGroup().addComponent(lblProdutos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblProdutosValor));
        javax.swing.GroupLayout pedidosLayout = new javax.swing.GroupLayout(pnlPedidos); pnlPedidos.setLayout(pedidosLayout); pedidosLayout.setHorizontalGroup(pedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblPedidos).addComponent(lblPedidosValor)); pedidosLayout.setVerticalGroup(pedidosLayout.createSequentialGroup().addComponent(lblPedidos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblPedidosValor));
        javax.swing.GroupLayout faturacaoLayout = new javax.swing.GroupLayout(pnlFaturacao); pnlFaturacao.setLayout(faturacaoLayout); faturacaoLayout.setHorizontalGroup(faturacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblFaturacao).addComponent(lblFaturacaoValor)); faturacaoLayout.setVerticalGroup(faturacaoLayout.createSequentialGroup().addComponent(lblFaturacao).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblFaturacaoValor));
        javax.swing.GroupLayout stockLayout = new javax.swing.GroupLayout(pnlStock); pnlStock.setLayout(stockLayout); stockLayout.setHorizontalGroup(stockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblStock).addComponent(lblStockValor)); stockLayout.setVerticalGroup(stockLayout.createSequentialGroup().addComponent(lblStock).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblStockValor));
        javax.swing.GroupLayout atalhosLayout = new javax.swing.GroupLayout(pnlAtalhos); pnlAtalhos.setLayout(atalhosLayout); atalhosLayout.setHorizontalGroup(atalhosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblAtalhos).addGroup(atalhosLayout.createSequentialGroup().addComponent(btnVendas).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnPedidos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnProdutos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnStock))); atalhosLayout.setVerticalGroup(atalhosLayout.createSequentialGroup().addComponent(lblAtalhos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(atalhosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnVendas).addComponent(btnPedidos).addComponent(btnProdutos).addComponent(btnStock)));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this); setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblTitulo).addComponent(lblSubtitulo)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE).addComponent(lblEstadoBd)).addGroup(layout.createSequentialGroup().addComponent(pnlProdutos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(12,12,12).addComponent(pnlPedidos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(12,12,12).addComponent(pnlFaturacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(12,12,12).addComponent(pnlStock, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addComponent(pnlAtalhos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblTitulo).addComponent(lblEstadoBd)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblSubtitulo).addGap(18,18,18).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnlProdutos).addComponent(pnlPedidos).addComponent(pnlFaturacao).addComponent(pnlStock)).addGap(18,18,18).addComponent(pnlAtalhos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(250, Short.MAX_VALUE));
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPedidos, btnProdutos, btnStock, btnVendas;
    private javax.swing.JLabel lblAtalhos, lblEstadoBd, lblFaturacao, lblFaturacaoValor, lblPedidos, lblPedidosValor, lblProdutos, lblProdutosValor, lblStock, lblStockValor, lblSubtitulo, lblTitulo;
    private javax.swing.JPanel pnlAtalhos, pnlFaturacao, pnlPedidos, pnlProdutos, pnlStock;
    // End of variables declaration//GEN-END:variables
}
