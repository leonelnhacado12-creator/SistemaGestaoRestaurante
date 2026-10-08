package mz.uem.restaurante.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import mz.uem.restaurante.model.Produto;
import mz.uem.restaurante.service.DadosSistema;

public class PainelProdutos extends JPanel {
    private DadosSistema dados;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Categoria", "Preço (MT)", "Disponível"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public PainelProdutos() {
        initComponents();
        configurarEventos();
    }

    public void setDados(DadosSistema d) { dados = d; atualizarTabela(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        btnNovo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20));
        lblTitulo.setText("GESTÃO DE PRODUTOS — CRUD");
        tabela.setModel(modelo);
        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll.setViewportView(tabela);
        btnNovo.setText("Novo");
        btnEditar.setText("Editar");
        btnRemover.setText("Remover");
        btnAtualizar.setText("Atualizar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scroll)
                )
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo)
                    .addComponent(btnEditar)
                    .addComponent(btnRemover)
                    .addComponent(btnAtualizar)
                )
        );
    }//GEN-END:initComponents



    private void configurarEventos() {
        btnNovo.addActionListener(e -> novo());
        btnEditar.addActionListener(e -> editar());
        btnRemover.addActionListener(e -> remover());
        btnAtualizar.addActionListener(e -> atualizarTabela());
    }

    private void novo() { formulario(-1); }
    private void editar() {
        int r = tabela.getSelectedRow();
        if (r >= 0) formulario((int) modelo.getValueAt(r, 0));
        else JOptionPane.showMessageDialog(this, "Selecione um produto.");
    }
    private void remover() {
        int r = tabela.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Selecione um produto."); return; }
        if (JOptionPane.showConfirmDialog(this, "Remover produto?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dados.produtoService.remover((int) modelo.getValueAt(r, 0));
            atualizarTabela();
        }
    }
    private void formulario(int id) {
        if (dados == null) return;
        JTextField nome = new JTextField(), cat = new JTextField(), preco = new JTextField();
        JCheckBox disp = new JCheckBox("Disponível", true);
        if (id > 0) {
            Produto p = dados.produtoService.buscar(id);
            nome.setText(p.getNome()); cat.setText(p.getCategoria()); preco.setText(String.valueOf(p.getPreco())); disp.setSelected(p.isDisponivel());
        }
        JPanel p = new JPanel(new java.awt.GridLayout(4, 2, 8, 8));
        p.add(new JLabel("Nome:")); p.add(nome); p.add(new JLabel("Categoria:")); p.add(cat);
        p.add(new JLabel("Preço (MT):")); p.add(preco); p.add(new JLabel("Estado:")); p.add(disp);
        if (JOptionPane.showConfirmDialog(this, p, id > 0 ? "Editar Produto" : "Novo Produto", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                double v = Double.parseDouble(preco.getText().replace(',', '.'));
                if (nome.getText().isBlank() || cat.getText().isBlank() || v < 0) throw new Exception();
                if (id < 0) dados.produtoService.criar(nome.getText().trim(), cat.getText().trim(), v, disp.isSelected());
                else dados.produtoService.atualizar(id, nome.getText().trim(), cat.getText().trim(), v, disp.isSelected());
                atualizarTabela();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Dados inválidos."); }
        }
    }
    public void atualizarTabela() {
        if (dados == null) return;
        modelo.setRowCount(0);
        for (Produto p : dados.produtoService.listar())
            modelo.addRow(new Object[]{p.getId(), p.getNome(), p.getCategoria(), String.format("%.2f", p.getPreco()), p.isDisponivel() ? "Sim" : "Não"});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAtualizar;
    private JButton btnEditar;
    private JButton btnNovo;
    private JButton btnRemover;
    private JLabel lblTitulo;
    private JScrollPane scroll;
    private JTable tabela;
    // End of variables declaration//GEN-END:variables
}
