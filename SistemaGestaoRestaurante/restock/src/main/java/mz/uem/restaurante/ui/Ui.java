package mz.uem.restaurante.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;

/** Paleta e componentes reutilizáveis para manter as telas coerentes. */
public final class Ui {
    public static final Color FUNDO = new Color(245, 247, 250);
    public static final Color PRIMARIA = new Color(23, 43, 77);
    public static final Color DESTAQUE = new Color(16, 134, 112);
    public static final Color TEXTO = new Color(35, 48, 65);
    public static final Color ALERTA = new Color(207, 91, 54);
    public static final Color FUNDO_ESCURO = new Color(31, 41, 55);
    public static final Color TEXTO_ESCURO = new Color(229, 231, 235);
    private static boolean temaEscuro;
    private static final Border CARD = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)), BorderFactory.createEmptyBorder(18, 20, 18, 20));

    private Ui() { }

    public static void instalarTema() {
        UIManager.put("Panel.background", FUNDO);
        UIManager.put("OptionPane.background", FUNDO);
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("Table.selectionBackground", new Color(210, 235, 229));
        UIManager.put("Table.selectionForeground", TEXTO);
    }

    /** Alterna o tema global e actualiza componentes Swing criados posteriormente. */
    public static void alternarTema() {
        temaEscuro = !temaEscuro;
        UIManager.put("Panel.background", temaEscuro ? FUNDO_ESCURO : FUNDO);
        UIManager.put("Label.foreground", temaEscuro ? TEXTO_ESCURO : TEXTO);
        UIManager.put("Table.background", temaEscuro ? new Color(55, 65, 81) : Color.WHITE);
        UIManager.put("Table.foreground", temaEscuro ? TEXTO_ESCURO : TEXTO);
        UIManager.put("TextField.background", temaEscuro ? new Color(55, 65, 81) : Color.WHITE);
        UIManager.put("TextField.foreground", temaEscuro ? TEXTO_ESCURO : TEXTO);
    }

    public static boolean isTemaEscuro() { return temaEscuro; }

    public static JButton botao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    public static JButton botaoSecundario(String texto) {
        JButton botao = botao(texto, new Color(93, 109, 126));
        return botao;
    }

    public static JLabel titulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(PRIMARIA);
        return titulo;
    }

    public static void card(JComponent componente) {
        componente.setOpaque(true);
        componente.setBackground(Color.WHITE);
        componente.setBorder(CARD);
    }

    public static void prepararTabela(JTable tabela) {
        tabela.setAutoCreateRowSorter(true);
        tabela.setRowHeight(29);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(PRIMARIA);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(new Color(230, 235, 241));
    }

    public static void aplicarEspacamento(Component componente) {
        if (componente instanceof JComponent jc) {
            jc.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        }
    }
}
