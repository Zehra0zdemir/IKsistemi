package com.hrms.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.hrms.util.DatabaseConnection;

public class ReportService {

    public record DashboardStats(
            int totalEmployees,
            int totalReviews,
            double avgRating,
            int lowPerformanceCount
    ) {}

    public DashboardStats loadDashboardStats() {
        int totalEmployees = safeIntQuery("SELECT COUNT(*) FROM employees");
        int totalReviews = safeIntQuery("SELECT COUNT(*) FROM reviews");
        double avgRating = safeDoubleQuery("SELECT COALESCE(AVG(overall_rating),0) FROM reviews");
        int lowPerf = safeIntQuery("SELECT COUNT(*) FROM reviews WHERE overall_rating < 3.0");

        return new DashboardStats(totalEmployees, totalReviews, avgRating, lowPerf);
    }

    private int safeIntQuery(String sql) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            // tablo yoksa vs. demo çökmesin
            return 0;
        }
    }

    private double safeDoubleQuery(String sql) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
