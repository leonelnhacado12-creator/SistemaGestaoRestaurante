package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mz.uem.restaurante.model.EstadoReserva;
import mz.uem.restaurante.model.Mesa;
import mz.uem.restaurante.model.Reserva;
import mz.uem.restaurante.service.DadosSistema;

/** Agenda de reservas com validação de capacidade e bloqueio de horários sobrepostos. */
public class PainelReservas extends JPanel {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Cliente", "Telefone", "Mesa", "Data/Hora", "Pessoas", "Estado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    private final JTextField pesquisa = new JTextField(18);

    public PainelReservas() { construir(); }
    public void setDados(DadosSistema dados) { this.dados = dados; atualizarTabela(); }

    private void construir() {
        setLayout(new BorderLayout(0, 14)); setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel topo = new JPanel(new BorderLayout(8, 8)); topo.setOpaque(false);
        JPanel textos = new JPanel(new GridLayout(2, 1)); textos.setOpaque(false);
        textos.add(Ui.titulo("Reservas de mesa")); textos.add(new JLabel("Cada reserva bloqueia a mesa por duas horas e valida a capacidade."));
        topo.add(textos, BorderLayout.WEST); topo.add(pesquisa, BorderLayout.EAST);
        tabela.setRowSorter(sorter); tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabela);
        JScrollPane scroll = new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); acoes.setOpaque(false);
        JButton nova = Ui.botao("Nova reserva", Ui.DESTAQUE);
        JButton confirmar = Ui.botaoSecundario("Confirmar");
        JButton concluir = Ui.botaoSecundario("Concluir visita");
        JButton naoCompareceu = Ui.botao("Não compareceu", Ui.ALERTA);
        JButton cancelar = Ui.botao("Cancelar", Ui.ALERTA);
        JButton lembretes = Ui.botaoSecundario("Enviar lembretes (24h)");
        JButton actualizar = Ui.botaoSecundario("Actualizar");
        acoes.add(nova); acoes.add(confirmar); acoes.add(concluir); acoes.add(naoCompareceu); acoes.add(cancelar); acoes.add(lembretes); acoes.add(actualizar);
        nova.addActionListener(e -> nova()); confirmar.addActionListener(e -> alterarEstado(EstadoReserva.CONFIRMADA));
        concluir.addActionListener(e -> alterarEstado(EstadoReserva.CONCLUIDA)); naoCompareceu.addActionListener(e -> alterarEstado(EstadoReserva.NAO_COMPARECEU)); cancelar.addActionListener(e -> alterarEstado(EstadoReserva.CANCELADA)); lembretes.addActionListener(e -> enviarLembretes());
        actualizar.addActionListener(e -> atualizarTabela()); pesquisa.getDocument().addDocumentListener(new SimplesDocumentListener(this::filtrar));
        add(topo, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH);
    }

    private void nova() {
        if (dados == null) return;
        if (dados.reservaService.listarMesas().isEmpty()) { aviso("Cadastre uma mesa antes de criar uma reserva."); return; }
        JTextField cliente = new JTextField(), telefone = new JTextField();
        JTextField dataHora = new JTextField(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0).format(FORMATO));
        JTextField pessoas = new JTextField("2");
        JComboBox<Mesa> mesa = new JComboBox<>(dados.reservaService.listarMesas().toArray(Mesa[]::new));
        JButton disponibilidade = Ui.botaoSecundario("Ver mesas disponíveis");
        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 8));
        for (JComponent[] campo : new JComponent[][] {{new JLabel("Cliente:*"), cliente}, {new JLabel("Telefone:"), telefone}, {new JLabel("Mesa:*"), mesa}, {new JLabel("Data/hora (dd/MM/aaaa HH:mm):*"), dataHora}, {new JLabel("Pessoas:*"), pessoas}}) {
            formulario.add(campo[0]); formulario.add(campo[1]);
        }
        formulario.add(new JLabel("")); formulario.add(disponibilidade);
        disponibilidade.addActionListener(e -> { try { java.util.List<Mesa> disponiveis = dados.reservaService.listarMesasDisponiveis(LocalDateTime.parse(dataHora.getText().trim(), FORMATO), Integer.parseInt(pessoas.getText().trim())); mesa.removeAllItems(); disponiveis.forEach(mesa::addItem); if (disponiveis.isEmpty()) aviso("Não há mesas disponíveis nesse horário."); } catch (Exception ex) { aviso(ex.getMessage()); } });
        if (JOptionPane.showConfirmDialog(this, formulario, "Nova reserva", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Mesa seleccionada = (Mesa) mesa.getSelectedItem();
            if (seleccionada == null) { aviso("Consulte uma data/hora com mesas disponíveis antes de guardar."); return; }
            LocalDateTime inicio = LocalDateTime.parse(dataHora.getText().trim(), FORMATO);
            dados.reservaService.criarReserva(cliente.getText(), telefone.getText(), seleccionada.getId(), inicio, Integer.parseInt(pessoas.getText().trim()));
            dados.auditoriaService.registar("gerente", "CRIAR", "reserva", cliente.getText().trim());
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Reserva registada. O cliente poderá acompanhar a confirmação nas notificações.", "Reserva recebida", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) { aviso("Use o formato de data/hora dd/MM/aaaa HH:mm."); }
        catch (Exception ex) { erro(ex); }
    }

    private void alterarEstado(EstadoReserva estado) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { aviso("Seleccione uma reserva."); return; }
        try {
            int id = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
            dados.reservaService.atualizarEstadoReserva(id, estado); atualizarTabela();
            dados.auditoriaService.registar("gerente", "ALTERAR_ESTADO", "reserva", "Reserva #" + id + " -> " + estado);
        } catch (Exception ex) { erro(ex); }
    }

    private void enviarLembretes() {
        if (dados == null) return;
        java.util.List<Reserva> proximas = dados.reservaService.listarParaLembrete(24);
        for (Reserva reserva : proximas) {
            dados.notificacaoService.notificarLembreteReserva(reserva);
            dados.reservaService.marcarLembreteEnviado(reserva.getId());
        }
        dados.auditoriaService.registar("gerente", "ENVIAR_LEMBRETES", "reserva", proximas.size() + " reserva(s)");
        JOptionPane.showMessageDialog(this, proximas.isEmpty() ? "Não existem reservas confirmadas nas próximas 24 horas." : proximas.size() + " lembrete(s) foram criados nas notificações dos clientes.", "Lembretes", JOptionPane.INFORMATION_MESSAGE);
    }

    public void atualizarTabela() {
        if (dados == null) return;
        modelo.setRowCount(0);
        for (Reserva reserva : dados.reservaService.listarReservas()) modelo.addRow(new Object[]{reserva.getId(), reserva.getCliente(), reserva.getTelefone(), reserva.getMesa().getNumero(), reserva.getDataHora().format(FORMATO), reserva.getPessoas(), reserva.getEstado()});
        if (tabela.getColumnModel().getColumnCount() > 0) tabela.getColumnModel().getColumn(0).setMinWidth(0);
        filtrar();
    }

    private void filtrar() {
        String termo = pesquisa.getText().trim().toLowerCase();
        sorter.setRowFilter(termo.isBlank() ? null : new javax.swing.RowFilter<>() {
            @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> linha) {
                return linha.getStringValue(1).toLowerCase().contains(termo) || linha.getStringValue(2).contains(termo) || linha.getStringValue(3).contains(termo);
            }
        });
    }
    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
