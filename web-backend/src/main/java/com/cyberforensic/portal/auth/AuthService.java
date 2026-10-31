
package com.cyberforensic.portal.auth;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JdbcTemplate jdbc,
                       PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser register(String username, String password,
                             String fullName, String email,
                             String mobileNumber) {

        if (isBlank(username) || isBlank(password)
                || isBlank(fullName) || isBlank(email)) {
            throw new IllegalArgumentException(
                    "Please fill all required fields.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?",
                Integer.class, username.trim(), email.trim());

        if (count != null && count > 0) {
            throw new IllegalArgumentException(
                    "Username or email already exists.");
        }

        jdbc.update(
                "INSERT INTO users (username, password, role, full_name, email, mobile_number) "
                + "VALUES (?, ?, 'USER', ?, ?, ?)",
                username.trim(),
                passwordEncoder.encode(password),
                fullName.trim(),
                email.trim(),
                mobileNumber
        );

        return findByLogin(username.trim());
    }

    public AuthUser login(String identifier, String password) {

        if (isBlank(identifier) || isBlank(password)) {
            throw new IllegalArgumentException(
                    "Username/email and password are required.");
        }

        List<UserRow> users = jdbc.query(
                "SELECT id, username, password, role, full_name, email, mobile_number "
                + "FROM users WHERE username = ? OR email = ? LIMIT 1",
                (rs, rowNum) -> new UserRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("mobile_number")
                ),
                identifier.trim(), identifier.trim()
        );

        if (users.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid username/email or password.");
        }

        UserRow user = users.get(0);
        boolean valid;

        if (isBcryptHash(user.password())) {
            valid = passwordEncoder.matches(password, user.password());
        } else {
            // Support existing demo accounts and upgrade their passwords.
            valid = password.equals(user.password());

            if (valid) {
                jdbc.update(
                        "UPDATE users SET password = ? WHERE id = ?",
                        passwordEncoder.encode(password),
                        user.id()
                );
            }
        }

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid username/email or password.");
        }

        return toAuthUser(user);
    }

    private AuthUser findByLogin(String username) {
        List<UserRow> users = jdbc.query(
                "SELECT id, username, password, role, full_name, email, mobile_number "
                + "FROM users WHERE username = ? LIMIT 1",
                (rs, rowNum) -> new UserRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("mobile_number")
                ),
                username
        );

        if (users.isEmpty()) {
            throw new IllegalStateException("User registration failed.");
        }

        return toAuthUser(users.get(0));
    }

    private AuthUser toAuthUser(UserRow user) {
        return new AuthUser(
                user.id(),
                user.username(),
                user.role(),
                user.fullName(),
                user.email(),
                user.mobileNumber()
        );
    }

    private boolean isBcryptHash(String value) {
        return value != null && (
                value.startsWith("$2a$")
                || value.startsWith("$2b$")
                || value.startsWith("$2y$")
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record UserRow(
            int id,
            String username,
            String password,
            String role,
            String fullName,
            String email,
            String mobileNumber
    ) {}

    public record AuthUser(
            int id,
            String username,
            String role,
            String fullName,
            String email,
            String mobileNumber
    ) {}
}
