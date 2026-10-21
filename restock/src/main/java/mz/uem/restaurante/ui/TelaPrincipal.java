package mz.uem.restaurante.ui;

import java.awt.AlphaComposite;
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
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mz.uem.restaurante.controller.AplicacaoController;

/** Tela de boas-vindas com imagem do restaurante e os dois percursos de acesso. */
public class TelaPrincipal extends JFrame {
    private static final Color AMBAR = new Color(247, 178, 65);
    private static final Color VERDE = new Color(90, 154, 56);
    private final AplicacaoController controlador = new AplicacaoController();
    private JLabel lblMarca;
    private JLabel lblTitulo;
    private JLabel lblDescricao;
    private JLabel lblEstado;
    private JButton btnCliente;
    private JButton btnGerente;

    public TelaPrincipal() {
        Ui.instalarTema();
        initComponents();
        configurarVisual();
        configurarEventos();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Restaurante UEM | Gestão e Pedidos");
        setMinimumSize(new Dimension(1040, 610));
        setPreferredSize(new Dimension(1120, 650));

        HeroInicial fundo = new HeroInicial();
        fundo.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
        c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(60, 78, 60, 0);
        fundo.add(criarConteudo(), c);
        setContentPane(fundo);
        pack();
    }

    private JPanel criarConteudo() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        painel.setPreferredSize(new Dimension(500, 430));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;

        lblMarca = new JLabel("RESTAURANTE UEM");
        lblMarca.setFont(new Font("SansSerif", Font.BOLD, 13));
        c.gridy = 0; c.insets = new Insets(0, 0, 18, 0); painel.add(lblMarca, c);

        JLabel etiqueta = new JLabel("GESTÃO & PEDIDOS");
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11)); etiqueta.setForeground(AMBAR);
        c.gridy++; c.insets = new Insets(0, 0, 10, 0); painel.add(etiqueta, c);

        lblTitulo = new JLabel("<html>Sabor que aproxima.<br>Gestão que transforma.</html>");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        c.gridy++; c.insets = new Insets(0, 0, 15, 0); painel.add(lblTitulo, c);

        lblDescricao = new JLabel("<html>Faça o seu pedido ou entre na área da equipa para gerir<br>pedidos, reservas, inventário e clientes num só lugar.</html>");
        lblDescricao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        c.gridy++; c.insets = new Insets(0, 0, 30, 0); painel.add(lblDescricao, c);

        btnCliente = botao("Fazer um pedido", VERDE, Color.WHITE);
        c.gridy++; c.insets = new Insets(0, 0, 12, 0); painel.add(btnCliente, c);
        btnGerente = botao("Entrar na área da equipa", new Color(255, 255, 255, 222), new Color(35, 51, 38));
        c.gridy++; c.insets = new Insets(0, 0, 24, 0); painel.add(btnGerente, c);

        lblEstado = new JLabel();
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        c.gridy++; c.insets = new Insets(0, 0, 0, 0); painel.add(lblEstado, c);
        return painel;
    }

    private JButton botao(String texto, Color fundo, Color primeiroPlano) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 14)); botao.setBackground(fundo); botao.setForeground(primeiroPlano);
        botao.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22)); botao.setFocusPainted(false); botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(275, 48));
        return botao;
    }

    private void configurarVisual() {
        lblMarca.setForeground(Color.WHITE);
        lblTitulo.setForeground(Color.WHITE);
        lblDescricao.setForeground(new Color(242, 238, 225));
        boolean bdActiva = controlador.getDados().isBaseDadosActiva();
        lblEstado.setForeground(bdActiva ? new Color(214, 255, 191) : new Color(255, 224, 167));
        lblEstado.setBackground(new Color(18, 30, 22, 180)); lblEstado.setOpaque(true);
        lblEstado.setText(bdActiva ? "● Base de dados MySQL ligada" : "● Modo temporário: configure o MySQL para persistir dados");
    }

    private void configurarEventos() {
        btnCliente.addActionListener(e -> abrirCliente());
        btnGerente.addActionListener(e -> new TelaLogin(this, controlador).setVisible(true));
    }

    private void abrirCliente() {
        JFrame janela = new JFrame("Restaurante UEM | Novo pedido");
        janela.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        PainelCliente painel = new PainelCliente();
        painel.setDados(controlador.getDados());
        janela.setContentPane(painel);
        janela.setMinimumSize(new Dimension(960, 620));
        janela.pack();
        janela.setLocationRelativeTo(this);
        janela.setVisible(true);
    }

    /** Fundo fotográfico com véu escuro à esquerda para leitura do conteúdo. */
    private static final class HeroInicial extends JPanel {
        private BufferedImage imagem;
        HeroInicial() { carregarImagem(); }
        private void carregarImagem() {
            try { imagem = ImageIO.read(TelaPrincipal.class.getResourceAsStream("/images/welcome-restaurant-hero.png")); }
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
                g.setPaint(new GradientPaint(0, 0, new Color(26, 47, 33), getWidth(), getHeight(), new Color(116, 72, 27))); g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setComposite(AlphaComposite.SrcOver.derive(.87f));
            g.setPaint(new GradientPaint(0, 0, new Color(10, 24, 16), (int) (getWidth() * .68), 0, new Color(10, 24, 16, 28)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setComposite(AlphaComposite.SrcOver); g.setColor(AMBAR); g.fillRect(0, 0, 12, getHeight());
            g.dispose();
        }
    }
}
