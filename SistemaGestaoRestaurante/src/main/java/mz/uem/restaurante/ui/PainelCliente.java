package mz.uem.restaurante.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import mz.uem.restaurante.model.*;
import mz.uem.restaurante.service.DadosSistema;

public class PainelCliente extends JPanel {
    private DadosSistema dados; private Pedido pedido;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Produto", "Categoria", "Preço (MT)", "Disponível"}, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
    public PainelCliente(){ initComponents(); configurarEventos(); }
    public void setDados(DadosSistema d){ dados=d; atualizarCardapio(); }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        lblQtd = new javax.swing.JLabel();
        spnQtd = new javax.swing.JSpinner();
        lblTotal = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        btnAdicionar = new javax.swing.JButton();
        btnCheckout = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20));
        lblTitulo.setText("MODO CLIENTE — PEDIDOS");
        lblCliente.setText("Cliente:");
        txtCliente.setText("Cliente");
        lblQtd.setText("Quantidade:");
        spnQtd.setModel(new javax.swing.SpinnerNumberModel(1, 1, 50, 1));
        lblTotal.setText("Total: 0,00 MT");
        tabela.setModel(modelo);
        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll.setViewportView(tabela);
        btnAdicionar.setText("Adicionar");
        btnCheckout.setText("Finalizar pedido");
        btnLimpar.setText("Limpar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(txtCliente)
                    .addComponent(lblQtd)
                    .addComponent(spnQtd)
                    .addComponent(lblTotal)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scroll)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionar)
                    .addComponent(btnCheckout)
                    .addComponent(btnLimpar)
                )
        );
    }//GEN-END:initComponents

    // </editor-fold>
    private void configurarEventos(){ btnAdicionar.addActionListener(e->adicionar()); btnCheckout.addActionListener(e->checkout()); btnLimpar.addActionListener(e->{pedido=null;txtCliente.setText("");lblTotal.setText("Total atual: 0,00 MT");}); }
    private void adicionar(){ if(dados==null)return; int r=tabela.getSelectedRow(); if(r<0){JOptionPane.showMessageDialog(this,"Selecione um produto.");return;} if(txtCliente.getText().isBlank()){JOptionPane.showMessageDialog(this,"Informe o nome do cliente.");return;} Produto p=dados.produtoService.buscar((int)modelo.getValueAt(r,0)); if(!p.isDisponivel()){JOptionPane.showMessageDialog(this,"Produto indisponível.");return;} if(pedido==null){Cliente c=new Cliente(100,txtCliente.getText().trim(),txtCliente.getText().trim().toLowerCase().replace(" ",""),"","");pedido=dados.pedidoService.criarPedido(c);} pedido.adicionarItem(p,(int)spnQtd.getValue()); lblTotal.setText(String.format("Total atual: %.2f MT",pedido.getTotal())); }
    private void checkout(){ if(pedido==null||pedido.getItens().isEmpty()){JOptionPane.showMessageDialog(this,"O pedido está vazio.");return;} String[] op={"Dinheiro","Cartão","M-Pesa"}; String esc=(String)JOptionPane.showInputDialog(this,"Forma de pagamento:","Checkout",JOptionPane.QUESTION_MESSAGE,null,op,op[0]); if(esc==null)return; Pagamento pag=switch(esc){case "Cartão"->new PagamentoCartao();case "M-Pesa"->new PagamentoMPesa();default->new PagamentoDinheiro();}; if(pag.pagar(pedido.getTotal())){JOptionPane.showMessageDialog(this,"Pedido #"+pedido.getId()+" confirmado!\nPagamento: "+pag.getDescricao()+"\nTotal: "+String.format("%.2f MT",pedido.getTotal()));pedido=null;txtCliente.setText("");lblTotal.setText("Total atual: 0,00 MT");} }
    public void atualizarCardapio(){if(dados==null)return;modelo.setRowCount(0);for(Produto p:dados.produtoService.listar())modelo.addRow(new Object[]{p.getId(),p.getNome(),p.getCategoria(),String.format("%.2f",p.getPreco()),p.isDisponivel()?"Sim":"Não"});}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAdicionar; private JButton btnCheckout; private JButton btnLimpar; private JLabel lblCliente; private JLabel lblQtd; private JLabel lblTitulo; private JLabel lblTotal; private JScrollPane scroll; private JSpinner spnQtd; private JTable tabela; private JTextField txtCliente;
    // End of variables declaration//GEN-END:variables
}
