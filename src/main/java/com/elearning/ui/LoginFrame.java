package com.elearning.ui;

import com.elearning.model.Status;
import com.elearning.model.User;
import com.elearning.service.AuthService;
import com.elearning.service.Session;
import com.elearning.ui.dialogs.RegisterDialog;
import com.elearning.util.Validator;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField emailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("E-Learning LMS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new MigLayout("fill,insets 24", "[grow]", "[]16[]12[]16[]"));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel form = new JPanel(new MigLayout("fillx, insets 0", "[][grow]", "[]12[]12[]"));
        form.add(new JLabel("Email"));
        form.add(emailField, "growx, wrap");
        form.add(new JLabel("Password"));
        form.add(passwordField, "growx, wrap");

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[]"));
        actions.add(loginButton, "growx");
        actions.add(registerButton, "growx");

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegister());

        panel.add(title, "growx");
        panel.add(form, "growx");
        panel.add(actions, "growx");

        return panel;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!Validator.isEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }
        if (!Validator.hasText(password)) {
            showError("Please enter your password.");
            return;
        }

        try {
            User user = authService.login(email, password);
            if (user == null) {
                showError("Invalid email or password.");
                return;
            }
            if (user.getStatus() != Status.ACTIVE) {
                showError("Your account status is " + user.getStatus() + ". Please contact admin.");
                return;
            }
            Session.setCurrentUser(user);
            SwingUtilities.invokeLater(() -> {
                dispose();
                new MainFrame().setVisible(true);
            });
        } catch (SQLException ex) {
            showError("Login failed: " + ex.getMessage());
        }
    }

    private void openRegister() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login", JOptionPane.ERROR_MESSAGE);
    }
}
