package mz.uem.restaurante.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mz.uem.restaurante.controller.AplicacaoController;

/** Diálogo de acesso editável no NetBeans GUI Builder. */
public class TelaLogin extends javax.swing.JDialog {
    private AplicacaoController controlador;

    /** Construtor sem argumentos exigido pelo NetBeans GUI Builder. */
    public TelaLogin() {
        super((java.awt.Frame) null, true);
        initComponents();
        configurarVisual();
    }

    public TelaLogin(JFrame parent, AplicacaoController controlador) {
        super(parent, "Iniciar sessão", true);
        this.controlador = controlador;
        initComponents();
        configurarVisual();
        configurarEventos();
        setLocationRelativeTo(parent);
    }

    private void configurarVisual() {
        getContentPane().setBackground(Ui.FUNDO);
        lblTitulo.setForeground(Ui.PRIMARIA);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 21));
        lblAjuda.setText("Acesso restrito à equipa autorizada.");
        lblAjuda.setForeground(new Color(92, 105, 121));
        lblAjuda.setFont(new Font("SansSerif", Font.PLAIN, 11));
        estilizarBotao(btnEntrar, Ui.DESTAQUE);
        estilizarBotao(btnCancelar, new Color(93, 109, 126));
    }

    private void estilizarBotao(javax.swing.JButton botao, Color cor) { botao.setBackground(cor); botao.setForeground(Color.WHITE); botao.setFocusPainted(false); }
    private void configurarEventos() { btnEntrar.addActionListener(e -> entrar()); btnCancelar.addActionListener(e -> dispose()); txtSenha.addActionListener(e -> entrar()); }

    private void entrar() {
        if (controlador == null) {
            JOptionPane.showMessageDialog(this, "A tela de login ainda não foi ligada à aplicação.", "Configuração", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!controlador.autenticar(txtUser.getText(), new String(txtSenha.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Utilizador ou senha inválidos.", "Acesso recusado", JOptionPane.ERROR_MESSAGE);
            txtSenha.selectAll(); txtSenha.requestFocusInWindow(); return;
        }
        JFrame principal = (JFrame) getParent();
        try {
            // Cria a área interna primeiro; se algo falhar, a janela inicial fica visível.
            TelaGerente dashboard = new TelaGerente(principal, controlador);
            dispose();
            principal.setVisible(false);
            dashboard.setVisible(true);
        } catch (RuntimeException ex) {
            controlador.terminarSessao();
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o dashboard:\n" + mensagem(ex), "Erro ao entrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String mensagem(Exception ex) { return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblSenha = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        btnEntrar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblAjuda = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        lblTitulo.setText("Aceder à área da equipa");
        lblSubtitulo.setText("Use as credenciais atribuídas pelo administrador.");
        lblUser.setText("Utilizador:");
        lblSenha.setText("Senha:");
        btnEntrar.setText("Entrar");
        btnCancelar.setText("Cancelar");
        lblAjuda.setText("Acesso restrito à equipa autorizada.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane()); getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTitulo).addComponent(lblSubtitulo).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblUser).addComponent(lblSenha)).addGap(18, 18, 18).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(txtUser).addComponent(txtSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(btnCancelar).addGap(8, 8, 8).addComponent(btnEntrar))
                .addComponent(lblAjuda)).addContainerGap()));
        layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap().addComponent(lblTitulo).addGap(6, 6, 6).addComponent(lblSubtitulo).addGap(20, 20, 20)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblUser).addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(12, 12, 12)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblSenha).addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnCancelar).addComponent(btnEntrar)).addGap(16, 16, 16).addComponent(lblAjuda).addContainerGap());
        pack();
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEntrar;
    private javax.swing.JLabel lblAjuda;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPasswordField txtSenha;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
