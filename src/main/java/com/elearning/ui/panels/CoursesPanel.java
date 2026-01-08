package com.elearning.ui.panels;

import com.elearning.dao.CourseDao;
import com.elearning.model.Course;
import com.elearning.model.User;
import com.elearning.ui.dialogs.CourseDialog;
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
import java.util.function.Consumer;

public class CoursesPanel extends JPanel {
    private final User user;
    private final CourseDao courseDao = new CourseDao();
    private final JTextField searchField = new JTextField(20);
    private final DefaultTableModel model;
    private final JTable table;
    private final List<Course> courses = new ArrayList<>();
    private final JFrame owner;
    private final Consumer<Course> lessonsHandler;

    public CoursesPanel(JFrame owner, User user, Consumer<Course> lessonsHandler) {
        this.owner = owner;
        this.user = user;
        this.lessonsHandler = lessonsHandler;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]12[grow]"));

        model = new DefaultTableModel(new Object[]{"ID", "Title", "Status", "Created"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(buildToolbar(), "growx, wrap");
        add(new JScrollPane(table), "grow");
        refresh();
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[][grow][]", "[]"));
        JPanel left = new JPanel(new MigLayout("insets 0", "[][grow]"));
        left.add(new JLabel("Search"));
        left.add(searchField, "growx");

        JButton searchButton = new JButton("Go");
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton toggleButton = new JButton("Publish/Unpublish");
        JButton lessonsButton = new JButton("Manage Lessons");

        searchButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> handleAdd());
        editButton.addActionListener(e -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());
        toggleButton.addActionListener(e -> handleToggle());
        lessonsButton.addActionListener(e -> handleLessons());

        JPanel right = new JPanel(new MigLayout("insets 0", "[][][][][]"));
        right.add(searchButton);
        right.add(addButton);
        right.add(editButton);
        right.add(deleteButton);
        right.add(toggleButton);
        right.add(lessonsButton);

        panel.add(left, "growx");
        panel.add(right, "align right");
        return panel;
    }

    public void refresh() {
        try {
            courses.clear();
            model.setRowCount(0);
            String keyword = searchField.getText().trim();
            List<Course> result = keyword.isEmpty()
                    ? courseDao.listByInstructor(user.getId())
                    : courseDao.searchByInstructor(user.getId(), keyword);
            for (Course course : result) {
                courses.add(course);
                model.addRow(new Object[]{
                        course.getId(),
                        course.getTitle(),
                        course.getStatus(),
                        course.getCreatedAt() == null ? "" : course.getCreatedAt().toString()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Course getSelectedCourse() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= courses.size()) {
            return null;
        }
        return courses.get(row);
    }

    private void handleAdd() {
        CourseDialog dialog = new CourseDialog(owner, null);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) {
            return;
        }
        Course course = new Course();
        course.setInstructorId(user.getId());
        course.setTitle(dialog.getTitleValue());
        course.setDescription(dialog.getDescriptionValue());
        course.setStatus(dialog.getStatusValue());
        try {
            courseDao.create(course);
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add course: " + ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        Course selected = getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Courses", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CourseDialog dialog = new CourseDialog(owner, selected);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) {
            return;
        }
        selected.setTitle(dialog.getTitleValue());
        selected.setDescription(dialog.getDescriptionValue());
        selected.setStatus(dialog.getStatusValue());
        try {
            courseDao.update(selected);
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update course: " + ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        Course selected = getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Courses", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete selected course?", "Courses", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            courseDao.delete(selected.getId(), user.getId());
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete course: " + ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleToggle() {
        Course selected = getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Courses", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String newStatus = "PUBLISHED".equals(selected.getStatus()) ? "DRAFT" : "PUBLISHED";
        selected.setStatus(newStatus);
        try {
            courseDao.update(selected);
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to change status: " + ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLessons() {
        Course selected = getSelectedCourse();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Courses", JOptionPane.WARNING_MESSAGE);
            return;
        }
        lessonsHandler.accept(selected);
    }
}
