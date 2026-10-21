package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mz.uem.restaurante.model.Mesa;
import mz.uem.restaurante.service.DadosSistema;

/** Formulário público: submete a reserva como pendente para confirmação pela equipa. */
final class DialogReservaCliente extends JDialog {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DadosSistema dados;
    private final JTextField cliente = new JTextField();
    private final JTextField telefone = new JTextField();
    private final JTextField dataHora = new JTextField();
    private final JTextField pessoas = new JTextField("2");
    private final JComboBox<Mesa> mesa;

    DialogReservaCliente(Component parent, DadosSistema dados, String nome, String contacto) {
        this.dados = dados;
        cliente.setText(nome == null ? "" : nome); telefone.setText(contacto == null ? "" : contacto);
        dataHora.setText(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0).format(FORMATO));
        mesa = new JComboBox<>(dados.reservaService.listarMesas().toArray(Mesa[]::new));
        construir(); actualizarMesasDisponiveis(); setLocationRelativeTo(parent);
    }

    private void construir() {
        setTitle("Reservar mesa"); setModal(true); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 8)); formulario.setBorder(BorderFactory.createEmptyBorder(16, 18, 10, 18));
        adicionar(formulario, "Nome:*", cliente); adicionar(formulario, "Telefone:", telefone); adicionar(formulario, "Mesa:*", mesa);
        adicionar(formulario, "Data/hora (dd/MM/aaaa HH:mm):*", dataHora); adicionar(formulario, "Pessoas:*", pessoas);
        JButton disponibilidade = Ui.botaoSecundario("Ver mesas disponíveis"); formulario.add(new JLabel("")); formulario.add(disponibilidade);
        JButton enviar = Ui.botao("Enviar reserva", Ui.DESTAQUE), cancelar = Ui.botaoSecundario("Cancelar"), minhas = Ui.botaoSecundario("Minhas reservas");
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT)); acoes.add(minhas); acoes.add(cancelar); acoes.add(enviar);
        disponibilidade.addActionListener(e -> actualizarMesasDisponiveis()); enviar.addActionListener(e -> enviar()); cancelar.addActionListener(e -> dispose()); minhas.addActionListener(e -> minhasReservas());
        add(formulario, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH); pack();
    }

    private void adicionar(JPanel painel, String texto, Component campo) { painel.add(new JLabel(texto)); painel.add(campo); }
    private void enviar() {
        try {
            Mesa seleccionada = (Mesa) mesa.getSelectedItem();
            if (seleccionada == null) throw new IllegalStateException("Não há mesa disponível para este horário. Altere os dados e consulte a disponibilidade.");
            dados.reservaService.criarReserva(cliente.getText(), telefone.getText(), seleccionada.getId(), LocalDateTime.parse(dataHora.getText().trim(), FORMATO), Integer.parseInt(pessoas.getText().trim()));
            JOptionPane.showMessageDialog(this, "Pedido de reserva recebido. A equipa irá confirmá-lo e a actualização ficará nas suas notificações.", "Reserva enviada", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "Use o formato dd/MM/aaaa HH:mm.", "Data/hora inválida", JOptionPane.WARNING_MESSAGE); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível reservar", JOptionPane.WARNING_MESSAGE); }
    }
    private void actualizarMesasDisponiveis() {
        try {
            LocalDateTime inicio = LocalDateTime.parse(dataHora.getText().trim(), FORMATO);
            int totalPessoas = Integer.parseInt(pessoas.getText().trim());
            java.util.List<Mesa> disponiveis = dados.reservaService.listarMesasDisponiveis(inicio, totalPessoas);
            mesa.removeAllItems(); disponiveis.forEach(mesa::addItem);
            if (disponiveis.isEmpty()) JOptionPane.showMessageDialog(this, "Não há mesas disponíveis para este horário e número de pessoas.", "Sem disponibilidade", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "Use o formato dd/MM/aaaa HH:mm.", "Data/hora inválida", JOptionPane.WARNING_MESSAGE); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Indique um número válido de pessoas.", "Pessoas inválido", JOptionPane.WARNING_MESSAGE); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível consultar", JOptionPane.WARNING_MESSAGE); }
    }
    private void minhasReservas() {
        try {
            String contacto = mz.uem.restaurante.validation.Validador.telefoneMocambicano(telefone.getText());
            java.util.List<mz.uem.restaurante.model.Reserva> reservas = dados.reservaService.listarReservasCliente(contacto);
            if (reservas.isEmpty()) { JOptionPane.showMessageDialog(this, "Não existem reservas associadas a este telefone.", "Minhas reservas", JOptionPane.INFORMATION_MESSAGE); return; }
            javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(new Object[]{"ID", "Mesa", "Data/hora", "Pessoas", "Estado"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
            for (mz.uem.restaurante.model.Reserva reserva : reservas) modelo.addRow(new Object[]{reserva.getId(), reserva.getMesa().getNumero(), reserva.getDataHora().format(FORMATO), reserva.getPessoas(), reserva.getEstado()});
            javax.swing.JTable tabela = new javax.swing.JTable(modelo); tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(tabela); scroll.setPreferredSize(new java.awt.Dimension(560, 220));
            int opcao = JOptionPane.showOptionDialog(this, scroll, "Minhas reservas", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Cancelar seleccionada", "Fechar"}, "Fechar");
            if (opcao == 0 && tabela.getSelectedRow() >= 0) { dados.reservaService.cancelarPeloCliente((int) modelo.getValueAt(tabela.getSelectedRow(), 0), contacto); JOptionPane.showMessageDialog(this, "Reserva cancelada. A equipa foi actualizada.", "Reserva cancelada", JOptionPane.INFORMATION_MESSAGE); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível consultar reservas", JOptionPane.WARNING_MESSAGE); }
    }
}
