package com.elearning.ui.panels;

import com.elearning.dao.CourseDao;
import com.elearning.dao.LessonDao;
import com.elearning.model.CourseStat;
import com.elearning.model.User;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

public class InstructorDashboardPanel extends JPanel {
    private final CourseDao courseDao = new CourseDao();
    private final LessonDao lessonDao = new LessonDao();
    private final User user;

    private final JLabel courseCountLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lessonCountLabel = new JLabel("0", SwingConstants.CENTER);
    private final JPanel chartHolder = new JPanel(new MigLayout("fill, insets 0"));

    public InstructorDashboardPanel(User user) {
        this.user = user;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        add(buildStatsRow(), "growx, wrap");
        add(buildChartCard(), "grow");
        refresh();
    }

    public void refresh() {
        try {
            int courseCount = courseDao.countByInstructor(user.getId());
            int lessonCount = lessonDao.countByInstructor(user.getId());
            courseCountLabel.setText(String.valueOf(courseCount));
            lessonCountLabel.setText(String.valueOf(lessonCount));

            List<CourseStat> stats = courseDao.lessonCountsByCourse(user.getId());
            chartHolder.removeAll();
            chartHolder.add(buildLessonsChart(stats), "grow");
            chartHolder.revalidate();
            chartHolder.repaint();
        } catch (SQLException ex) {
            // Keep UI stable; counts will remain unchanged if load fails.
        }
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        row.add(buildStatCard("My Courses", courseCountLabel, new Color(37, 99, 235)), "growx");
        row.add(buildStatCard("Total Lessons", lessonCountLabel, new Color(15, 118, 110)), "growx");
        return row;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new MigLayout("fill, insets 12", "[grow]", "[]8[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(71, 85, 105));
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);

        card.add(titleLabel, "align left");
        card.add(valueLabel, "align left");
        return card;
    }

    private JPanel buildChartCard() {
        JPanel card = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[]8[grow]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Lessons Per Course");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 41, 59));

        card.add(title, "wrap");
        card.add(chartHolder, "grow");
        return card;
    }

    private ChartPanel buildLessonsChart(List<CourseStat> stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (CourseStat stat : stats) {
            dataset.addValue(stat.getValue(), "Lessons", stat.getLabel());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Course",
                "Lessons",
                dataset
        );
        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }
}
