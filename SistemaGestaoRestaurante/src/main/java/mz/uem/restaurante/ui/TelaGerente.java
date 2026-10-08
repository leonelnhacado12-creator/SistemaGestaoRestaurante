package mz.uem.restaurante.ui;
import javax.swing.*; import mz.uem.restaurante.service.DadosSistema;
public class TelaGerente extends JFrame {
 private final DadosSistema dados; private PainelProdutos painelProdutos; private PainelPedidos painelPedidos; private PainelPromocoes painelPromocoes;
 public TelaGerente(JFrame parent,DadosSistema dados){this.dados=dados;initComponents();configurarEventos();setLocationRelativeTo(parent);atualizarPaineis();}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblProdutos = new javax.swing.JLabel();
        lblPedidos = new javax.swing.JLabel();
        lblPromocoes = new javax.swing.JLabel();
        btnProdutos = new javax.swing.JButton();
        btnPedidos = new javax.swing.JButton();
        btnPromocoes = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setText("DASHBOARD DO GERENTE");

        lblProdutos.setText("Produtos: 0");

        lblPedidos.setText("Pedidos: 0");

        lblPromocoes.setText("Promoções: 0");

        btnProdutos.setText("Produtos");

        btnPedidos.setText("Pedidos");

        btnPromocoes.setText("Promoções");

        btnAtualizar.setText("Atualizar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblProdutos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnProdutos)
                        .addGap(18, 18, 18)
                        .addComponent(btnPedidos)
                        .addGap(15, 15, 15)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPromocoes)
                        .addGap(18, 18, 18)
                        .addComponent(btnAtualizar)
                        .addGap(0, 100, Short.MAX_VALUE))
                    .addComponent(lblPromocoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(202, 202, 202)
                .addComponent(lblTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProdutos)
                    .addComponent(btnPedidos)
                    .addComponent(btnPromocoes)
                    .addComponent(btnAtualizar))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProdutos)
                    .addComponent(lblPromocoes)
                    .addComponent(lblPedidos))
                .addContainerGap(185, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

 // </editor-fold>
 private void configurarEventos(){btnProdutos.addActionListener(e->abrirPainel("Produtos",painelProdutos));btnPedidos.addActionListener(e->abrirPainel("Pedidos",painelPedidos));btnPromocoes.addActionListener(e->abrirPainel("Promoções",painelPromocoes));btnAtualizar.addActionListener(e->atualizarPaineis());}
 private void atualizarPaineis(){painelProdutos=new PainelProdutos();painelPedidos=new PainelPedidos();painelPromocoes=new PainelPromocoes();painelProdutos.setDados(dados);painelPedidos.setDados(dados);painelPromocoes.setDados(dados);lblProdutos.setText("Produtos: "+dados.produtoService.listar().size());lblPedidos.setText("Pedidos: "+dados.pedidoService.listar().size());lblPromocoes.setText("Promoções: "+dados.promocaoService.listar().size());}
 private void abrirPainel(String titulo,JPanel painel){JFrame f=new JFrame(titulo);f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);f.setContentPane(painel);f.pack();f.setLocationRelativeTo(this);f.setVisible(true);}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnPedidos;
    private javax.swing.JButton btnProdutos;
    private javax.swing.JButton btnPromocoes;
    private javax.swing.JLabel lblPedidos;
    private javax.swing.JLabel lblProdutos;
    private javax.swing.JLabel lblPromocoes;
    private javax.swing.JLabel lblTitulo;
    // End of variables declaration//GEN-END:variables
}
