package mz.uem.restaurante.ui;

import java.awt.Color;
import java.nio.file.Path;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mz.uem.restaurante.model.Cliente;
import mz.uem.restaurante.model.OperadoraCarteira;
import mz.uem.restaurante.model.EstadoPagamento;
import mz.uem.restaurante.model.Pagamento;
import mz.uem.restaurante.model.PagamentoCartao;
import mz.uem.restaurante.model.PagamentoDinheiro;
import mz.uem.restaurante.model.PagamentoMPesa;
import mz.uem.restaurante.model.PagamentoEMola;
import mz.uem.restaurante.model.PagamentoMKesh;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.Promocao;
import mz.uem.restaurante.model.Produto;
import mz.uem.restaurante.report.ReciboPdfService;
import mz.uem.restaurante.service.DadosSistema;
import mz.uem.restaurante.validation.Validador;

/** Tela de venda integralmente editável no NetBeans GUI Builder. */
public class PainelCliente extends javax.swing.JPanel {
    private DadosSistema dados;
    private Pedido pedido;
    private Pedido ultimoPedido;
    private final DefaultTableModel modeloCardapio = new DefaultTableModel(new Object[]{"ID", "Produto", "Categoria", "Preço (MT)", "Estado"}, 0) { @Override public boolean isCellEditable(int l, int c) { return false; } };
    private final DefaultTableModel modeloCarrinho = new DefaultTableModel(new Object[]{"ID", "Produto", "Qtd.", "Unitário", "Subtotal"}, 0) { @Override public boolean isCellEditable(int l, int c) { return false; } };
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloCardapio);
    private final Set<Long> avisosProntoExibidos = new HashSet<>();
    private javax.swing.Timer temporizadorNotificacoes;

    public PainelCliente() {
        initComponents();
        configurarComponentes();
        configurarEventos();
    }

    public void setDados(DadosSistema dados) { this.dados = dados; atualizarCardapio(); iniciarNotificacoesAutomaticas(); }

    private void configurarComponentes() {
        setBackground(Ui.FUNDO);
        tabelaCardapio.setModel(modeloCardapio); tabelaCardapio.setRowSorter(sorter); tabelaCardapio.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabelaCardapio);
        tabelaCarrinho.setModel(modeloCarrinho); tabelaCarrinho.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabelaCarrinho);
        spnQuantidade.setModel(new SpinnerNumberModel(1, 1, 50, 1));
        lblTitulo.setForeground(Ui.PRIMARIA); lblTitulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 22));
        lblTotal.setForeground(Ui.DESTAQUE); lblTotal.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 20));
        for (javax.swing.JScrollPane scroll : new javax.swing.JScrollPane[]{scrollCardapio, scrollCarrinho}) scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        estilizar(btnAdicionar, Ui.DESTAQUE); estilizar(btnRemover, Ui.ALERTA); estilizar(btnLimpar, new Color(93, 109, 126)); estilizar(btnFinalizar, Ui.PRIMARIA); estilizar(btnPdf, new Color(93, 109, 126)); estilizar(btnReserva, Ui.PRIMARIA); estilizar(btnAtendimento, Ui.DESTAQUE); estilizar(btnNotificacoes, new Color(93, 109, 126)); btnPdf.setEnabled(false);
    }
    private void estilizar(JButton botao, Color cor) { botao.setBackground(cor); botao.setForeground(Color.WHITE); botao.setFocusPainted(false); }
    private void configurarEventos() {
        txtPesquisa.getDocument().addDocumentListener(new SimplesDocumentListener(this::filtrar)); cmbCategoria.addActionListener(e -> filtrar());
        btnAdicionar.addActionListener(e -> adicionar()); btnRemover.addActionListener(e -> removerItem()); btnLimpar.addActionListener(e -> limparPedido()); btnFinalizar.addActionListener(e -> finalizar()); btnPdf.addActionListener(e -> exportarPdf()); btnReserva.addActionListener(e -> reservarMesa()); btnAtendimento.addActionListener(e -> abrirAtendimento()); btnNotificacoes.addActionListener(e -> consultarNotificacoes());
    }

    private void filtrar() {
        String termo = txtPesquisa.getText().trim().toLowerCase(); String categoria = (String) cmbCategoria.getSelectedItem();
        sorter.setRowFilter(new RowFilter<>() { @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
            String nome = e.getStringValue(1).toLowerCase(); String cat = e.getStringValue(2);
            return (termo.isEmpty() || nome.contains(termo)) && (categoria == null || "Todas".equals(categoria) || categoria.equals(cat));
        }});
    }

    private void adicionar() {
        if (dados == null) return;
        int linha = tabelaCardapio.getSelectedRow();
        if (linha < 0) { aviso("Seleccione um produto do cardápio."); return; }
        if (txtCliente.getText().isBlank()) { aviso("Indique o nome do cliente antes de adicionar itens."); txtCliente.requestFocusInWindow(); return; }
        int id = (int) modeloCardapio.getValueAt(tabelaCardapio.convertRowIndexToModel(linha), 0); Produto produto = dados.produtoService.buscar(id);
        if (produto == null || !produto.isDisponivel()) { aviso("Este produto não está disponível."); return; }
        if (pedido == null) pedido = dados.pedidoService.criarPedido(obterCliente());
        pedido.adicionarItem(produto, (int) spnQuantidade.getValue()); atualizarCarrinho();
    }
    private Cliente obterCliente() { String nome = txtCliente.getText().trim(); return dados.clienteService.listar(nome).stream().filter(c -> c.getNome().equalsIgnoreCase(nome)).findFirst().orElseGet(() -> dados.clienteService.guardar(0, nome, "")); }
    private void removerItem() { int linha = tabelaCarrinho.getSelectedRow(); if (pedido == null || linha < 0) { aviso("Seleccione um item do carrinho."); return; } pedido.alterarQuantidade(linha, 0); atualizarCarrinho(); }
    private void limparPedido() { pedido = null; modeloCarrinho.setRowCount(0); atualizarTotal(); }

    private void finalizar() {
        if (pedido == null || pedido.getItens().isEmpty()) { aviso("O carrinho está vazio."); return; }
        String[] opcoes = {"Dinheiro", "Cartão", "M-Pesa", "e-Mola", "mKesh"}; String escolha = (String) JOptionPane.showInputDialog(this, "Forma de pagamento:", "Checkout", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]); if (escolha == null) return;
        double subtotal = pedido.getTotal(), desconto = descontoPromocao(subtotal), total = subtotal - desconto;
        String telefoneCarteira = null;
        if (escolha.equals("M-Pesa") || escolha.equals("e-Mola") || escolha.equals("mKesh")) {
            String telefone = JOptionPane.showInputDialog(this, "Número da carteira móvel (9 dígitos):", escolha);
            if (telefone == null) return;
            try { telefoneCarteira = Validador.telefoneCarteira(telefone, OperadoraCarteira.porDescricao(escolha)); associarTelefoneCliente(telefoneCarteira); }
            catch (Exception ex) { aviso(ex.getMessage()); return; }
        }
        else if (!pedido.getCliente().getTelefone().isBlank() || pedirTelefoneNotificacao()) { /* contacto actualizado, se informado */ }
        String metodoPagamento = escolha;
        Pagamento pagamento = switch (metodoPagamento) { case "Cartão" -> new PagamentoCartao(); case "M-Pesa" -> new PagamentoMPesa(); case "e-Mola" -> new PagamentoEMola(); case "mKesh" -> new PagamentoMKesh(); default -> new PagamentoDinheiro(); };
        if (!pagamento.pagar(total)) { aviso("O pagamento não foi aprovado."); return; }
        String formaRegistada = pagamento.getDescricao() + (telefoneCarteira == null ? "" : " (" + telefoneCarteira + ")");
        EstadoPagamento estadoPagamento = telefoneCarteira == null ? EstadoPagamento.CONFIRMADO : EstadoPagamento.EM_VALIDACAO;
        try { dados.pedidoService.confirmar(pedido, formaRegistada, subtotal, desconto, total, estadoPagamento); ultimoPedido = pedido; pedido = null; modeloCarrinho.setRowCount(0); atualizarTotal(); btnPdf.setEnabled(true); mostrarRecibo(ultimoPedido); atualizarCardapio(); } catch (Exception ex) { erro(ex); }
    }

    private boolean pedirTelefoneNotificacao() {
        String telefone = JOptionPane.showInputDialog(this, "Telefone para receber/consultar notificações do pedido (opcional):", "Notificações do pedido", JOptionPane.QUESTION_MESSAGE);
        if (telefone == null || telefone.isBlank()) return false;
        try { associarTelefoneCliente(Validador.telefoneMocambicano(telefone)); return true; }
        catch (Exception ex) { aviso(ex.getMessage()); return false; }
    }

    private void associarTelefoneCliente(String telefone) {
        Cliente cliente = pedido.getCliente();
        dados.clienteService.guardar(cliente.getId(), cliente.getNome(), telefone);
    }

    private void abrirAtendimento() {
        if (dados == null) return;
        String telefone = pedido == null ? "" : pedido.getCliente().getTelefone();
        javax.swing.JFrame janela = new javax.swing.JFrame("Restaurante UEM | Atendimento");
        janela.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        janela.setContentPane(new PainelChatCliente(dados, txtCliente.getText().trim(), telefone));
        janela.setSize(720, 560); janela.setLocationRelativeTo(this); janela.setVisible(true);
    }

    private void reservarMesa() {
        if (dados == null) return;
        if (dados.reservaService.listarMesas().isEmpty()) { aviso("Ainda não há mesas disponíveis para reserva. Contacte o restaurante."); return; }
        String telefone = pedido == null ? "" : pedido.getCliente().getTelefone();
        new DialogReservaCliente(this, dados, txtCliente.getText().trim(), telefone).setVisible(true);
    }

    private void consultarNotificacoes() {
        if (dados == null) return;
        String sugerido = pedido != null && !pedido.getCliente().getTelefone().isBlank() ? pedido.getCliente().getTelefone() : txtCliente.getText().trim();
        String identificador = JOptionPane.showInputDialog(this, "Indique o nome ou telefone usado no pedido:", sugerido);
        if (identificador == null || identificador.isBlank()) return;
        StringBuilder texto = new StringBuilder();
        dados.notificacaoService.listarParaCliente(identificador).forEach(n -> texto.append(n.getDataHora().toString().replace('T', ' ')).append("\n").append(n.getMensagem()).append("\n\n"));
        javax.swing.JTextArea area = new javax.swing.JTextArea(texto.isEmpty() ? "Ainda não há notificações para este contacto." : texto.toString(), 14, 42);
        area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(area), "Notificações", JOptionPane.INFORMATION_MESSAGE);
    }
    private void iniciarNotificacoesAutomaticas() {
        if (temporizadorNotificacoes != null) temporizadorNotificacoes.stop();
        temporizadorNotificacoes = new javax.swing.Timer(10_000, e -> avisarPedidoPronto());
        temporizadorNotificacoes.setInitialDelay(2_000); temporizadorNotificacoes.start();
    }
    private void avisarPedidoPronto() {
        if (dados == null || !isShowing() || txtCliente.getText().isBlank()) return;
        String identificador = pedido != null && !pedido.getCliente().getTelefone().isBlank() ? pedido.getCliente().getTelefone() : txtCliente.getText().trim();
        dados.notificacaoService.listarParaCliente(identificador).stream()
                .filter(n -> n.getMensagem().contains("está pronto"))
                .filter(n -> avisosProntoExibidos.add(n.getId()))
                .findFirst()
                .ifPresent(n -> JOptionPane.showMessageDialog(this, n.getMensagem() + "\nDirija-se ao balcão para levantar o pedido.", "Pedido pronto para levantamento", JOptionPane.INFORMATION_MESSAGE));
    }
    private double descontoPromocao(double subtotal) {
        java.util.List<Promocao> ativas = dados.promocaoService.listar().stream().filter(Promocao::isAtiva).toList(); if (ativas.isEmpty()) return 0;
        Object[] escolhas = new Object[ativas.size() + 1]; escolhas[0] = "Sem promoção"; for (int i = 0; i < ativas.size(); i++) escolhas[i + 1] = ativas.get(i);
        Object escolhido = JOptionPane.showInputDialog(this, "Aplicar promoção (opcional):", "Promoções", JOptionPane.QUESTION_MESSAGE, null, escolhas, escolhas[0]); return escolhido instanceof Promocao promocao ? subtotal - promocao.aplicarDesconto(subtotal) : 0;
    }
    private void mostrarRecibo(Pedido recibo) {
        StringBuilder texto = new StringBuilder("RECIBO — RESTAURANTE UEM\nPedido #").append(recibo.getId()).append("\nCliente: ").append(recibo.getCliente().getNome()).append("\n\n");
        recibo.getItens().forEach(item -> texto.append(item.getQuantidade()).append(" x ").append(item.getProduto().getNome()).append(" = ").append(String.format("%.2f MT", item.getSubtotal())).append("\n"));
        texto.append("\nSubtotal: ").append(String.format("%.2f MT", recibo.getSubtotal())).append("\nDesconto: ").append(String.format("%.2f MT", recibo.getDesconto())).append("\nTOTAL: ").append(String.format("%.2f MT", recibo.getTotal())).append("\nPagamento: ").append(recibo.getFormaPagamento()).append("\nEstado do pagamento: ").append(recibo.getEstadoPagamento().getDescricao());
        javax.swing.JTextArea area = new javax.swing.JTextArea(texto.toString(), 16, 38); area.setEditable(false); JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(area), "Pedido confirmado", JOptionPane.INFORMATION_MESSAGE);
    }
    private void exportarPdf() {
        if (ultimoPedido == null) { aviso("Finalize um pedido para gerar o recibo PDF."); return; }
        JFileChooser seletor = new JFileChooser(); seletor.setSelectedFile(new java.io.File(ReciboPdfService.nomeFicheiro(ultimoPedido))); if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try { Path destino = seletor.getSelectedFile().toPath(); if (!destino.toString().toLowerCase().endsWith(".pdf")) destino = Path.of(destino + ".pdf"); ReciboPdfService.exportar(ultimoPedido, destino); JOptionPane.showMessageDialog(this, "Recibo PDF guardado em:\n" + destino, "Recibo disponível", JOptionPane.INFORMATION_MESSAGE); } catch (Exception ex) { erro(ex); }
    }
    private void atualizarCarrinho() { modeloCarrinho.setRowCount(0); if (pedido != null) pedido.getItens().forEach(item -> modeloCarrinho.addRow(new Object[]{item.getProduto().getId(), item.getProduto().getNome(), item.getQuantidade(), item.getProduto().getPreco(), item.getSubtotal()})); atualizarTotal(); }
    private void atualizarTotal() { lblTotal.setText(String.format("%.2f MT", pedido == null ? 0 : pedido.getTotal())); }
    public void atualizarCardapio() {
        if (dados == null) return; String seleccionado = (String) cmbCategoria.getSelectedItem(); Set<String> categorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); modeloCardapio.setRowCount(0);
        for (Produto produto : dados.produtoService.listar()) { categorias.add(produto.getCategoria()); modeloCardapio.addRow(new Object[]{produto.getId(), produto.getNome(), produto.getCategoria(), produto.getPreco(), produto.isDisponivel() ? "Disponível" : "Indisponível"}); }
        cmbCategoria.removeAllItems(); cmbCategoria.addItem("Todas"); categorias.forEach(cmbCategoria::addItem); cmbCategoria.setSelectedItem(seleccionado != null && categorias.contains(seleccionado) ? seleccionado : "Todas"); filtrar();
    }
    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage() == null ? "Ocorreu um erro ao concluir a operação." : ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel(); lblCliente = new javax.swing.JLabel(); txtCliente = new javax.swing.JTextField(); lblPesquisa = new javax.swing.JLabel(); txtPesquisa = new javax.swing.JTextField(); lblCategoria = new javax.swing.JLabel(); cmbCategoria = new javax.swing.JComboBox<>(); lblQuantidade = new javax.swing.JLabel(); spnQuantidade = new javax.swing.JSpinner();
        lblCardapio = new javax.swing.JLabel(); scrollCardapio = new javax.swing.JScrollPane(); tabelaCardapio = new javax.swing.JTable(); btnAdicionar = new javax.swing.JButton();
        lblCarrinho = new javax.swing.JLabel(); scrollCarrinho = new javax.swing.JScrollPane(); tabelaCarrinho = new javax.swing.JTable(); btnRemover = new javax.swing.JButton(); btnLimpar = new javax.swing.JButton(); lblTotalTitulo = new javax.swing.JLabel(); lblTotal = new javax.swing.JLabel(); btnPdf = new javax.swing.JButton(); btnReserva = new javax.swing.JButton(); btnAtendimento = new javax.swing.JButton(); btnNotificacoes = new javax.swing.JButton(); btnFinalizar = new javax.swing.JButton();
        lblTitulo.setText("Nova venda"); lblCliente.setText("Cliente:* "); lblPesquisa.setText("Pesquisar:"); lblCategoria.setText("Categoria:"); lblQuantidade.setText("Quantidade:"); lblCardapio.setText("Cardápio"); lblCarrinho.setText("Carrinho do pedido"); btnAdicionar.setText("Adicionar ao carrinho"); btnRemover.setText("Remover item"); btnLimpar.setText("Limpar pedido"); lblTotalTitulo.setText("Total:"); lblTotal.setText("0,00 MT"); btnPdf.setText("Baixar último recibo (PDF)"); btnReserva.setText("Reservar mesa"); btnAtendimento.setText("Falar com atendente"); btnNotificacoes.setText("Notificações"); btnFinalizar.setText("Finalizar e gerar recibo");
        scrollCardapio.setViewportView(tabelaCardapio); scrollCarrinho.setViewportView(tabelaCarrinho);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this); setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitulo).addGroup(layout.createSequentialGroup().addComponent(lblCliente).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtCliente, 190,190,190).addGap(18,18,18).addComponent(lblPesquisa).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtPesquisa, 150,150,150).addGap(12,12,12).addComponent(lblCategoria).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(cmbCategoria, 130,130,130).addGap(12,12,12).addComponent(lblQuantidade).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(spnQuantidade, 65,65,65))
            .addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblCardapio).addComponent(scrollCardapio, 0, 480, Short.MAX_VALUE).addComponent(btnAdicionar)).addGap(14,14,14).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblCarrinho).addComponent(scrollCarrinho, 0, 480, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(btnRemover).addGap(8,8,8).addComponent(btnLimpar).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE).addComponent(lblTotalTitulo).addGap(6,6,6).addComponent(lblTotal))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(btnPdf).addGap(10,10,10).addComponent(btnReserva).addGap(10,10,10).addComponent(btnAtendimento).addGap(10,10,10).addComponent(btnNotificacoes).addGap(10,10,10).addComponent(btnFinalizar))).addContainerGap()));
        layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap().addComponent(lblTitulo).addGap(14,14,14).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblCliente).addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblPesquisa).addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblCategoria).addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblQuantidade).addComponent(spnQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(16,16,16).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblCardapio).addComponent(lblCarrinho)).addGap(6,6,6).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(scrollCardapio, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE).addComponent(scrollCarrinho, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)).addGap(10,10,10).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnAdicionar).addComponent(btnRemover).addComponent(btnLimpar).addComponent(lblTotalTitulo).addComponent(lblTotal)).addGap(14,14,14).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnPdf).addComponent(btnReserva).addComponent(btnAtendimento).addComponent(btnNotificacoes).addComponent(btnFinalizar)).addContainerGap());
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar, btnAtendimento, btnFinalizar, btnLimpar, btnNotificacoes, btnPdf, btnRemover, btnReserva;
    private javax.swing.JComboBox<String> cmbCategoria;
    private javax.swing.JLabel lblCardapio, lblCarrinho, lblCategoria, lblCliente, lblPesquisa, lblQuantidade, lblTitulo, lblTotal, lblTotalTitulo;
    private javax.swing.JScrollPane scrollCardapio, scrollCarrinho;
    private javax.swing.JSpinner spnQuantidade;
    private javax.swing.JTable tabelaCardapio, tabelaCarrinho;
    private javax.swing.JTextField txtCliente, txtPesquisa;
    // End of variables declaration//GEN-END:variables
}
