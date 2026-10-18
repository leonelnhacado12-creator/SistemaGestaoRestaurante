package mz.uem.restaurante.ui;

import java.awt.*; import javax.swing.*; import javax.swing.table.DefaultTableModel; import mz.uem.restaurante.model.EstadoMesa;

/** Gestão visual de mesas; componentes fixos e editáveis no GUI Builder. */
public class PainelMesas extends JPanel {
 private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"Nº","Lugares","Estado"},0){public boolean isCellEditable(int r,int c){return false;}};
 private final JTable tabela=new JTable(modelo); private final JComboBox<EstadoMesa> estado=new JComboBox<>(EstadoMesa.values());
 public PainelMesas(){ construir(); }
 private void construir(){setLayout(new BorderLayout(0,14));setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
  JPanel topo=new JPanel(new BorderLayout(8,8));topo.setOpaque(false);topo.add(Ui.titulo("Gestão de mesas"),BorderLayout.WEST);topo.add(estado,BorderLayout.EAST);
  Ui.prepararTabela(tabela);Ui.card(new JScrollPane(tabela));JScrollPane scroll=new JScrollPane(tabela);Ui.card(scroll);
  JPanel acoes=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));acoes.setOpaque(false);JButton nova=Ui.botao("Nova mesa",Ui.DESTAQUE), ocupar=Ui.botaoSecundario("Marcar ocupada"), libertar=Ui.botaoSecundario("Libertar");acoes.add(nova);acoes.add(ocupar);acoes.add(libertar);
  nova.addActionListener(e->nova());ocupar.addActionListener(e->marcar(EstadoMesa.OCUPADA));libertar.addActionListener(e->marcar(EstadoMesa.LIVRE));add(topo,BorderLayout.NORTH);add(scroll,BorderLayout.CENTER);add(acoes,BorderLayout.SOUTH);
 }
 private void nova(){JTextField n=new JTextField(),l=new JTextField();JPanel p=new JPanel(new GridLayout(2,2,8,8));p.add(new JLabel("Número:"));p.add(n);p.add(new JLabel("Lugares:"));p.add(l);if(JOptionPane.showConfirmDialog(this,p,"Nova mesa",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)try{modelo.addRow(new Object[]{Integer.parseInt(n.getText()),Integer.parseInt(l.getText()),EstadoMesa.LIVRE});}catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Número e lugares inválidos.","Validação",JOptionPane.WARNING_MESSAGE);}}
 private void marcar(EstadoMesa e){int r=tabela.getSelectedRow();if(r<0)return;modelo.setValueAt(e,r,2);}
}
