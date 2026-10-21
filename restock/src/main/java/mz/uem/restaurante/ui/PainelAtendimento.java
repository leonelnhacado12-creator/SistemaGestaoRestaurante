package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import mz.uem.restaurante.model.MensagemAtendimento;
import mz.uem.restaurante.service.DadosSistema;

/** Caixa de entrada da equipa para responder às conversas de atendimento. */
public class PainelAtendimento extends JPanel {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Data/hora", "Cliente", "Telefone", "Assunto", "De", "Mensagem"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelAtendimento() { construir(); }
    public void setDados(DadosSistema dados) { this.dados = dados; atualizarTabela(); }

    private void construir() {
        setLayout(new BorderLayout(0, 14)); setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel topo = new JPanel(new GridLayout(2, 1)); topo.setOpaque(false);
        topo.add(Ui.titulo("Atendimento ao cliente")); topo.add(new JLabel("Reclamações, dúvidas e feedback enviados pelo cliente."));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabela);
        JScrollPane scroll = new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); acoes.setOpaque(false);
        JButton responder = Ui.botao("Responder", Ui.DESTAQUE), actualizar = Ui.botaoSecundario("Actualizar");
        acoes.add(responder); acoes.add(actualizar); responder.addActionListener(e -> responder()); actualizar.addActionListener(e -> atualizarTabela());
        add(topo, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH);
    }

    private void responder() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Seleccione uma mensagem do cliente.", "Atendimento", JOptionPane.WARNING_MESSAGE); return; }
        MensagemAtendimento contexto = dados.atendimentoService.listar().stream().filter(m -> m.getId() == (long) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0)).findFirst().orElse(null);
        if (contexto == null) return;
        JTextArea texto = new JTextArea(5, 36); texto.setLineWrap(true); texto.setWrapStyleWord(true);
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(texto), "Responder a " + contexto.getCliente(), JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { dados.atendimentoService.responder(contexto, texto.getText()); atualizarTabela(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível responder", JOptionPane.ERROR_MESSAGE); }
    }

    public void atualizarTabela() {
        if (dados == null) return;
        modelo.setRowCount(0);
        for (MensagemAtendimento mensagem : dados.atendimentoService.listar()) modelo.addRow(new Object[]{mensagem.getId(), mensagem.getDataHora().format(FORMATO), mensagem.getCliente(), mensagem.getTelefone(), mensagem.getTipo(), mensagem.getRemetente(), mensagem.getMensagem()});
        if (tabela.getColumnModel().getColumnCount() > 0) tabela.getColumnModel().getColumn(0).setMinWidth(0);
    }
}
