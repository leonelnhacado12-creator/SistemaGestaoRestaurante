package mz.uem.restaurante.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import mz.uem.restaurante.controller.AplicacaoController;

/** Tela inicial editável no NetBeans GUI Builder. */
public class TelaPrincipal extends JFrame {
    private final AplicacaoController controlador = new AplicacaoController();

    public TelaPrincipal() {
        Ui.instalarTema();
        initComponents();
        configurarVisual();
        configurarEventos();
        setLocationRelativeTo(null);
    }

    private void configurarVisual() {
        getContentPane().setBackground(Ui.FUNDO);
        lblMarca.setForeground(Ui.DESTAQUE);
        lblMarca.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(Ui.PRIMARIA);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        lblDescricao.setForeground(new Color(89, 103, 121));
        lblEstado.setForeground(controlador.getDados().isBaseDadosActiva() ? Ui.DESTAQUE : Ui.ALERTA);
        lblEstado.setText(controlador.getDados().isBaseDadosActiva()
                ? "● Base de dados MySQL ligada" : "● Modo temporário: configure o MySQL para persistir os dados");
        estilizarBotao(btnCliente, Ui.DESTAQUE);
        estilizarBotao(btnGerente, Ui.PRIMARIA);
    }

    private void estilizarBotao(javax.swing.JButton botao, Color cor) {
        botao.setBackground(cor); botao.setForeground(Color.WHITE); botao.setFocusPainted(false);
    }

    private void configurarEventos() {
        btnCliente.addActionListener(e -> abrirCliente());
        btnGerente.addActionListener(e -> new TelaLogin(this, controlador).setVisible(true));
    }

    private void abrirCliente() {
        JFrame janela = new JFrame("Restaurante UEM | Novo pedido");
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        PainelCliente painel = new PainelCliente();
        painel.setDados(controlador.getDados());
        janela.setContentPane(painel);
        janela.setMinimumSize(new java.awt.Dimension(960, 620));
        janela.pack();
        janela.setLocationRelativeTo(this);
        janela.setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblMarca = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        btnCliente = new javax.swing.JButton();
        btnGerente = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Restaurante UEM | Gestão e Pedidos");
        setMinimumSize(new java.awt.Dimension(780, 430));

        lblMarca.setText("RESTAURANTE UEM");
        lblTitulo.setText("Gestão simples. Serviço memorável.");
        lblDescricao.setText("Pedidos, inventário, clientes, relatórios e recibos num único lugar.");
        btnCliente.setText("Fazer um pedido");
        btnGerente.setText("Área da equipa");
        lblEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEstado.setText("Estado da base de dados");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup().addContainerGap(70, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblMarca)
                        .addComponent(lblTitulo)
                        .addComponent(lblDescricao)
                        .addGroup(layout.createSequentialGroup().addComponent(btnCliente, 190, 190, 190)
                            .addGap(18, 18, 18).addComponent(btnGerente, 190, 190, 190))
                        .addComponent(lblEstado))
                    .addContainerGap(70, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup().addContainerGap(70, Short.MAX_VALUE)
                .addComponent(lblMarca).addGap(14, 14, 14).addComponent(lblTitulo)
                .addGap(12, 12, 12).addComponent(lblDescricao).addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnCliente, 42, 42, 42).addComponent(btnGerente, 42, 42, 42))
                .addGap(42, 42, 42).addComponent(lblEstado).addContainerGap(60, Short.MAX_VALUE)
        );
        pack();
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnGerente;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblTitulo;
    // End of variables declaration//GEN-END:variables
}
