package mz.uem.restaurante.ui;
import javax.swing.*; import mz.uem.restaurante.service.DadosSistema;
public class TelaPrincipal extends JFrame {
 private final DadosSistema dados=new DadosSistema();
 public TelaPrincipal(){initComponents();configurarEventos();setLocationRelativeTo(null);}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        btnCliente = new javax.swing.JButton();
        btnGerente = new javax.swing.JButton();
        lblRodape = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setText("SISTEMA DE GESTÃO DE RESTAURANTE");

        lblSubtitulo.setText("Escolha o modo de acesso");

        btnCliente.setText("Modo Cliente");

        btnGerente.setText("Modo Gerente");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnCliente)
                        .addGap(48, 48, 48)
                        .addComponent(btnGerente))
                    .addComponent(lblRodape)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(lblSubtitulo)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblRodape)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGerente)
                    .addComponent(btnCliente))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

 // </editor-fold>
 private void configurarEventos(){btnCliente.addActionListener(e->abrirCliente());btnGerente.addActionListener(e->new TelaLogin(this,dados).setVisible(true));}
 private void abrirCliente(){JFrame janela=new JFrame("Modo Cliente");janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);PainelCliente painel=new PainelCliente();painel.setDados(dados);janela.setContentPane(painel);janela.pack();janela.setLocationRelativeTo(this);janela.setVisible(true);}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnGerente;
    private javax.swing.JLabel lblRodape;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    // End of variables declaration//GEN-END:variables
}
