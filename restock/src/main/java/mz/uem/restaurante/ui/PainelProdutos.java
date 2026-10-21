package mz.uem.restaurante.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mz.uem.restaurante.model.Produto;
import mz.uem.restaurante.model.ItemStock;
import mz.uem.restaurante.model.IngredienteReceita;
import mz.uem.restaurante.service.DadosSistema;

/** CRUD de cardápio ligado ao serviço e com filtro por nome/categoria/estado. */
public class PainelProdutos extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Categoria", "Preço (MT)", "Disponibilidade"}, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    private final JTextField pesquisa = new JTextField(15);
    private final JComboBox<String> categoria = new JComboBox<>();
    private final JComboBox<String> disponibilidade = new JComboBox<>(new String[]{"Todos", "Disponível", "Indisponível"});
    private JButton novo, editar, receita, remover, atualizar;
    private JScrollPane scroll;

    public PainelProdutos() { initComponents(); }
    public void setDados(DadosSistema dados) { this.dados = dados; atualizarTabela(); }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { construir(); }
    // </editor-fold>//GEN-END:initComponents

    private void construir() {
        setLayout(new BorderLayout(0, 14)); setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel topo = new JPanel(new BorderLayout(0, 10)); topo.setOpaque(false);
        JPanel textos = new JPanel(new GridLayout(2, 1)); textos.setOpaque(false); textos.add(Ui.titulo("Produtos")); textos.add(new JLabel("Gerencie o cardápio, os preços e a disponibilidade."));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0)); filtros.setOpaque(false);
        filtros.add(new JLabel("Pesquisar:")); filtros.add(pesquisa); filtros.add(new JLabel("Categoria:")); filtros.add(categoria); filtros.add(disponibilidade);
        topo.add(textos, BorderLayout.WEST); topo.add(filtros, BorderLayout.EAST);
        tabela.setRowSorter(sorter); tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); Ui.prepararTabela(tabela);
        scroll = new JScrollPane(tabela); Ui.card(scroll);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); acoes.setOpaque(false);
        novo = Ui.botao("Novo produto", Ui.DESTAQUE); editar = Ui.botaoSecundario("Editar"); receita = Ui.botaoSecundario("Ficha técnica / receita"); remover = Ui.botao("Remover", Ui.ALERTA); atualizar = Ui.botaoSecundario("Atualizar");
        acoes.add(novo); acoes.add(editar); acoes.add(receita); acoes.add(remover); acoes.add(atualizar);
        add(topo, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(acoes, BorderLayout.SOUTH);
        pesquisa.getDocument().addDocumentListener(new SimplesDocumentListener(this::filtrar)); categoria.addActionListener(e -> filtrar()); disponibilidade.addActionListener(e -> filtrar());
        novo.addActionListener(e -> formulario(0)); editar.addActionListener(e -> editar()); receita.addActionListener(e -> editarReceita()); remover.addActionListener(e -> remover()); atualizar.addActionListener(e -> atualizarTabela());
    }

    private void filtrar() {
        String termo = pesquisa.getText().trim().toLowerCase(); String cat = (String) categoria.getSelectedItem(); String estado = (String) disponibilidade.getSelectedItem();
        sorter.setRowFilter(new RowFilter<>() {
            @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String nome = entry.getStringValue(1).toLowerCase(); String c = entry.getStringValue(2); String e = entry.getStringValue(4);
                return (termo.isEmpty() || nome.contains(termo)) && (cat == null || "Todas".equals(cat) || cat.equals(c)) && ("Todos".equals(estado) || estado.equals(e));
            }
        });
    }

    private void editar() {
        int linha = tabela.getSelectedRow(); if (linha < 0) { aviso("Seleccione um produto."); return; }
        formulario((int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0));
    }

    private void remover() {
        int linha = tabela.getSelectedRow(); if (linha < 0) { aviso("Seleccione um produto."); return; }
        int id = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        if (JOptionPane.showConfirmDialog(this, "Remover o produto seleccionado?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dados.produtoService.remover(id); atualizarTabela(); } catch (RuntimeException ex) { erro(ex); }
    }

    private void formulario(int id) {
        Produto produto = id > 0 ? dados.produtoService.buscar(id) : null;
        JTextField nome = new JTextField(produto == null ? "" : produto.getNome()); JTextField cat = new JTextField(produto == null ? "" : produto.getCategoria());
        JTextField preco = new JTextField(produto == null ? "" : String.valueOf(produto.getPreco())); JCheckBox disponivel = new JCheckBox("Disponível", produto == null || produto.isDisponivel());
        JPanel form = new JPanel(new GridLayout(4, 2, 9, 9)); form.add(new JLabel("Nome:")); form.add(nome); form.add(new JLabel("Categoria:")); form.add(cat); form.add(new JLabel("Preço (MT):")); form.add(preco); form.add(new JLabel("Estado:")); form.add(disponivel);
        if (JOptionPane.showConfirmDialog(this, form, produto == null ? "Novo produto" : "Editar produto", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            double valor = Double.parseDouble(preco.getText().trim().replace(',', '.'));
            if (produto == null) dados.produtoService.criar(nome.getText(), cat.getText(), valor, disponivel.isSelected()); else dados.produtoService.atualizar(id, nome.getText(), cat.getText(), valor, disponivel.isSelected());
            atualizarTabela();
        } catch (NumberFormatException ex) { aviso("Introduza um preço numérico válido."); } catch (RuntimeException ex) { erro(ex); }
    }

    private void editarReceita() {
        int linha = tabela.getSelectedRow(); if (linha < 0) { aviso("Seleccione um produto para definir a receita."); return; }
        Produto produto = dados.produtoService.buscar((int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0));
        List<ItemStock> itensStock = dados.stockService.listar("");
        if (itensStock.isEmpty()) { aviso("Cadastre os ingredientes no inventário antes de criar uma receita."); return; }
        DefaultTableModel ingredientes = new DefaultTableModel(new Object[]{"ID", "Ingrediente", "Unidade", "Quantidade por produto"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        for (IngredienteReceita ingrediente : dados.receitaService.listar(produto.getId())) ingredientes.addRow(new Object[]{ingrediente.getItemStockId(), ingrediente.getItemStockNome(), ingrediente.getUnidade(), ingrediente.getQuantidade()});
        JTable tabelaIngredientes = new JTable(ingredientes); Ui.prepararTabela(tabelaIngredientes);
        JComboBox<ItemStock> item = new JComboBox<>(itensStock.toArray(ItemStock[]::new)); JTextField quantidade = new JTextField();
        JButton adicionar = Ui.botao("Adicionar ingrediente", Ui.DESTAQUE), removerIngrediente = Ui.botao("Remover", Ui.ALERTA), guardar = Ui.botao("Guardar ficha técnica", Ui.PRIMARIA);
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); topo.add(new JLabel("Ingrediente:")); topo.add(item); topo.add(new JLabel("Qtd. por unidade:")); topo.add(quantidade); topo.add(adicionar); topo.add(removerIngrediente);
        JPanel conteudo = new JPanel(new BorderLayout(8, 8)); conteudo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); conteudo.add(new JLabel("Receita: " + produto.getNome() + " — ingredientes consumidos por uma unidade vendida."), BorderLayout.NORTH); conteudo.add(new JScrollPane(tabelaIngredientes), BorderLayout.CENTER); conteudo.add(topo, BorderLayout.SOUTH);
        javax.swing.JDialog janela = new javax.swing.JDialog((java.awt.Frame) null, "Ficha técnica", true); janela.setLayout(new BorderLayout(8, 8)); janela.add(conteudo, BorderLayout.CENTER);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT)); rodape.add(guardar); janela.add(rodape, BorderLayout.SOUTH); janela.setSize(760, 430); janela.setLocationRelativeTo(this);
        adicionar.addActionListener(e -> { try { ItemStock escolhido = (ItemStock) item.getSelectedItem(); double q = Double.parseDouble(quantidade.getText().trim().replace(',', '.')); if (q <= 0) throw new NumberFormatException(); for (int i = 0; i < ingredientes.getRowCount(); i++) if ((int) ingredientes.getValueAt(i, 0) == escolhido.getId()) throw new IllegalArgumentException("Este ingrediente já foi incluído."); ingredientes.addRow(new Object[]{escolhido.getId(), escolhido.getNome(), escolhido.getUnidade(), q}); quantidade.setText(""); } catch (NumberFormatException ex) { aviso("Indique uma quantidade válida maior que zero."); } catch (Exception ex) { aviso(ex.getMessage()); } });
        removerIngrediente.addActionListener(e -> { int seleccionada = tabelaIngredientes.getSelectedRow(); if (seleccionada >= 0) ingredientes.removeRow(seleccionada); });
        guardar.addActionListener(e -> { try { List<IngredienteReceita> valores = new ArrayList<>(); for (int i = 0; i < ingredientes.getRowCount(); i++) valores.add(new IngredienteReceita(produto.getId(), (int) ingredientes.getValueAt(i, 0), (String) ingredientes.getValueAt(i, 1), (String) ingredientes.getValueAt(i, 2), ((Number) ingredientes.getValueAt(i, 3)).doubleValue())); dados.receitaService.substituir(produto.getId(), valores); dados.auditoriaService.registar("gerente", "ACTUALIZAR_RECEITA", "produto", produto.getNome()); janela.dispose(); } catch (Exception ex) { erro(ex); } });
        janela.setVisible(true);
    }

    public void atualizarTabela() {
        if (dados == null) return; String seleccionado = (String) categoria.getSelectedItem(); Set<String> categorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); modelo.setRowCount(0);
        for (Produto produto : dados.produtoService.listar()) { categorias.add(produto.getCategoria()); modelo.addRow(new Object[]{produto.getId(), produto.getNome(), produto.getCategoria(), produto.getPreco(), produto.isDisponivel() ? "Disponível" : "Indisponível"}); }
        categoria.removeAllItems(); categoria.addItem("Todas"); categorias.forEach(categoria::addItem); categoria.setSelectedItem(seleccionado != null && categorias.contains(seleccionado) ? seleccionado : "Todas"); filtrar();
    }

    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
