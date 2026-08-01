package ui;

import javax.swing.*;

/**
 * Standalone launcher to preview the GUI without any backend.
 * Run this class directly — it does NOT touch Main.java or any DAO/DB code.
 */
public class GuiLauncher {
    public static void main(String[] args) {
        // Use system look-and-feel for native window decorations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
