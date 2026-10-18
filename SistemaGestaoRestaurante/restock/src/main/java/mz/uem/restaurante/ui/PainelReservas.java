package mz.uem.restaurante.ui;

import java.awt.*; import javax.swing.*; import javax.swing.table.DefaultTableModel;

/** Agenda de reservas com pesquisa e estados. */
public class PainelReservas extends JPanel {
 private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"Cliente","Telefone","Mesa","Data/Hora","Pessoas","Estado"},0){public boolean isCellEditable(int r,int c){return false;}};
 private final JTable tabela=new JTable(modelo); private final JTextField pesquisa=new JTextField(18);
 public PainelReservas(){setLayout(new BorderLayout(0,14));setBorder(BorderFactory.createEmptyBorder(20,24,20,24));JPanel topo=new JPanel(new BorderLayout(8,8));topo.setOpaque(false);topo.add(Ui.titulo("Reservas"),BorderLayout.WEST);topo.add(pesquisa,BorderLayout.EAST);Ui.prepararTabela(tabela);JScrollPane scroll=new JScrollPane(tabela);Ui.card(scroll);JPanel acoes=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));acoes.setOpaque(false);JButton nova=Ui.botao("Nova reserva",Ui.DESTAQUE), confirmar=Ui.botaoSecundario("Confirmar"), cancelar=Ui.botao("Cancelar",Ui.ALERTA);acoes.add(nova);acoes.add(confirmar);acoes.add(cancelar);nova.addActionListener(e->nova());confirmar.addActionListener(e->estado("CONFIRMADA"));cancelar.addActionListener(e->estado("CANCELADA"));add(topo,BorderLayout.NORTH);add(scroll,BorderLayout.CENTER);add(acoes,BorderLayout.SOUTH);}
 private void nova(){JTextField c=new JTextField(),t=new JTextField(),m=new JTextField(),d=new JTextField(),p=new JTextField();JPanel f=new JPanel(new GridLayout(5,2,8,8));for(JComponent[] x:new JComponent[][]{{new JLabel("Cliente:"),c},{new JLabel("Telefone:"),t},{new JLabel("Mesa:"),m},{new JLabel("Data/hora:"),d},{new JLabel("Pessoas:"),p}}){f.add(x[0]);f.add(x[1]);}if(JOptionPane.showConfirmDialog(this,f,"Nova reserva",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)modelo.addRow(new Object[]{c.getText(),t.getText(),m.getText(),d.getText(),p.getText(),"PENDENTE"});}
 private void estado(String s){int r=tabela.getSelectedRow();if(r>=0)modelo.setValueAt(s,r,5);}
}
