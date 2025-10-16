package sudoku;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIController gui = new GUIController();
            gui.setVisible(true);
        });
    }
}
