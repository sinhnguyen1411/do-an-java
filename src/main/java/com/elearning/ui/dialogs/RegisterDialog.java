package com.elearning.ui.dialogs;

import com.elearning.model.Role;
import com.elearning.service.AuthService;
import com.elearning.util.Validator;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.SQLException;

public class RegisterDialog extends JDialog {
    private final JTextField fullNameField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private final JTextField phoneField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JPasswordField confirmPasswordField = new JPasswordField(24);
    private final JComboBox<Role> roleBox = new JComboBox<>(new Role[]{Role.INSTRUCTOR, Role.STUDENT});
    private final AuthService authService = new AuthService();

    public RegisterDialog(JFrame owner) {
        super(owner, "Register", true);
        setSize(520, 420);
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 20", "[][grow]", "[]12[]12[]12[]12[]12[]16[]"));

        panel.add(new JLabel("Full name"));
        panel.add(fullNameField, "growx, wrap");
        panel.add(new JLabel("Email"));
        panel.add(emailField, "growx, wrap");
        panel.add(new JLabel("Phone"));
        panel.add(phoneField, "growx, wrap");
        panel.add(new JLabel("Password"));
        panel.add(passwordField, "growx, wrap");
        panel.add(new JLabel("Confirm password"));
        panel.add(confirmPasswordField, "growx, wrap");
        panel.add(new JLabel("Role"));
        panel.add(roleBox, "growx, wrap");

        JButton submitButton = new JButton("Create account");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());

        panel.add(submitButton, "span, split 2, growx");
        panel.add(cancelButton, "growx");

        return panel;
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        Role role = (Role) roleBox.getSelectedItem();

        if (!Validator.hasText(fullName)) {
            showError("Full name is required.");
            return;
        }
        if (!Validator.isEmail(email)) {
            showError("Please enter a valid email.");
            return;
        }
        if (!Validator.hasText(password)) {
            showError("Password is required.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            authService.register(fullName, email, phone.isEmpty() ? null : phone, password, role);
            JOptionPane.showMessageDialog(this, "Registration submitted. Await admin approval.", "Register", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            showError("Registration failed: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Register", JOptionPane.ERROR_MESSAGE);
    }
}
