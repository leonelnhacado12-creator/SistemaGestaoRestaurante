package mz.uem.restaurante.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import mz.uem.restaurante.controller.AplicacaoController;
import mz.uem.restaurante.report.RelatorioService;
import mz.uem.restaurante.report.BackupService;
import mz.uem.restaurante.security.PerfilAcesso;

/** Dashboard da equipa. Todos os controles fixos podem ser editados por drag-and-drop. */
public class TelaGerente extends JFrame {
    private JFrame telaInicial;
    private AplicacaoController controlador;
    private final CardLayout cardLayout = new CardLayout();
    private final Map<String, JPanel> telas = new LinkedHashMap<>();
    private PainelDashboard dashboard;

    /** Construtor requerido pelo NetBeans GUI Builder. */
    public TelaGerente() {
        initComponents();
        configurarVisual();
    }

    public TelaGerente(JFrame telaInicial, AplicacaoController controlador) {
        this();
        configurar(telaInicial, controlador);
    }

    public final void configurar(JFrame telaInicial, AplicacaoController controlador) {
        this.telaInicial = telaInicial;
        this.controlador = controlador;
        lblSessao.setText(controlador.getSessao().getNome() + " — " + controlador.getSessao().getPerfil().getDescricao());
        aplicarPermissoes();
        criarTelas();
        configurarEventos();
        setLocationRelativeTo(telaInicial);
        navegar("inicio");
    }

