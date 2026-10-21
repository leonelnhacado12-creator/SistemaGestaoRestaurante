package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import mz.uem.restaurante.model.EstadoMesa;
import mz.uem.restaurante.model.Mesa;
import mz.uem.restaurante.service.DadosSistema;

/** Cadastro persistente de mesas, usado pelo fluxo de reservas. */
public class PainelMesas extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nº", "Lugares", "Estado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelMesas() { construir(); }
    public void setDados(DadosSistema dados) { this.dados = dados; atualizarTabela(); }

    private void construir() {
        setLayout(new BorderLayout(0, 14)); setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel topo = new JPanel(new BorderLayout(8, 8)); topo.setOpaque(false);
        topo.add(Ui.titulo("Gestão de mesas"), BorderLayout.WEST);
        topo.add(new JLabel("As mesas reservadas só podem ser usadas fora do horário já agendado."), BorderLayout.SOUTH);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabela);
        JScrollPane scroll = new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); acoes.setOpaque(false);
        JButton nova = Ui.botao("Nova mesa", Ui.DESTAQUE);
        JButton ocupar = Ui.botaoSecundario("Marcar ocupada");
        JButton libertar = Ui.botaoSecundario("Libertar");
        JButton actualizar = Ui.botaoSecundario("Actualizar");
        acoes.add(nova); acoes.add(ocupar); acoes.add(libertar); acoes.add(actualizar);
        nova.addActionListener(e -> nova()); ocupar.addActionListener(e -> marcar(EstadoMesa.OCUPADA));
        libertar.addActionListener(e -> marcar(EstadoMesa.LIVRE)); actualizar.addActionListener(e -> atualizarTabela());
        add(topo, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH);
    }

    private void nova() {
        JTextField numero = new JTextField(), lugares = new JTextField();
        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));
        formulario.add(new JLabel("Número:")); formulario.add(numero);
        formulario.add(new JLabel("Lugares:")); formulario.add(lugares);
        if (JOptionPane.showConfirmDialog(this, formulario, "Nova mesa", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            dados.reservaService.guardarMesa(Integer.parseInt(numero.getText().trim()), Integer.parseInt(lugares.getText().trim()));
            atualizarTabela();
        } catch (Exception ex) { erro(ex); }
    }

    private void marcar(EstadoMesa estado) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { aviso("Seleccione uma mesa."); return; }
        try {
            dados.reservaService.atualizarEstadoMesa((int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0), estado);
            atualizarTabela();
        } catch (Exception ex) { erro(ex); }
    }

    public void atualizarTabela() {
        if (dados == null) return;
        modelo.setRowCount(0);
        for (Mesa mesa : dados.reservaService.listarMesas()) modelo.addRow(new Object[]{mesa.getId(), mesa.getNumero(), mesa.getLugares(), mesa.getEstado()});
        if (tabela.getColumnModel().getColumnCount() > 0) tabela.getColumnModel().getColumn(0).setMinWidth(0);
    }
    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
