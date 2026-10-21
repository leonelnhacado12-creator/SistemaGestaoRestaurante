package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import mz.uem.restaurante.model.MensagemAtendimento;
import mz.uem.restaurante.model.RemetenteAtendimento;
import mz.uem.restaurante.model.TipoAtendimento;
import mz.uem.restaurante.service.DadosSistema;

/** Chat do cliente para reclamações, dúvidas e feedback, sem necessidade de conta. */
public class PainelChatCliente extends JPanel {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final DadosSistema dados;
    private final JTextField txtCliente = new JTextField();
    private final JTextField txtTelefone = new JTextField();
    private final JComboBox<TipoAtendimento> cmbTipo = new JComboBox<>(TipoAtendimento.values());
    private final JTextArea historico = new JTextArea();
    private final JTextArea mensagem = new JTextArea(4, 45);

    public PainelChatCliente(DadosSistema dados, String cliente, String telefone) {
        this.dados = dados;
        txtCliente.setText(cliente == null ? "" : cliente);
        txtTelefone.setText(telefone == null ? "" : telefone);
        construir(); atualizarHistorico();
    }

    private void construir() {
        setLayout(new BorderLayout(10, 10)); setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JPanel dadosCliente = new JPanel(new GridLayout(2, 4, 8, 8));
        dadosCliente.add(new JLabel("Nome:*")); dadosCliente.add(txtCliente); dadosCliente.add(new JLabel("Telefone:")); dadosCliente.add(txtTelefone);
        dadosCliente.add(new JLabel("Assunto:")); dadosCliente.add(cmbTipo); dadosCliente.add(new JLabel(""));
        JButton actualizar = Ui.botaoSecundario("Actualizar conversa"); dadosCliente.add(actualizar);
        historico.setEditable(false); historico.setLineWrap(true); historico.setWrapStyleWord(true);
        JScrollPane scrollHistorico = new JScrollPane(historico); Ui.card(scrollHistorico);
        JPanel envio = new JPanel(new BorderLayout(8, 8)); envio.setOpaque(false);
        mensagem.setLineWrap(true); mensagem.setWrapStyleWord(true); envio.add(new JScrollPane(mensagem), BorderLayout.CENTER);
        JButton enviar = Ui.botao("Enviar mensagem", Ui.DESTAQUE);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); botoes.setOpaque(false); botoes.add(enviar); envio.add(botoes, BorderLayout.SOUTH);
        actualizar.addActionListener(e -> atualizarHistorico()); enviar.addActionListener(e -> enviar());
        add(dadosCliente, BorderLayout.NORTH); add(scrollHistorico, BorderLayout.CENTER); add(envio, BorderLayout.SOUTH);
    }

    private void enviar() {
        try {
            dados.atendimentoService.enviarCliente(txtCliente.getText(), txtTelefone.getText(), (TipoAtendimento) cmbTipo.getSelectedItem(), mensagem.getText());
            mensagem.setText(""); atualizarHistorico();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível enviar", JOptionPane.WARNING_MESSAGE); }
    }

    private void atualizarHistorico() {
        StringBuilder texto = new StringBuilder();
        for (MensagemAtendimento item : dados.atendimentoService.conversa(txtCliente.getText(), txtTelefone.getText())) {
            texto.append('[').append(item.getDataHora().format(FORMATO)).append("] ")
                    .append(item.getRemetente() == RemetenteAtendimento.CLIENTE ? "Você" : "Atendente")
                    .append(": ").append(item.getMensagem()).append("\n\n");
        }
        historico.setText(texto.isEmpty() ? "Ainda não existem mensagens neste atendimento." : texto.toString());
        historico.setCaretPosition(historico.getDocument().getLength());
    }
}
