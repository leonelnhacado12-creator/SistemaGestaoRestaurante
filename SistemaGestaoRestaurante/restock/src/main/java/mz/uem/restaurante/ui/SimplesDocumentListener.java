package mz.uem.restaurante.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Adaptador para evitar repetir três métodos ao reagir a uma pesquisa. */
public class SimplesDocumentListener implements DocumentListener {
    private final Runnable aoAlterar;
    public SimplesDocumentListener(Runnable aoAlterar) { this.aoAlterar = aoAlterar; }
    @Override public void insertUpdate(DocumentEvent evento) { aoAlterar.run(); }
    @Override public void removeUpdate(DocumentEvent evento) { aoAlterar.run(); }
    @Override public void changedUpdate(DocumentEvent evento) { aoAlterar.run(); }
}
