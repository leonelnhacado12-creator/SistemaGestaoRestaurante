package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mz.uem.restaurante.security.PerfilAcesso;
import mz.uem.restaurante.security.UsuarioSistema;
import mz.uem.restaurante.service.DadosSistema;

/** Gestão de contas; é disponibilizada exclusivamente ao perfil Administrador. */
public class PainelUtilizadores extends JPanel {
    private DadosSistema dados;
    private int idSessao;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Utilizador", "Perfil"}, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    private final JTextField pesquisa = new JTextField(22);
    private JButton novo, editar, remover, atualizar;
    private JScrollPane scroll;

    public PainelUtilizadores() {
        initComponents();
    }

    public PainelUtilizadores(DadosSistema dados, int idSessao) {
        this.dados = dados;
        this.idSessao = idSessao;
        initComponents();
        atualizarTabela();
    }

    public final void configurar(DadosSistema dados, int idSessao) {
        this.dados = dados; this.idSessao = idSessao; atualizarTabela();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { construir(); }
    // </editor-fold>//GEN-END:initComponents

    private void construir() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel topo = new JPanel(new BorderLayout(0, 10)); topo.setOpaque(false);
        JPanel texto = new JPanel(new GridLayout(2, 1)); texto.setOpaque(false);
        texto.add(Ui.titulo("Utilizadores e permissões"));
        texto.add(new JLabel("Crie as contas de administrador, gerente e atendente."));
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); filtro.setOpaque(false);
        filtro.add(new JLabel("Pesquisar:")); filtro.add(pesquisa);
        topo.add(texto, BorderLayout.WEST); topo.add(filtro, BorderLayout.EAST);

        tabela.setRowSorter(sorter); Ui.prepararTabela(tabela);
        scroll = new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); acoes.setOpaque(false);
        novo = Ui.botao("Novo utilizador", Ui.DESTAQUE);
        editar = Ui.botaoSecundario("Editar");
        remover = Ui.botao("Remover", Ui.ALERTA);
        atualizar = Ui.botaoSecundario("Atualizar");
        acoes.add(novo); acoes.add(editar); acoes.add(remover); acoes.add(atualizar);
        add(topo, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH);

        pesquisa.getDocument().addDocumentListener(new SimplesDocumentListener(this::filtrar));
        novo.addActionListener(e -> formulario(0)); editar.addActionListener(e -> editar());
        remover.addActionListener(e -> remover()); atualizar.addActionListener(e -> atualizarTabela());
    }

    private void filtrar() {
        String termo = pesquisa.getText().trim();
        sorter.setRowFilter(termo.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(termo)));
    }

    private void editar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { aviso("Seleccione um utilizador."); return; }
        formulario((int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0));
    }

    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { aviso("Seleccione um utilizador."); return; }
        int id = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        if (JOptionPane.showConfirmDialog(this, "Remover o utilizador seleccionado?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dados.authService.remover(id, idSessao); atualizarTabela(); } catch (RuntimeException ex) { erro(ex); }
    }

    private void formulario(int id) {
        UsuarioSistema usuario = id > 0 ? dados.authService.buscar(id) : null;
        JTextField nome = new JTextField(usuario == null ? "" : usuario.getNome());
        JTextField username = new JTextField(usuario == null ? "" : usuario.getUsername());
        JPasswordField senha = new JPasswordField();
        JComboBox<PerfilAcesso> perfil = new JComboBox<>(PerfilAcesso.values());
        if (usuario != null) perfil.setSelectedItem(usuario.getPerfil());
        JPanel formulario = new JPanel(new GridLayout(4, 2, 9, 9));
        formulario.add(new JLabel("Nome:")); formulario.add(nome);
        formulario.add(new JLabel("Utilizador:")); formulario.add(username);
        formulario.add(new JLabel(usuario == null ? "Senha:" : "Nova senha (opcional):")); formulario.add(senha);
        formulario.add(new JLabel("Perfil:")); formulario.add(perfil);
        if (JOptionPane.showConfirmDialog(this, formulario, usuario == null ? "Novo utilizador" : "Editar utilizador", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            dados.authService.guardar(id, nome.getText(), username.getText(), new String(senha.getPassword()), (PerfilAcesso) perfil.getSelectedItem());
            atualizarTabela();
        } catch (RuntimeException ex) { erro(ex); }
    }

    public void atualizarTabela() {
        if (dados == null) return;
        modelo.setRowCount(0);
        for (UsuarioSistema u : dados.authService.listar()) modelo.addRow(new Object[]{u.getId(), u.getNome(), u.getUsername(), u.getPerfil().getDescricao()});
        filtrar();
    }

    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível concluir", JOptionPane.ERROR_MESSAGE); }
}
