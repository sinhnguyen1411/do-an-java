package com.elearning.ui;

import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.service.Session;
import com.elearning.ui.panels.CoursesPanel;
import com.elearning.ui.panels.InstructorDashboardPanel;
import com.elearning.ui.panels.LessonsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private InstructorDashboardPanel dashboardPanel;
    private CoursesPanel coursesPanel;
    private LessonsPanel lessonsPanel;

    public MainFrame() {
        setTitle("E-Learning LMS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        User user = Session.getCurrentUser();
        String welcomeText = user != null ? user.getFullName() + " (" + user.getRole() + ")" : "Guest";

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildTopbar(welcomeText), BorderLayout.NORTH);
        root.add(buildContent(user), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new MigLayout("fillx, insets 16", "[grow]", "[]12[]12[]12[]"));
        sidebar.setBackground(new Color(32, 36, 48));

        JLabel logo = new JLabel("LMS");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton dashboardButton = new JButton("Dashboard");
        JButton coursesButton = new JButton("Courses");
        JButton lessonsButton = new JButton("Lessons");
        dashboardButton.setFocusPainted(false);
        coursesButton.setFocusPainted(false);
        lessonsButton.setFocusPainted(false);

        User user = Session.getCurrentUser();
        boolean isInstructor = user != null && user.getRole() == Role.INSTRUCTOR;
        coursesButton.setVisible(isInstructor);
        lessonsButton.setVisible(isInstructor);

        dashboardButton.addActionListener(e -> showCard("dashboard"));
        coursesButton.addActionListener(e -> showCard("courses"));
        lessonsButton.addActionListener(e -> showCard("lessons"));

        sidebar.add(logo, "growx, wrap");
        sidebar.add(dashboardButton, "growx, wrap");
        sidebar.add(coursesButton, "growx, wrap");
        sidebar.add(lessonsButton, "growx, wrap");
        return sidebar;
    }

    private JPanel buildTopbar(String userLabel) {
        JPanel topbar = new JPanel(new MigLayout("fillx, insets 12", "[grow][]", "[]"));
        topbar.setBackground(Color.WHITE);

        JLabel title = new JLabel("E-Learning Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.clear();
            dispose();
            new LoginFrame().setVisible(true);
        });

        topbar.add(title, "growx");
        topbar.add(new JLabel(userLabel), "gapright 12");
        topbar.add(logoutButton);

        return topbar;
    }

    private JPanel buildContent(User user) {
        if (user != null && user.getRole() == Role.INSTRUCTOR) {
            dashboardPanel = new InstructorDashboardPanel(user);
            lessonsPanel = new LessonsPanel(this);
            coursesPanel = new CoursesPanel(this, user, course -> {
                lessonsPanel.setCourse(course);
                showCard("lessons");
            });

            contentPanel.add(dashboardPanel, "dashboard");
            contentPanel.add(coursesPanel, "courses");
            contentPanel.add(lessonsPanel, "lessons");
            showCard("dashboard");
            return contentPanel;
        }

        JPanel panel = new JPanel(new MigLayout("fill, insets 24", "[grow]", "[grow]"));
        JLabel label = new JLabel("Dashboard coming next", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(label, "grow");
        return panel;
    }

    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
        if ("dashboard".equals(name) && dashboardPanel != null) {
            dashboardPanel.refresh();
        }
        if ("courses".equals(name) && coursesPanel != null) {
            coursesPanel.refresh();
        }
    }
}
