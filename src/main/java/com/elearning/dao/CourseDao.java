package com.elearning.dao;

import com.elearning.model.Course;
import com.elearning.model.CourseStat;
import com.elearning.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    public Course create(Course course) throws SQLException {
        String sql = "INSERT INTO courses (instructor_id, title, description, status) VALUES (?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, course.getInstructorId());
            ps.setString(2, course.getTitle());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setId(keys.getLong(1));
                }
            }
        } finally {
            Database.release(conn);
        }
        return course;
    }

    public void update(Course course) throws SQLException {
        String sql = "UPDATE courses SET title = ?, description = ?, status = ? WHERE id = ? AND instructor_id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setString(3, course.getStatus());
            ps.setLong(4, course.getId());
            ps.setLong(5, course.getInstructorId());
            ps.executeUpdate();
        } finally {
            Database.release(conn);
        }
    }

    public void delete(long courseId, long instructorId) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ? AND instructor_id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ps.setLong(2, instructorId);
            ps.executeUpdate();
        } finally {
            Database.release(conn);
        }
    }

    public List<Course> listByInstructor(long instructorId) throws SQLException {
        String sql = "SELECT id, instructor_id, title, description, status, created_at FROM courses WHERE instructor_id = ? ORDER BY created_at DESC";
        List<Course> courses = new ArrayList<>();
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } finally {
            Database.release(conn);
        }
        return courses;
    }

    public List<Course> searchByInstructor(long instructorId, String keyword) throws SQLException {
        String sql = "SELECT id, instructor_id, title, description, status, created_at FROM courses WHERE instructor_id = ? AND title LIKE ? ORDER BY created_at DESC";
        List<Course> courses = new ArrayList<>();
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, instructorId);
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } finally {
            Database.release(conn);
        }
        return courses;
    }

    public List<CourseStat> lessonCountsByCourse(long instructorId) throws SQLException {
        String sql = "SELECT c.title, COUNT(l.id) AS lesson_count " +
                "FROM courses c LEFT JOIN lessons l ON c.id = l.course_id " +
                "WHERE c.instructor_id = ? GROUP BY c.id, c.title ORDER BY c.created_at DESC";
        List<CourseStat> stats = new ArrayList<>();
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.add(new CourseStat(rs.getString("title"), rs.getInt("lesson_count")));
                }
            }
        } finally {
            Database.release(conn);
        }
        return stats;
    }

    public int countByInstructor(long instructorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM courses WHERE instructor_id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            Database.release(conn);
        }
        return 0;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setInstructorId(rs.getLong("instructor_id"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            course.setCreatedAt(created.toLocalDateTime());
        }
        return course;
    }
}
