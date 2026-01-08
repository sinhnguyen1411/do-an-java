package com.elearning.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

public final class Database {
    private static final int MAX_POOL_SIZE = 5;
    private static final Deque<Connection> POOL = new ArrayDeque<>();
    private static String url;
    private static String user;
    private static String password;

    static {
        loadConfig();
    }

    private Database() {
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load application.properties", e);
        }
        url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/elearning");
        user = props.getProperty("db.user", "root");
        password = props.getProperty("db.password", "");
    }

    public static Connection getConnection() throws SQLException {
        synchronized (POOL) {
            if (!POOL.isEmpty()) {
                return POOL.pop();
            }
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void release(Connection connection) {
        if (connection == null) {
            return;
        }
        synchronized (POOL) {
            if (POOL.size() < MAX_POOL_SIZE) {
                POOL.push(connection);
                return;
            }
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
            // Ignore close failures
        }
    }
}
