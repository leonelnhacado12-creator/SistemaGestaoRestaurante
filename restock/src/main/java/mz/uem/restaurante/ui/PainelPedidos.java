package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.StatusPedido;
import mz.uem.restaurante.model.EstadoPagamento;
import mz.uem.restaurante.report.ReciboPdfService;
import mz.uem.restaurante.service.DadosSistema;

/** Gestão de pedidos com pesquisa, filtro, ordenação e recibo PDF. */
public class PainelPedidos extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID","Cliente","Data/Hora","Subtotal","Desconto","Total MT","Pagamento","Estado pagamento","Estado pedido"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    private final JTextField pesquisa = new JTextField(16);
    private final JComboBox<String> estado = new JComboBox<>(new String[]{"Todos","PENDENTE","EM_PREPARACAO","PRONTO","ENTREGUE"});
    private JButton avancar, confirmarPagamento, recusarPagamento, pdf, atualizar;
    private JScrollPane scroll;
    private final java.util.Set<Integer> alertasPagamentoExibidos = new java.util.HashSet<>();
    private javax.swing.Timer temporizadorPagamentos;

    public PainelPedidos(){ initComponents(); }
    public void setDados(DadosSistema d){dados=d; atualizarTabela(); iniciarAlertasPagamento();}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents(){ construir(); }
    // </editor-fold>//GEN-END:initComponents
    private void construir(){
        setLayout(new BorderLayout(0,14)); setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        JPanel topo=new JPanel(new BorderLayout(0,10)); topo.setOpaque(false); JPanel textos=new JPanel(new GridLayout(2,1)); textos.setOpaque(false); textos.add(Ui.titulo("Pedidos")); textos.add(new JLabel("Pesquise, ordene e acompanhe cada estado.")); JPanel filtros=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); filtros.setOpaque(false); filtros.add(new JLabel("Pesquisar:")); filtros.add(pesquisa); filtros.add(new JLabel("Estado:")); filtros.add(estado); topo.add(textos,BorderLayout.WEST); topo.add(filtros,BorderLayout.EAST);
        tabela.setRowSorter(sorter); tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabela); scroll=new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); acoes.setOpaque(false); avancar=Ui.botao("Avançar estado",Ui.DESTAQUE); confirmarPagamento=Ui.botao("Confirmar pagamento",Ui.PRIMARIA); recusarPagamento=Ui.botao("Recusar pagamento",Ui.ALERTA); pdf=Ui.botaoSecundario("Gerar recibo PDF"); atualizar=Ui.botaoSecundario("Atualizar"); acoes.add(avancar); acoes.add(confirmarPagamento); acoes.add(recusarPagamento); acoes.add(pdf); acoes.add(atualizar);
        add(topo,BorderLayout.NORTH); add(scroll,BorderLayout.CENTER); add(acoes,BorderLayout.SOUTH); pesquisa.getDocument().addDocumentListener(new SimplesDocumentListener(this::filtrar)); estado.addActionListener(e->filtrar()); avancar.addActionListener(e->avancar()); confirmarPagamento.addActionListener(e->alterarPagamento(EstadoPagamento.CONFIRMADO)); recusarPagamento.addActionListener(e->alterarPagamento(EstadoPagamento.RECUSADO)); pdf.addActionListener(e->gerarPdf()); atualizar.addActionListener(e->atualizarTabela());
    }
    private void filtrar(){String termo=pesquisa.getText().trim().toLowerCase(); String escolha=(String)estado.getSelectedItem(); sorter.setRowFilter(new RowFilter<>(){@Override public boolean include(Entry<? extends DefaultTableModel,? extends Integer> e){String cliente=e.getStringValue(1).toLowerCase(),id=e.getStringValue(0),situacao=e.getStringValue(8);return (termo.isEmpty()||cliente.contains(termo)||id.contains(termo))&&("Todos".equals(escolha)||escolha.equals(situacao));}});}
    private Pedido selecionado(){int r=tabela.getSelectedRow();if(r<0){aviso("Seleccione um pedido.");return null;}return dados.pedidoService.buscar((int)modelo.getValueAt(tabela.convertRowIndexToModel(r),0));}
    private void avancar(){Pedido p=selecionado();if(p==null)return;StatusPedido n=switch(p.getStatus()){case PENDENTE->StatusPedido.EM_PREPARACAO;case EM_PREPARACAO->StatusPedido.PRONTO;case PRONTO->StatusPedido.ENTREGUE;case ENTREGUE->StatusPedido.ENTREGUE;};if(n==p.getStatus()){aviso("Este pedido já foi entregue.");return;}try{dados.pedidoService.atualizarStatus(p.getId(),n);atualizarTabela();}catch(RuntimeException e){erro(e);}}
    private void alterarPagamento(EstadoPagamento estadoPagamento){Pedido p=selecionado();if(p==null)return;try{dados.pedidoService.atualizarEstadoPagamento(p.getId(),estadoPagamento);atualizarTabela();JOptionPane.showMessageDialog(this,estadoPagamento==EstadoPagamento.CONFIRMADO?"Pagamento confirmado. A cozinha já pode preparar este pedido.":"Pagamento recusado. O cliente será avisado.","Pagamento actualizado",JOptionPane.INFORMATION_MESSAGE);}catch(RuntimeException e){erro(e);}}
    private void gerarPdf(){Pedido p=selecionado();if(p==null)return;JFileChooser fc=new JFileChooser();fc.setSelectedFile(new java.io.File(ReciboPdfService.nomeFicheiro(p)));if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;try{Path destino=fc.getSelectedFile().toPath();if(!destino.toString().toLowerCase().endsWith(".pdf"))destino=Path.of(destino+".pdf");ReciboPdfService.exportar(p,destino);JOptionPane.showMessageDialog(this,"Recibo PDF guardado em:\n"+destino);}catch(Exception e){erro(e);}}
    public void atualizarTabela(){if(dados==null)return;modelo.setRowCount(0);for(Pedido p:dados.pedidoService.listar())modelo.addRow(new Object[]{p.getId(),p.getCliente().getNome(),p.getDataHora().toString().replace('T',' '),p.getSubtotal(),p.getDesconto(),p.getTotal(),p.getFormaPagamento(),p.getEstadoPagamento().getDescricao(),p.getStatus().name()});filtrar();}
    private void iniciarAlertasPagamento(){if(temporizadorPagamentos!=null)temporizadorPagamentos.stop();temporizadorPagamentos=new javax.swing.Timer(10_000,e->avisarPagamentosPorValidar());temporizadorPagamentos.setInitialDelay(1_500);temporizadorPagamentos.start();}
    private void avisarPagamentosPorValidar(){if(dados==null||!isShowing())return;java.util.List<Pedido> novos=dados.pedidoService.listar().stream().filter(p->p.getEstadoPagamento()==EstadoPagamento.EM_VALIDACAO).filter(p->alertasPagamentoExibidos.add(p.getId())).toList();if(!novos.isEmpty()){Pedido primeiro=novos.get(0);JOptionPane.showMessageDialog(this,"Recebido pagamento de "+primeiro.getCliente().getNome()+" para o pedido #"+primeiro.getId()+".\nValide o comprovativo e clique em ‘Confirmar pagamento’."+(novos.size()>1?"\nHá mais "+(novos.size()-1)+" pagamento(s) pendente(s).":""),"Pagamento por validar",JOptionPane.INFORMATION_MESSAGE);atualizarTabela();}}
    private void aviso(String s){JOptionPane.showMessageDialog(this,s,"Atenção",JOptionPane.WARNING_MESSAGE);} private void erro(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Erro",JOptionPane.ERROR_MESSAGE);}
}
