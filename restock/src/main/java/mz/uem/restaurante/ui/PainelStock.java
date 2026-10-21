package mz.uem.restaurante.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import mz.uem.restaurante.model.ItemStock;
import mz.uem.restaurante.model.MovimentoStock;
import mz.uem.restaurante.service.DadosSistema;

/** Formulário de inventário; os componentes principais estão no initComponents para o GUI Builder. */
public class PainelStock extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID","Nome","Categoria","Unidade","Quantidade","Stock mínimo","Estado"},0) {
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    public PainelStock(){initComponents(); configurarEventos();}
    public void setDados(DadosSistema d){dados=d; atualizarTabela();}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblPesquisa = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        btnPesquisar = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        btnNovo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnEntrada = new javax.swing.JButton();
        btnSaida = new javax.swing.JButton();
        btnMovimentos = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        lblLegenda = new javax.swing.JLabel();
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20));
        lblTitulo.setText("GESTÃO DE STOCK E INVENTÁRIO");
        lblPesquisa.setText("Pesquisar:");
        txtPesquisa.setToolTipText("Nome ou categoria");
        btnPesquisar.setText("Pesquisar");
        tabela.setModel(modelo);
        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        Ui.prepararTabela(tabela);
        scroll.setViewportView(tabela);
        btnNovo.setText("Novo item"); btnEditar.setText("Editar"); btnEliminar.setText("Eliminar");
        btnEntrada.setText("Repor stock"); btnSaida.setText("Registar saída"); btnMovimentos.setText("Histórico"); btnAtualizar.setText("Atualizar");
        lblLegenda.setText("Estado: STOCK BAIXO quando a quantidade for igual ou inferior ao mínimo definido.");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this); setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitulo)
            .addGroup(layout.createSequentialGroup().addComponent(lblPesquisa).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnPesquisar))
            .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup().addComponent(btnNovo).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnEditar).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnEliminar).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnEntrada).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnSaida).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnMovimentos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnAtualizar))
            .addComponent(lblLegenda)).addContainerGap()));
        layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap().addComponent(lblTitulo).addGap(12)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblPesquisa).addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnPesquisar))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnNovo).addComponent(btnEditar).addComponent(btnEliminar).addComponent(btnEntrada).addComponent(btnSaida).addComponent(btnMovimentos).addComponent(btnAtualizar))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblLegenda).addContainerGap());
    }//GEN-END:initComponents

    private void configurarEventos(){btnNovo.addActionListener(e->novo());btnEditar.addActionListener(e->editar());btnEliminar.addActionListener(e->eliminar());btnEntrada.addActionListener(e->movimentar(true));btnSaida.addActionListener(e->movimentar(false));btnAtualizar.addActionListener(e->atualizarTabela());btnPesquisar.addActionListener(e->atualizarTabela());txtPesquisa.addActionListener(e->atualizarTabela());btnMovimentos.addActionListener(e->verMovimentos());}
    private void novo(){if(dados==null)return; JTextField nome=new JTextField(),cat=new JTextField(),unidade=new JTextField("un"),qtd=new JTextField("0"),min=new JTextField("0"); JPanel p=form(nome,cat,unidade,qtd,min);if(JOptionPane.showConfirmDialog(this,p,"Novo item de stock",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){try{dados.stockService.guardar(new ItemStock(0,nome.getText().trim(),cat.getText().trim(),unidade.getText().trim(),numero(qtd),numero(min)));dados.auditoriaService.registar("gerente","CRIAR","stock",nome.getText().trim());atualizarTabela();}catch(Exception ex){erro(ex);}}}
    private void editar(){int r=tabela.getSelectedRow();if(r<0){mensagem("Seleccione um item.");return;}int id=(int)modelo.getValueAt(tabela.convertRowIndexToModel(r),0);ItemStock x=encontrar(id);if(x==null)return;JTextField nome=new JTextField(x.getNome()),cat=new JTextField(x.getCategoria()),unidade=new JTextField(x.getUnidade()),qtd=new JTextField(String.valueOf(x.getQuantidade())),min=new JTextField(String.valueOf(x.getStockMinimo()));if(JOptionPane.showConfirmDialog(this,form(nome,cat,unidade,qtd,min),"Editar item",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){try{x.setNome(nome.getText().trim());x.setCategoria(cat.getText().trim());x.setUnidade(unidade.getText().trim());x.setQuantidade(numero(qtd));x.setStockMinimo(numero(min));dados.stockService.guardar(x);atualizarTabela();}catch(Exception ex){erro(ex);}}}
    private JPanel form(JTextField n,JTextField c,JTextField u,JTextField q,JTextField m){JPanel p=new JPanel(new GridLayout(5,2,8,8));p.add(new JLabel("Nome do item:"));p.add(n);p.add(new JLabel("Categoria:"));p.add(c);p.add(new JLabel("Unidade (kg, L, un.):"));p.add(u);p.add(new JLabel("Quantidade actual:"));p.add(q);p.add(new JLabel("Stock mínimo:"));p.add(m);return p;}
    private void eliminar(){int r=tabela.getSelectedRow();if(r<0){mensagem("Seleccione um item.");return;}int id=(int)modelo.getValueAt(tabela.convertRowIndexToModel(r),0);if(JOptionPane.showConfirmDialog(this,"Eliminar o item seleccionado? O histórico impede eliminar itens com movimentos.","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){try{dados.stockService.eliminar(id);atualizarTabela();}catch(Exception ex){erro(ex);}}}
    private void movimentar(boolean entrada){int r=tabela.getSelectedRow();if(r<0){mensagem("Seleccione um item.");return;}int id=(int)modelo.getValueAt(tabela.convertRowIndexToModel(r),0);JTextField qtd=new JTextField();JTextField motivo=new JTextField();JPanel p=new JPanel(new GridLayout(2,2,8,8));p.add(new JLabel("Quantidade:"));p.add(qtd);p.add(new JLabel("Motivo:"));p.add(motivo);if(JOptionPane.showConfirmDialog(this,p,entrada?"Reposição de stock":"Registar saída de stock",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){try{double q=numero(qtd);if(q<=0)throw new IllegalArgumentException("A quantidade deve ser maior que zero.");dados.stockService.movimentar(id,q,entrada,motivo.getText().trim(),"gerente");dados.auditoriaService.registar("gerente",entrada?"ENTRADA_STOCK":"SAIDA_STOCK","stock","Item #"+id+": "+q);atualizarTabela();}catch(Exception ex){erro(ex);}}}
    private void verMovimentos(){try{List<MovimentoStock> lista=dados.stockService.listarMovimentos();DefaultTableModel m=new DefaultTableModel(new Object[]{"ID","Item","Tipo","Quantidade","Saldo depois","Motivo","Utilizador","Data"},0){public boolean isCellEditable(int r,int c){return false;}};for(MovimentoStock x:lista)m.addRow(new Object[]{x.getId(),x.getItemNome(),x.getTipo(),x.getQuantidade(),x.getSaldoDepois(),x.getMotivo(),x.getUtilizador(),x.getData()});JTable t=new JTable(m);t.setAutoCreateRowSorter(true);JScrollPane sp=new JScrollPane(t);sp.setPreferredSize(new Dimension(850,350));JOptionPane.showMessageDialog(this,sp,"Histórico de movimentos (últimos 200)",JOptionPane.INFORMATION_MESSAGE);}catch(Exception ex){erro(ex);}}
    private ItemStock encontrar(int id){return dados.stockService.listar("").stream().filter(x->x.getId()==id).findFirst().orElse(null);}
    private double numero(JTextField f){return Double.parseDouble(f.getText().trim().replace(',','.'));}
    public void atualizarTabela(){if(dados==null)return;try{modelo.setRowCount(0);for(ItemStock x:dados.stockService.listar(txtPesquisa.getText()))modelo.addRow(new Object[]{x.getId(),x.getNome(),x.getCategoria(),x.getUnidade(),x.getQuantidade(),x.getStockMinimo(),x.isStockBaixo()?"STOCK BAIXO":"Normal"});}catch(Exception ex){erro(ex);}}
    private void erro(Exception e){JOptionPane.showMessageDialog(this,e.getMessage()==null?"Ocorreu um erro.":e.getMessage(),"Erro",JOptionPane.ERROR_MESSAGE);}private void mensagem(String s){JOptionPane.showMessageDialog(this,s);}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar; private javax.swing.JButton btnEditar; private javax.swing.JButton btnEliminar; private javax.swing.JButton btnEntrada; private javax.swing.JButton btnMovimentos; private javax.swing.JButton btnNovo; private javax.swing.JButton btnPesquisar; private javax.swing.JButton btnSaida; private javax.swing.JLabel lblLegenda; private javax.swing.JLabel lblPesquisa; private javax.swing.JLabel lblTitulo; private javax.swing.JScrollPane scroll; private javax.swing.JTable tabela; private javax.swing.JTextField txtPesquisa;
    // End of variables declaration//GEN-END:variables
}
