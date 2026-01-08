package com.elearning.dao;

import com.elearning.model.Lesson;
import com.elearning.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LessonDao {
    public Lesson create(Lesson lesson) throws SQLException {
        String sql = "INSERT INTO lessons (course_id, title, content_json, order_index) VALUES (?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, lesson.getCourseId());
            ps.setString(2, lesson.getTitle());
            ps.setString(3, lesson.getContentJson());
            ps.setInt(4, lesson.getOrderIndex());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    lesson.setId(keys.getLong(1));
                }
            }
        } finally {
            Database.release(conn);
        }
        return lesson;
    }

    public void update(Lesson lesson) throws SQLException {
        String sql = "UPDATE lessons SET title = ?, content_json = ?, order_index = ? WHERE id = ? AND course_id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lesson.getTitle());
            ps.setString(2, lesson.getContentJson());
            ps.setInt(3, lesson.getOrderIndex());
            ps.setLong(4, lesson.getId());
            ps.setLong(5, lesson.getCourseId());
            ps.executeUpdate();
        } finally {
            Database.release(conn);
        }
    }

    public void delete(long lessonId, long courseId) throws SQLException {
        String sql = "DELETE FROM lessons WHERE id = ? AND course_id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            ps.setLong(2, courseId);
            ps.executeUpdate();
        } finally {
            Database.release(conn);
        }
    }

    public List<Lesson> listByCourse(long courseId) throws SQLException {
        String sql = "SELECT id, course_id, title, content_json, order_index, created_at FROM lessons WHERE course_id = ? ORDER BY order_index ASC, created_at DESC";
        List<Lesson> lessons = new ArrayList<>();
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lessons.add(mapLesson(rs));
                }
            }
        } finally {
            Database.release(conn);
        }
        return lessons;
    }

    public List<Lesson> searchByCourse(long courseId, String keyword) throws SQLException {
        String sql = "SELECT id, course_id, title, content_json, order_index, created_at FROM lessons WHERE course_id = ? AND title LIKE ? ORDER BY order_index ASC, created_at DESC";
        List<Lesson> lessons = new ArrayList<>();
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lessons.add(mapLesson(rs));
                }
            }
        } finally {
            Database.release(conn);
        }
        return lessons;
    }

    public int countByInstructor(long instructorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM lessons l JOIN courses c ON l.course_id = c.id WHERE c.instructor_id = ?";
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

    private Lesson mapLesson(ResultSet rs) throws SQLException {
        Lesson lesson = new Lesson();
        lesson.setId(rs.getLong("id"));
        lesson.setCourseId(rs.getLong("course_id"));
        lesson.setTitle(rs.getString("title"));
        lesson.setContentJson(rs.getString("content_json"));
        lesson.setOrderIndex(rs.getInt("order_index"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            lesson.setCreatedAt(created.toLocalDateTime());
        }
        return lesson;
    }
}
