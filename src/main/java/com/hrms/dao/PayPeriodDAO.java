package com.hrms.dao;

import com.hrms.model.PayPeriod;
import com.hrms.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayPeriodDAO {

    public List<PayPeriod> listOpenPeriods() throws SQLException {
        List<PayPeriod> list = new ArrayList<>();

        String sql = """
            SELECT period_id, start_date, end_date, status
            FROM pay_periods
            WHERE status = 'OPEN'
            ORDER BY start_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PayPeriod p = new PayPeriod();
                p.setPeriodId(rs.getInt("period_id"));
                p.setStartDate(rs.getDate("start_date").toLocalDate());
                p.setEndDate(rs.getDate("end_date").toLocalDate());
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        }
        return list;
    }

    public int createPeriod(LocalDate start, LocalDate end) throws SQLException {
        String sql = """
            INSERT INTO pay_periods (start_date, end_date)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void closePeriod(int periodId) throws SQLException {
        String sql = "UPDATE pay_periods SET status = 'CLOSED' WHERE period_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, periodId);
            ps.executeUpdate();
        }
    }
}
