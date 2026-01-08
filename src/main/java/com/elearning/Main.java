package com.elearning;

import com.elearning.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fallback to default look and feel
            }
            new LoginFrame().setVisible(true);
        });
    }
}
