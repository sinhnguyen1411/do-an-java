package com.elearning.ui.dialogs;

import com.elearning.model.Course;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;

public class CourseDialog extends JDialog {
    private final JTextField titleField = new JTextField(28);
    private final JTextArea descriptionArea = new JTextArea(4, 28);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"DRAFT", "PUBLISHED"});
    private boolean confirmed;

    public CourseDialog(JFrame owner, Course course) {
        super(owner, course == null ? "Add Course" : "Edit Course", true);
        setContentPane(buildContent());
        setSize(520, 360);
        setLocationRelativeTo(owner);
        if (course != null) {
            titleField.setText(course.getTitle());
            descriptionArea.setText(course.getDescription() == null ? "" : course.getDescription());
            statusBox.setSelectedItem(course.getStatus());
        }
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 16", "[][grow]", "[]10[]10[]16[]"));
        panel.add(new JLabel("Title"));
        panel.add(titleField, "growx, wrap");
        panel.add(new JLabel("Description"));
        panel.add(descriptionArea, "growx, wrap");
        panel.add(new JLabel("Status"));
        panel.add(statusBox, "growx, wrap");

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[]"));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        actions.add(saveButton, "growx");
        actions.add(cancelButton, "growx");
        panel.add(actions, "span, growx");

        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setMinimumSize(new Dimension(200, 80));

        return panel;
    }

    private void handleSave() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.", "Course", JOptionPane.ERROR_MESSAGE);
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

    public String getDescriptionValue() {
        String text = descriptionArea.getText().trim();
        return text.isEmpty() ? null : text;
    }

    public String getStatusValue() {
        return (String) statusBox.getSelectedItem();
    }
}
