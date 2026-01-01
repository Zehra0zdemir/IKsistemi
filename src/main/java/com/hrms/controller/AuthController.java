package com.hrms.controller;

public class AuthController {

    public record LoginResult(boolean success, String message) {}

    public static LoginResult login(String email, String password) {
        if (email == null || email.isBlank()) return new LoginResult(false, "Email boş olamaz.");
        if (password == null || password.isBlank()) return new LoginResult(false, "Şifre boş olamaz.");

        // Dummy: demo için
        if (email.equalsIgnoreCase("admin@example.com") && password.equals("1234")) {
            return new LoginResult(true, "OK");
        }

        return new LoginResult(false, "Email veya şifre hatalı.");
    }
}
