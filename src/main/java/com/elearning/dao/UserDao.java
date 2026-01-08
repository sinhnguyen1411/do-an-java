package com.elearning.dao;

import com.elearning.model.Role;
import com.elearning.model.Status;
import com.elearning.model.User;
import com.elearning.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class UserDao {
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, full_name, email, phone, password_hash, role, status, created_at FROM users WHERE email = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        } finally {
            Database.release(conn);
        }
    }

    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
        } finally {
            Database.release(conn);
        }
        return user;
    }

    public void updateProfile(long userId, String fullName) throws SQLException {
        String sql = "UPDATE users SET full_name = ? WHERE id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } finally {
            Database.release(conn);
        }
    }

    public void updatePassword(long userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } finally {
            Database.release(conn);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setStatus(Status.valueOf(rs.getString("status")));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            user.setCreatedAt(created.toLocalDateTime());
        }
        return user;
    }
}
