package com.elearning.service;

import com.elearning.dao.UserDao;
import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.util.PasswordUtil;

import java.sql.SQLException;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String email, String password) throws SQLException {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }

    public User register(String fullName, String email, String phone, String password, Role role) throws SQLException {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role);
        user.setStatus(Status.PENDING);
        return userDao.create(user);
    }
}
