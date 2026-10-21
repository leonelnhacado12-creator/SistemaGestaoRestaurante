package mz.uem.restaurante.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import mz.uem.restaurante.controller.AplicacaoController;

/** Janela de acesso com composição gastronómica e formulário focado na equipa. */
public class TelaLogin extends JDialog {
    private static final Color CREME = new Color(255, 251, 243);
    private static final Color AMBAR = new Color(247, 178, 65);
    private static final Color VERDE = new Color(90, 154, 56);
    private static final Color TEXTO = new Color(37, 43, 38);
    private static final Preferences PREFERENCIAS = Preferences.userNodeForPackage(TelaLogin.class);
    private AplicacaoController controlador;
    private JTextField txtUser;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCancelar;
    private JCheckBox chkLembrar;

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

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(960, 580));
        setPreferredSize(new Dimension(960, 580));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(CREME);
        raiz.add(new HeroPanel(), BorderLayout.WEST);
        raiz.add(criarFormulario(), BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(CREME);
        painel.setBorder(BorderFactory.createEmptyBorder(54, 68, 42, 68));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.WEST;

        JLabel etiqueta = new JLabel("ÁREA DA EQUIPA");
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11)); etiqueta.setForeground(VERDE);
        c.gridy = 0; c.insets = new Insets(0, 0, 12, 0); painel.add(etiqueta, c);

        JLabel titulo = new JLabel("Bem-vindo de volta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 29)); titulo.setForeground(TEXTO);
        c.gridy++; c.insets = new Insets(0, 0, 8, 0); painel.add(titulo, c);

        JLabel subtitulo = new JLabel("Entre para gerir pedidos, reservas e operações.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13)); subtitulo.setForeground(new Color(103, 107, 100));
        c.gridy++; c.insets = new Insets(0, 0, 31, 0); painel.add(subtitulo, c);

        c.gridy++; c.insets = new Insets(0, 0, 7, 0); painel.add(rotulo("Utilizador"), c);
        txtUser = campo("Introduza o seu utilizador");
        txtUser.setText(PREFERENCIAS.get("ultimo_utilizador", ""));
        c.gridy++; c.insets = new Insets(0, 0, 18, 0); painel.add(campoComFundo(txtUser), c);

        c.gridy++; c.insets = new Insets(0, 0, 7, 0); painel.add(rotulo("Senha"), c);
        txtSenha = new JPasswordField();
        configurarCampo(txtSenha, "Introduza a sua senha");
        c.gridy++; c.insets = new Insets(0, 0, 9, 0); painel.add(campoComFundo(txtSenha), c);

        JPanel apoio = new JPanel(new BorderLayout()); apoio.setOpaque(false);
        chkLembrar = new JCheckBox("Lembrar utilizador neste computador");
        chkLembrar.setSelected(!txtUser.getText().isBlank());
        chkLembrar.setOpaque(false); chkLembrar.setFont(new Font("SansSerif", Font.PLAIN, 11)); chkLembrar.setForeground(new Color(103, 107, 100));
        JLabel ajuda = new JLabel("Precisa de ajuda? Fale com o administrador");
        ajuda.setFont(new Font("SansSerif", Font.PLAIN, 11)); ajuda.setForeground(VERDE);
        apoio.add(chkLembrar, BorderLayout.WEST); apoio.add(ajuda, BorderLayout.EAST);
        c.gridy++; c.insets = new Insets(0, 0, 27, 0); painel.add(apoio, c);

        btnEntrar = botao("Entrar no sistema", VERDE, Color.WHITE);
        c.gridy++; c.insets = new Insets(0, 0, 13, 0); painel.add(btnEntrar, c);
        btnCancelar = botao("Cancelar", new Color(238, 231, 216), new Color(74, 79, 71));
        c.gridy++; c.insets = new Insets(0, 0, 24, 0); painel.add(btnCancelar, c);

        JLabel rodape = new JLabel("Restaurante UEM  •  Gestão simples, serviço memorável");
        rodape.setText("Demonstração: admin/admin123  •  gerente/gerente123  •  atendente/atendente123");
        rodape.setFont(new Font("SansSerif", Font.PLAIN, 10)); rodape.setForeground(new Color(145, 139, 128));
        c.gridy++; c.insets = new Insets(0, 0, 0, 0); painel.add(rodape, c);
        return painel;
    }

    private JLabel rotulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 12)); etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    private JTextField campo(String dica) {
        JTextField campo = new JTextField(); configurarCampo(campo, dica); return campo;
    }

    private void configurarCampo(JTextField campo, String dica) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13)); campo.setForeground(TEXTO); campo.setBackground(new Color(250, 248, 243));
        campo.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14)); campo.setToolTipText(dica);
        campo.setPreferredSize(new Dimension(340, 44));
    }

    private JPanel campoComFundo(JTextField campo) {
        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(new Color(250, 248, 243)); fundo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 220, 207)), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        fundo.add(campo, BorderLayout.CENTER); return fundo;
    }

    private JButton botao(String texto, Color fundo, Color primeiroPlano) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 13)); botao.setBackground(fundo); botao.setForeground(primeiroPlano);
        botao.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18)); botao.setFocusPainted(false); botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void configurarVisual() { getContentPane().setBackground(CREME); }

    private void configurarEventos() {
        btnEntrar.addActionListener(e -> entrar());
        btnCancelar.addActionListener(e -> dispose());
        txtSenha.addActionListener(e -> entrar());
    }

    private void entrar() {
        if (controlador == null) {
            JOptionPane.showMessageDialog(this, "A tela de login ainda não foi ligada à aplicação.", "Configuração", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!controlador.autenticar(txtUser.getText(), new String(txtSenha.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Utilizador ou senha inválidos.", "Acesso recusado", JOptionPane.ERROR_MESSAGE);
            txtSenha.selectAll(); txtSenha.requestFocusInWindow(); return;
        }
        if (chkLembrar.isSelected()) PREFERENCIAS.put("ultimo_utilizador", txtUser.getText().trim());
        else PREFERENCIAS.remove("ultimo_utilizador");
        JFrame principal = (JFrame) getParent();
        try {
            TelaGerente dashboard = new TelaGerente(principal, controlador);
            dispose(); principal.setVisible(false); dashboard.setVisible(true);
        } catch (RuntimeException ex) {
            controlador.terminarSessao();
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o dashboard:\n" + mensagem(ex), "Erro ao entrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String mensagem(Exception ex) { return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(); }

    /** Painel de fotografia com gradiente para manter contraste e identidade visual. */
    private static final class HeroPanel extends JPanel {
        private BufferedImage imagem;
        HeroPanel() { setPreferredSize(new Dimension(386, 580)); setMinimumSize(new Dimension(330, 580)); carregarImagem(); }
        private void carregarImagem() {
            try { imagem = ImageIO.read(TelaLogin.class.getResourceAsStream("/images/login-food-hero.png")); }
            catch (IOException | IllegalArgumentException ex) { imagem = null; }
        }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            if (imagem != null) {
                double escala = Math.max(getWidth() / (double) imagem.getWidth(), getHeight() / (double) imagem.getHeight());
                int largura = (int) Math.ceil(imagem.getWidth() * escala), altura = (int) Math.ceil(imagem.getHeight() * escala);
                g.drawImage(imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH), (getWidth() - largura) / 2, (getHeight() - altura) / 2, null);
            } else {
                g.setPaint(new GradientPaint(0, 0, new Color(45, 55, 43), getWidth(), getHeight(), new Color(18, 24, 20))); g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setComposite(AlphaComposite.SrcOver.derive(.38f)); g.setPaint(new GradientPaint(0, 0, new Color(16, 22, 17), 0, getHeight(), new Color(16, 22, 17, 25))); g.fillRect(0, 0, getWidth(), getHeight());
            g.setComposite(AlphaComposite.SrcOver); g.setColor(AMBAR); g.fillRect(0, 0, 12, getHeight());
            g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 22)); g.drawString("RESTAURANTE", 37, 58); g.setFont(new Font("SansSerif", Font.PLAIN, 12)); g.drawString("UEM  •  GESTÃO OPERACIONAL", 39, 79);
            g.setColor(new Color(255, 255, 255, 218)); g.setFont(new Font("SansSerif", Font.BOLD, 24)); g.drawString("Sabor que inspira.", 37, getHeight() - 73); g.setFont(new Font("SansSerif", Font.PLAIN, 13)); g.drawString("Organização para um serviço excepcional.", 38, getHeight() - 49);
            g.dispose();
        }
    }
}
