package mz.uem.restaurante.ui;

import java.awt.*;import javax.swing.*;import javax.swing.table.DefaultTableModel;import mz.uem.restaurante.model.Cliente;import mz.uem.restaurante.service.DadosSistema;

public class PainelGestaoClientes extends JPanel {
 private DadosSistema dados;private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"ID","Nome","Telefone","Utilizador"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
 public PainelGestaoClientes(){initComponents();btnNovo.addActionListener(e->formulario(0));btnEditar.addActionListener(e->editar());btnRemover.addActionListener(e->remover());btnPesquisar.addActionListener(e->atualizarTabela());btnAtualizar.addActionListener(e->{txtPesquisa.setText("");atualizarTabela();});}
 public void setDados(DadosSistema d){dados=d;atualizarTabela();}
 // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
 private void initComponents(){lblTitulo=new JLabel("GESTÃO DE CLIENTES — CRUD");lblTitulo.setFont(new Font("SansSerif",Font.BOLD,20));txtPesquisa=new JTextField(18);btnPesquisar=Ui.botaoSecundario("Pesquisar");scroll=new JScrollPane();tabela=new JTable(modelo);tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);Ui.prepararTabela(tabela);scroll.setViewportView(tabela);btnNovo=Ui.botao("Novo",Ui.DESTAQUE);btnEditar=Ui.botaoSecundario("Editar");btnRemover=Ui.botao("Remover",Ui.ALERTA);btnAtualizar=Ui.botaoSecundario("Actualizar");
 GroupLayout layout=new GroupLayout(this);setLayout(layout);layout.setAutoCreateGaps(true);layout.setAutoCreateContainerGaps(true);
 layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lblTitulo).addGroup(layout.createSequentialGroup().addComponent(txtPesquisa).addComponent(btnPesquisar)).addComponent(scroll).addGroup(layout.createSequentialGroup().addComponent(btnNovo).addComponent(btnEditar).addComponent(btnRemover).addComponent(btnAtualizar)));
 layout.setVerticalGroup(layout.createSequentialGroup().addComponent(lblTitulo).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(txtPesquisa).addComponent(btnPesquisar)).addComponent(scroll,0,300,Short.MAX_VALUE).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(btnNovo).addComponent(btnEditar).addComponent(btnRemover).addComponent(btnAtualizar)));}
 //GEN-END:initComponents
 private void editar(){int r=tabela.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(this,"Seleccione um cliente.");return;}formulario((int)modelo.getValueAt(r,0));}
 private void remover(){int r=tabela.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(this,"Seleccione um cliente.");return;}if(JOptionPane.showConfirmDialog(this,"Remover cliente seleccionado?","Confirmação",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){try{dados.clienteService.remover((int)modelo.getValueAt(r,0));atualizarTabela();}catch(Exception e){erro(e);}}}
 private void formulario(int id){JTextField nome=new JTextField(),telefone=new JTextField();if(id>0){Cliente c=dados.clienteService.buscar(id);if(c==null)return;nome.setText(c.getNome());telefone.setText(c.getTelefone());}JPanel p=new JPanel(new GridLayout(2,2,8,8));p.add(new JLabel("Nome:"));p.add(nome);p.add(new JLabel("Telefone:"));p.add(telefone);if(JOptionPane.showConfirmDialog(this,p,id>0?"Editar cliente":"Novo cliente",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){if(nome.getText().isBlank()){JOptionPane.showMessageDialog(this,"O nome é obrigatório.");return;}try{dados.clienteService.guardar(id,nome.getText().trim(),telefone.getText().trim());atualizarTabela();}catch(Exception e){erro(e);}}}
 public void atualizarTabela(){if(dados==null)return;modelo.setRowCount(0);for(Cliente c:dados.clienteService.listar(txtPesquisa.getText()))modelo.addRow(new Object[]{c.getId(),c.getNome(),c.getTelefone(),c.getUsername()});}
 private void erro(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Erro",JOptionPane.ERROR_MESSAGE);}
 // Variables declaration - do not modify//GEN-BEGIN:variables
 private JButton btnAtualizar,btnEditar,btnNovo,btnPesquisar,btnRemover;private JLabel lblTitulo;private JScrollPane scroll;private JTable tabela;private JTextField txtPesquisa;
 // End of variables declaration//GEN-END:variables
}