    private void configurarVisual() {
        setMinimumSize(new Dimension(1120, 690));
        pnlMenu.setBackground(Ui.PRIMARIA);
        pnlConteudo.setBackground(Ui.FUNDO);
        pnlConteudo.setLayout(cardLayout);
        lblMarca.setForeground(Color.WHITE); lblMarca.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblSubtitulo.setForeground(new Color(173, 231, 218)); lblSubtitulo.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblSessao.setForeground(new Color(226, 232, 240)); lblSessao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (JButton botao : new JButton[]{btnInicio, btnVendas, btnPedidos, btnClientes, btnProdutos, btnPromocoes, btnStock, btnRelatorios, btnUtilizadores, btnSair}) {
            botao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); botao.setBackground(Ui.PRIMARIA); botao.setForeground(Color.WHITE); botao.setFocusPainted(false);
            botao.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }
        btnSair.setBackground(new Color(124, 54, 54));
    }

    private void aplicarPermissoes() {
        boolean catalogo = controlador.podeGerirCatalogo();
        btnProdutos.setVisible(catalogo); btnPromocoes.setVisible(catalogo); btnStock.setVisible(catalogo); btnRelatorios.setVisible(catalogo);
        btnUtilizadores.setVisible(controlador.temPerfil(PerfilAcesso.ADMINISTRADOR));
    }

    private void configurarEventos() {
        btnInicio.addActionListener(e -> navegar("inicio")); btnVendas.addActionListener(e -> navegar("vendas"));
        btnPedidos.addActionListener(e -> navegar("pedidos")); btnClientes.addActionListener(e -> navegar("clientes"));
        btnProdutos.addActionListener(e -> navegar("produtos")); btnPromocoes.addActionListener(e -> navegar("promocoes"));
        btnStock.addActionListener(e -> navegar("stock")); btnRelatorios.addActionListener(e -> mostrarRelatorios());
        btnUtilizadores.addActionListener(e -> navegar("utilizadores")); btnSair.addActionListener(e -> dispose());
        addWindowListener(new WindowAdapter() { @Override public void windowClosed(WindowEvent evento) { voltarAoInicio(); } });
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke("control T"), "alternarTema");
        getRootPane().getActionMap().put("alternarTema", new javax.swing.AbstractAction() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { alternarTema(); } });
        lblMarca.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) alternarTema(); } });
    }

    private void alternarTema() {
        Ui.alternarTema();
        getContentPane().setBackground(Ui.isTemaEscuro() ? Ui.FUNDO_ESCURO : Ui.FUNDO);
        pnlConteudo.setBackground(Ui.isTemaEscuro() ? Ui.FUNDO_ESCURO : Ui.FUNDO);
        lblMarca.setToolTipText(Ui.isTemaEscuro() ? "Tema escuro activo" : "Tema claro activo");
        if (dashboard != null) dashboard.atualizar();
    }

    private void criarTelas() {
        telas.clear(); pnlConteudo.removeAll();
        dashboard = new PainelDashboard(controlador.getDados(), this::navegar); adicionarTela("inicio", dashboard);
        PainelCliente vendas = new PainelCliente(); vendas.setDados(controlador.getDados()); adicionarTela("vendas", vendas);
        PainelPedidos pedidos = new PainelPedidos(); pedidos.setDados(controlador.getDados()); adicionarTela("pedidos", pedidos);
        PainelGestaoClientes clientes = new PainelGestaoClientes(); clientes.setDados(controlador.getDados()); adicionarTela("clientes", clientes);
        if (controlador.podeGerirCatalogo()) {
            PainelProdutos produtos = new PainelProdutos(); produtos.setDados(controlador.getDados()); adicionarTela("produtos", produtos);
            PainelPromocoes promocoes = new PainelPromocoes(); promocoes.setDados(controlador.getDados()); adicionarTela("promocoes", promocoes);
            PainelStock stock = new PainelStock(); stock.setDados(controlador.getDados()); adicionarTela("stock", stock);
            PainelMesas mesas = new PainelMesas(); mesas.setDados(controlador.getDados()); adicionarTela("mesas", mesas);
            PainelReservas reservas = new PainelReservas(); reservas.setDados(controlador.getDados()); adicionarTela("reservas", reservas);
            adicionarTela("fornecedores", new PainelFornecedores());
            adicionarTela("lotes", new PainelLotesStock());
        }
        if (controlador.temPerfil(PerfilAcesso.ADMINISTRADOR)) { adicionarTela("utilizadores", new PainelUtilizadores(controlador.getDados(), controlador.getSessao().getId())); PainelAuditoria auditoria=new PainelAuditoria(); auditoria.setDados(controlador.getDados()); adicionarTela("auditoria", auditoria); }
    }

    private void adicionarTela(String nome, JPanel painel) { telas.put(nome, painel); pnlConteudo.add(painel, nome); }

    private void navegar(String destino) {
        if (controlador == null || !telas.containsKey(destino)) return;
        if ("inicio".equals(destino)) dashboard.atualizar();
        JPanel painel = telas.get(destino);
        if (painel instanceof PainelPedidos p) p.atualizarTabela();
        if (painel instanceof PainelProdutos p) p.atualizarTabela();
        if (painel instanceof PainelPromocoes p) p.atualizarTabela();
        if (painel instanceof PainelGestaoClientes p) p.atualizarTabela();
        if (painel instanceof PainelStock p) p.atualizarTabela();
        if (painel instanceof PainelMesas p) p.atualizarTabela();
        if (painel instanceof PainelReservas p) p.atualizarTabela();
        cardLayout.show(pnlConteudo, destino);
    }

    private void mostrarRelatorios() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem produtos = new JMenuItem("Exportar produtos (CSV)"); JMenuItem promocoes = new JMenuItem("Exportar promoções (CSV)"); JMenuItem pedidos = new JMenuItem("Exportar pedidos (CSV)");
        produtos.addActionListener(e -> exportar("produtos", 0)); promocoes.addActionListener(e -> exportar("promocoes", 1)); pedidos.addActionListener(e -> exportar("pedidos", 2));
        JMenuItem mesas=new JMenuItem("Abrir mesas"); JMenuItem reservas=new JMenuItem("Abrir reservas"); JMenuItem fornecedores=new JMenuItem("Abrir fornecedores"); JMenuItem lotes=new JMenuItem("Abrir lotes e validade"); JMenuItem auditoria=new JMenuItem("Abrir auditoria"); JMenuItem backup=new JMenuItem("Criar cópia de segurança (SQL)");
        mesas.addActionListener(e->navegar("mesas")); reservas.addActionListener(e->navegar("reservas")); fornecedores.addActionListener(e->navegar("fornecedores")); lotes.addActionListener(e->navegar("lotes")); auditoria.addActionListener(e->navegar("auditoria")); backup.addActionListener(e->exportarBackup());
        menu.add(produtos); menu.add(promocoes); menu.add(pedidos); menu.addSeparator(); menu.add(mesas); menu.add(reservas); menu.add(fornecedores); menu.add(lotes); menu.addSeparator(); menu.add(backup); if(controlador.temPerfil(PerfilAcesso.ADMINISTRADOR)) menu.add(auditoria); menu.show(btnRelatorios, 0, btnRelatorios.getHeight());
    }

    private void exportar(String nome, int tipo) {
        JFileChooser seletor = new JFileChooser(); seletor.setSelectedFile(new java.io.File(RelatorioService.nomeFicheiro(nome)));
        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            Path destino = seletor.getSelectedFile().toPath(); if (!destino.toString().toLowerCase().endsWith(".csv")) destino = Path.of(destino + ".csv");
            if (tipo == 0) RelatorioService.exportarProdutos(controlador.getDados(), destino); else if (tipo == 1) RelatorioService.exportarPromocoes(controlador.getDados(), destino); else RelatorioService.exportarPedidos(controlador.getDados(), destino);
            JOptionPane.showMessageDialog(this, "Relatório exportado para:\n" + destino, "Relatórios", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao exportar", JOptionPane.ERROR_MESSAGE); }
    }

    private void exportarBackup() {
        JFileChooser seletor = new JFileChooser(); seletor.setSelectedFile(new java.io.File(BackupService.nomeFicheiro()));
        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            Path destino = seletor.getSelectedFile().toPath(); if (!destino.toString().toLowerCase().endsWith(".sql")) destino = Path.of(destino + ".sql");
            BackupService.exportarSql(destino);
            controlador.getDados().auditoriaService.registar(controlador.getSessao().getUsername(), "BACKUP", "base_dados", destino.getFileName().toString());
            JOptionPane.showMessageDialog(this, "Cópia de segurança criada em:\n" + destino, "Backup concluído", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao criar backup", JOptionPane.ERROR_MESSAGE); }
    }

    private void voltarAoInicio() { if (controlador != null) controlador.terminarSessao(); if (telaInicial != null) telaInicial.setVisible(true); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlMenu = new javax.swing.JPanel(); lblMarca = new javax.swing.JLabel(); lblSubtitulo = new javax.swing.JLabel();
        btnInicio = new javax.swing.JButton(); btnVendas = new javax.swing.JButton(); btnPedidos = new javax.swing.JButton(); btnClientes = new javax.swing.JButton();
        btnProdutos = new javax.swing.JButton(); btnPromocoes = new javax.swing.JButton(); btnStock = new javax.swing.JButton(); btnRelatorios = new javax.swing.JButton(); btnUtilizadores = new javax.swing.JButton();
        lblSessao = new javax.swing.JLabel(); btnSair = new javax.swing.JButton(); pnlConteudo = new javax.swing.JPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); setTitle("Restaurante UEM | Dashboard");
        lblMarca.setText("RESTAURANTE UEM"); lblSubtitulo.setText("GESTÃO OPERACIONAL"); btnInicio.setText("Visão geral"); btnVendas.setText("Nova venda"); btnPedidos.setText("Pedidos"); btnClientes.setText("Clientes"); btnProdutos.setText("Produtos"); btnPromocoes.setText("Promoções"); btnStock.setText("Stock"); btnRelatorios.setText("Relatórios"); btnUtilizadores.setText("Utilizadores"); lblSessao.setText("Sessão"); btnSair.setText("Terminar sessão");
        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(pnlMenu); pnlMenu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(menuLayout.createSequentialGroup().addContainerGap().addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblMarca).addComponent(lblSubtitulo).addComponent(btnInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE).addComponent(btnVendas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnPromocoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnRelatorios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnUtilizadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(lblSessao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
        menuLayout.setVerticalGroup(menuLayout.createSequentialGroup().addContainerGap().addComponent(lblMarca).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblSubtitulo).addGap(24, 24, 24).addComponent(btnInicio).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnVendas).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnPedidos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnClientes).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnProdutos).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnPromocoes).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnStock).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnRelatorios).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnUtilizadores).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE).addComponent(lblSessao).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnSair).addContainerGap());
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane()); getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(pnlMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(pnlConteudo, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(pnlConteudo, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE));
        pack();
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClientes, btnInicio, btnPedidos, btnProdutos, btnPromocoes, btnRelatorios, btnSair, btnStock, btnUtilizadores, btnVendas;
    private javax.swing.JLabel lblMarca, lblSessao, lblSubtitulo;
    private javax.swing.JPanel pnlConteudo, pnlMenu;
    // End of variables declaration//GEN-END:variables
}
