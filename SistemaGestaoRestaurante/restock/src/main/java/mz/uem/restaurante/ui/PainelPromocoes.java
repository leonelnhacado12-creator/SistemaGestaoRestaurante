package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mz.uem.restaurante.model.Promocao;
import mz.uem.restaurante.service.DadosSistema;

/** CRUD de promoções com pesquisa, filtro de estado e ordenação das colunas. */
public class PainelPromocoes extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Desconto (%)", "Estado"}, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    private final JTextField pesquisa = new JTextField(18);
    private final JComboBox<String> estado = new JComboBox<>(new String[]{"Todas", "Activa", "Inactiva"});
    private JButton novo, editar, remover, atualizar;
    private JScrollPane scroll;

    public PainelPromocoes() { initComponents(); }
    public void setDados(DadosSistema dados) { this.dados = dados; atualizarTabela(); }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { construir(); }
    // </editor-fold>//GEN-END:initComponents

    private void construir() {
        setLayout(new BorderLayout(0, 14)); setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel topo = new JPanel(new BorderLayout(0, 10)); topo.setOpaque(false);
        JPanel textos = new JPanel(new GridLayout(2, 1)); textos.setOpaque(false); textos.add(Ui.titulo("Promoções")); textos.add(new JLabel("Defina descontos e controle quais campanhas estão activas."));
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); filtro.setOpaque(false); filtro.add(new JLabel("Pesquisar:")); filtro.add(pesquisa); filtro.add(estado);
        topo.add(textos, BorderLayout.WEST); topo.add(filtro, BorderLayout.EAST);
        tabela.setRowSorter(sorter); tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabela);
        scroll = new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); acoes.setOpaque(false);
        novo = Ui.botao("Nova promoção", Ui.DESTAQUE); editar = Ui.botaoSecundario("Editar"); remover = Ui.botao("Remover", Ui.ALERTA); atualizar = Ui.botaoSecundario("Atualizar");
        acoes.add(novo); acoes.add(editar); acoes.add(remover); acoes.add(atualizar);
        add(topo, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH);
        pesquisa.getDocument().addDocumentListener(new SimplesDocumentListener(this::filtrar)); estado.addActionListener(e -> filtrar());
        novo.addActionListener(e -> formulario(0)); editar.addActionListener(e -> editar()); remover.addActionListener(e -> remover()); atualizar.addActionListener(e -> atualizarTabela());
    }

    private void filtrar() {
        String termo = pesquisa.getText().trim().toLowerCase(); String estadoEscolhido = (String) estado.getSelectedItem();
        sorter.setRowFilter(new RowFilter<>() { @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
            String nome = entry.getStringValue(1).toLowerCase(); String situacao = entry.getStringValue(3);
            return (termo.isEmpty() || nome.contains(termo)) && ("Todas".equals(estadoEscolhido) || estadoEscolhido.equals(situacao));
        }});
    }

    private void editar() { int linha = tabela.getSelectedRow(); if (linha < 0) { aviso("Seleccione uma promoção."); return; } formulario((int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0)); }
    private void remover() {
        int linha = tabela.getSelectedRow(); if (linha < 0) { aviso("Seleccione uma promoção."); return; }
        int id = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        if (JOptionPane.showConfirmDialog(this, "Remover a promoção seleccionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dados.promocaoService.remover(id); atualizarTabela(); } catch (RuntimeException ex) { erro(ex); }
    }

    private void formulario(int id) {
        Promocao promocao = id > 0 ? dados.promocaoService.buscar(id) : null;
        JTextField nome = new JTextField(promocao == null ? "" : promocao.getNome()); JTextField desconto = new JTextField(promocao == null ? "" : String.valueOf(promocao.getDescontoPercentual())); JCheckBox activa = new JCheckBox("Activa", promocao == null || promocao.isAtiva());
        JPanel form = new JPanel(new GridLayout(3, 2, 9, 9)); form.add(new JLabel("Nome:")); form.add(nome); form.add(new JLabel("Desconto (%):")); form.add(desconto); form.add(new JLabel("Estado:")); form.add(activa);
        if (JOptionPane.showConfirmDialog(this, form, promocao == null ? "Nova promoção" : "Editar promoção", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { double valor = Double.parseDouble(desconto.getText().trim().replace(',', '.')); if (promocao == null) dados.promocaoService.criar(nome.getText(), valor, activa.isSelected()); else dados.promocaoService.atualizar(id, nome.getText(), valor, activa.isSelected()); atualizarTabela(); }
        catch (NumberFormatException ex) { aviso("Introduza um desconto numérico válido."); } catch (RuntimeException ex) { erro(ex); }
    }

    public void atualizarTabela() { if (dados == null) return; modelo.setRowCount(0); for (Promocao promocao : dados.promocaoService.listar()) modelo.addRow(new Object[]{promocao.getId(), promocao.getNome(), promocao.getDescontoPercentual(), promocao.isAtiva() ? "Activa" : "Inactiva"}); filtrar(); }
    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
