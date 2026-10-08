package mz.uem.restaurante.ui;
import javax.swing.*; import javax.swing.table.DefaultTableModel; import mz.uem.restaurante.model.Promocao; import mz.uem.restaurante.service.DadosSistema;
public class PainelPromocoes extends JPanel {
 private DadosSistema dados; private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"ID","Nome","Desconto (%)","Ativa"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
 public PainelPromocoes(){initComponents();configurarEventos();} public void setDados(DadosSistema d){dados=d;atualizarTabela();}
     // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        btnNovo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20));
        lblTitulo.setText("GESTÃO DE PROMOÇÕES — CRUD");
        tabela.setModel(modelo);
        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll.setViewportView(tabela);
        btnNovo.setText("Novo");
        btnEditar.setText("Editar");
        btnRemover.setText("Remover");
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
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(btnNovo)
                    .addComponent(btnEditar)
                    .addComponent(btnRemover)
                    .addComponent(btnAtualizar)
                )
        );
    }//GEN-END:initComponents

 // </editor-fold>
 private void configurarEventos(){btnNovo.addActionListener(e->formulario(-1));btnEditar.addActionListener(e->editar());btnRemover.addActionListener(e->remover());btnAtualizar.addActionListener(e->atualizarTabela());}
 private void editar(){int r=tabela.getSelectedRow();if(r>=0)formulario((int)modelo.getValueAt(r,0));else JOptionPane.showMessageDialog(this,"Selecione uma promoção.");}
 private void remover(){int r=tabela.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(this,"Selecione uma promoção.");return;}if(JOptionPane.showConfirmDialog(this,"Remover promoção?","Confirmação",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){dados.promocaoService.remover((int)modelo.getValueAt(r,0));atualizarTabela();}}
 private void formulario(int id){if(dados==null)return;JTextField nome=new JTextField(),desconto=new JTextField();JCheckBox ativa=new JCheckBox("Ativa",true);if(id>0){Promocao x=dados.promocaoService.buscar(id);nome.setText(x.getNome());desconto.setText(String.valueOf(x.getDescontoPercentual()));ativa.setSelected(x.isAtiva());}JPanel p=new JPanel(new java.awt.GridLayout(3,2,8,8));p.add(new JLabel("Nome:"));p.add(nome);p.add(new JLabel("Desconto (%):"));p.add(desconto);p.add(new JLabel("Estado:"));p.add(ativa);if(JOptionPane.showConfirmDialog(this,p,id>0?"Editar Promoção":"Nova Promoção",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){try{double v=Double.parseDouble(desconto.getText().replace(',','.'));if(nome.getText().isBlank()||v<0||v>100)throw new Exception();if(id<0)dados.promocaoService.criar(nome.getText().trim(),v,ativa.isSelected());else dados.promocaoService.atualizar(id,nome.getText().trim(),v,ativa.isSelected());atualizarTabela();}catch(Exception ex){JOptionPane.showMessageDialog(this,"Dados inválidos. O desconto deve estar entre 0 e 100.");}}}
 public void atualizarTabela(){if(dados==null)return;modelo.setRowCount(0);for(Promocao p:dados.promocaoService.listar())modelo.addRow(new Object[]{p.getId(),p.getNome(),String.format("%.1f",p.getDescontoPercentual()),p.isAtiva()?"Sim":"Não"});}
 // Variables declaration - do not modify//GEN-BEGIN:variables
 private JButton btnAtualizar;private JButton btnEditar;private JButton btnNovo;private JButton btnRemover;private JLabel lblTitulo;private JScrollPane scroll;private JTable tabela;
 // End of variables declaration//GEN-END:variables
}
