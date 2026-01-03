package com.hrms.dao;

import com.hrms.model.Review;
import com.hrms.util.DatabaseConnection;

import java.sql.*;

public class ReviewDAO {

    public int insert(Review r) throws SQLException {

        String sql = """
            INSERT INTO reviews
            (employee_id, evaluator_email, tech, comm, team, leadership, strengths, improvements, overall_rating, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getEmployeeId());
            ps.setString(2, r.getEvaluatorEmail());
            ps.setInt(3, r.getTech());
            ps.setInt(4, r.getComm());
            ps.setInt(5, r.getTeam());
            ps.setInt(6, r.getLead()); // Java'da lead, DB'de leadership
            ps.setString(7, r.getStrengths());
            ps.setString(8, r.getImprovements());
            ps.setDouble(9, r.getOverallRating());
            ps.setString(10, r.getStatus());
            ps.setTimestamp(11, Timestamp.valueOf(r.getCreatedAt()));

            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Insert başarısız, satır eklenmedi.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("Insert oldu ama id dönmedi.");
            }
        }
    }
}
