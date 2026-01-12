package com.hrms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton Pattern
 * Tüm sınıflar için tek bağlantı noktası
 */
public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hrms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "SIFRENIZ";  // ← MySQL ŞİFRENİ BURAYA YAZ!

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * Private constructor - Singleton pattern
     */
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Veritabanı bağlantısı başarılı!");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver bulunamadı!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Veritabanı bağlantı hatası!");
            System.err.println("Kontrol Et:");
            System.err.println("  - MySQL servisi çalışıyor mu?");
            System.err.println("  - Kullanıcı: " + USER);
            System.err.println("  - Şifre: " + PASSWORD);
            System.err.println("  - Veritabanı: hrms_db");
            e.printStackTrace();
        }
    }

    /**
     * Singleton instance döndürür
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.connection == null || instance.connection.isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                instance = new DatabaseConnection();
            }
        }
        return instance;
    }

    /**
     * Connection nesnesi döndürür
     * KULLANIM: DatabaseConnection.getInstance().getConnection()
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Bağlantıyı kapatır
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Veritabanı bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            System.err.println("✗ Bağlantı kapatma hatası!");
            e.printStackTrace();
        }
    }

    /**
     * Bağlantı test metodu
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}