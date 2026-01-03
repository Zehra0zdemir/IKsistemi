package com.hrms.controller;

import com.hrms.util.DatabaseConnection;
import com.hrms.util.PasswordUtil;
import com.hrms.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthController {

    public record LoginResult(boolean success, String message) {}

    public static LoginResult login(String email, String password) {
        if (!ValidationUtil.isValidEmail(email))
            return new LoginResult(false, "Email formatı geçersiz.");

        if (password == null || password.isBlank())
            return new LoginResult(false, "Şifre boş olamaz.");

        String sql = "SELECT password_hash FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new LoginResult(false, "Email veya şifre hatalı.");
                }

                String storedHash = rs.getString("password_hash");
                boolean ok = PasswordUtil.verify(password, storedHash);

                if (ok) return new LoginResult(true, "OK");
                return new LoginResult(false, "Email veya şifre hatalı.");
            }

        } catch (Exception ex) {
            return new LoginResult(false, "Auth DB hatası: " + ex.getMessage());
        }
    }
}
