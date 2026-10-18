package mz.uem.restaurante;

import javax.swing.SwingUtilities;
import mz.uem.restaurante.ui.TelaPrincipal;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
