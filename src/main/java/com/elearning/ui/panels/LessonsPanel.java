package com.elearning.ui.panels;

import com.elearning.dao.LessonDao;
import com.elearning.model.Course;
import com.elearning.model.Lesson;
import com.elearning.ui.dialogs.LessonDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LessonsPanel extends JPanel {
    private final LessonDao lessonDao = new LessonDao();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField = new JTextField(20);
    private final JLabel courseLabel = new JLabel("Course: -");
    private final List<Lesson> lessons = new ArrayList<>();
    private final JFrame owner;
    private Course course;

    public LessonsPanel(JFrame owner) {
        this.owner = owner;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]12[grow]"));

        model = new DefaultTableModel(new Object[]{"ID", "Title", "Order", "Created"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(buildToolbar(), "growx, wrap");
        add(new JScrollPane(table), "grow");
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[][grow][]", "[]"));
        JPanel left = new JPanel(new MigLayout("insets 0", "[][grow]"));
        left.add(courseLabel, "span, wrap");
        left.add(new JLabel("Search"));
        left.add(searchField, "growx");

        JButton searchButton = new JButton("Go");
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        searchButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> handleAdd());
        editButton.addActionListener(e -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());

        JPanel right = new JPanel(new MigLayout("insets 0", "[][][]"));
        right.add(searchButton);
        right.add(addButton);
        right.add(editButton);
        right.add(deleteButton);

        panel.add(left, "growx");
        panel.add(right, "align right");
        return panel;
    }

    public void setCourse(Course course) {
        this.course = course;
        courseLabel.setText(course == null ? "Course: -" : "Course: " + course.getTitle());
        refresh();
    }

    public void refresh() {
        lessons.clear();
        model.setRowCount(0);
        if (course == null) {
            return;
        }
        try {
            String keyword = searchField.getText().trim();
            List<Lesson> result = keyword.isEmpty()
                    ? lessonDao.listByCourse(course.getId())
                    : lessonDao.searchByCourse(course.getId(), keyword);
            for (Lesson lesson : result) {
                lessons.add(lesson);
                model.addRow(new Object[]{
                        lesson.getId(),
                        lesson.getTitle(),
                        lesson.getOrderIndex(),
                        lesson.getCreatedAt() == null ? "" : lesson.getCreatedAt().toString()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load lessons: " + ex.getMessage(), "Lessons", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Lesson getSelectedLesson() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= lessons.size()) {
            return null;
        }
        return lessons.get(row);
    }

    private void handleAdd() {
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Lessons", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LessonDialog dialog = new LessonDialog(owner, null);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) {
            return;
        }
        Lesson lesson = new Lesson();
        lesson.setCourseId(course.getId());
        lesson.setTitle(dialog.getTitleValue());
        lesson.setOrderIndex(dialog.getOrderValue());
        lesson.setContentJson(dialog.getContentJsonValue());
        try {
            lessonDao.create(lesson);
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add lesson: " + ex.getMessage(), "Lessons", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        Lesson selected = getSelectedLesson();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a lesson first.", "Lessons", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LessonDialog dialog = new LessonDialog(owner, selected);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) {
            return;
        }
        selected.setTitle(dialog.getTitleValue());
        selected.setOrderIndex(dialog.getOrderValue());
        selected.setContentJson(dialog.getContentJsonValue());
        try {
            lessonDao.update(selected);
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update lesson: " + ex.getMessage(), "Lessons", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        Lesson selected = getSelectedLesson();
        if (selected == null || course == null) {
            JOptionPane.showMessageDialog(this, "Select a lesson first.", "Lessons", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete selected lesson?", "Lessons", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            lessonDao.delete(selected.getId(), course.getId());
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete lesson: " + ex.getMessage(), "Lessons", JOptionPane.ERROR_MESSAGE);
        }
    }
}
