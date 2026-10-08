package mz.uem.restaurante.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import mz.uem.restaurante.model.*;
import mz.uem.restaurante.service.DadosSistema;

public class PainelPedidos extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Cliente", "Data/Hora", "Total (MT)", "Estado"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    public PainelPedidos() { initComponents(); configurarEventos(); }
    public void setDados(DadosSistema d) { dados = d; atualizarTabela(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        btnAvancar = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20));
        lblTitulo.setText("GESTÃO DE PEDIDOS");
        tabela.setModel(modelo);
        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll.setViewportView(tabela);
        btnAvancar.setText("Avançar estado");
        btnAtualizar.setText("Atualizar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnAvancar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scroll)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAvancar)
                    .addComponent(btnAtualizar)
                )
        );
    }//GEN-END:initComponents


    private void configurarEventos() { btnAvancar.addActionListener(e -> avancar()); btnAtualizar.addActionListener(e -> atualizarTabela()); }
    private void avancar() {
        int r = tabela.getSelectedRow(); if (r < 0) { JOptionPane.showMessageDialog(this, "Selecione um pedido."); return; }
        int id = (int) modelo.getValueAt(r, 0); Pedido p = dados.pedidoService.buscar(id);
        StatusPedido novo = switch (p.getStatus()) { case PENDENTE -> StatusPedido.EM_PREPARACAO; case EM_PREPARACAO -> StatusPedido.PRONTO; case PRONTO -> StatusPedido.ENTREGUE; case ENTREGUE -> StatusPedido.ENTREGUE; };
        dados.pedidoService.atualizarStatus(id, novo); atualizarTabela();
    }
    public void atualizarTabela() {
        if (dados == null) return; modelo.setRowCount(0);
        for (Pedido p : dados.pedidoService.listar()) modelo.addRow(new Object[]{p.getId(), p.getCliente().getNome(), p.getDataHora().toString().replace("T", " "), String.format("%.2f", p.getTotal()), p.getStatus()});
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAtualizar; private JButton btnAvancar; private JLabel lblTitulo; private JScrollPane scroll; private JTable tabela;
    // End of variables declaration//GEN-END:variables
}
