package com.elearning.ui.dialogs;

import com.elearning.model.Lesson;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LessonDialog extends JDialog {
    private final JTextField titleField = new JTextField(26);
    private final JTextField orderField = new JTextField(6);
    private final JTextArea contentArea = new JTextArea(6, 26);
    private boolean confirmed;

    public LessonDialog(JFrame owner, Lesson lesson) {
        super(owner, lesson == null ? "Add Lesson" : "Edit Lesson", true);
        setContentPane(buildContent());
        setSize(520, 420);
        setLocationRelativeTo(owner);
        if (lesson != null) {
            titleField.setText(lesson.getTitle());
            orderField.setText(String.valueOf(lesson.getOrderIndex()));
            contentArea.setText(lesson.getContentJson());
        }
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 16", "[][grow]", "[]10[]10[]16[]"));
        panel.add(new JLabel("Title"));
        panel.add(titleField, "growx, wrap");
        panel.add(new JLabel("Order"));
        panel.add(orderField, "w 80!, wrap");
        panel.add(new JLabel("Content JSON"));
        panel.add(new JScrollPane(contentArea), "growx, h 160!, wrap");

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[]"));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        actions.add(saveButton, "growx");
        actions.add(cancelButton, "growx");
        panel.add(actions, "span, growx");

        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());

        return panel;
    }

    private void handleSave() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.", "Lesson", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Integer.parseInt(orderField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Order must be a number.", "Lesson", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (contentArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Content JSON is required.", "Lesson", JOptionPane.ERROR_MESSAGE);
            return;
        }
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getTitleValue() {
        return titleField.getText().trim();
    }

    public int getOrderValue() {
        return Integer.parseInt(orderField.getText().trim());
    }

    public String getContentJsonValue() {
        return contentArea.getText().trim();
    }
}
