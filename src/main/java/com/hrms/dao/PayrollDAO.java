package com.hrms.dao;

import com.hrms.model.Payroll;
import com.hrms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public boolean existsForEmployeeAndPeriod(int employeeId, int periodId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM payroll
            WHERE employee_id = ? AND period_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, periodId);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public int insertPayroll(Payroll p) throws SQLException {
        String sql = """
            INSERT INTO payroll
            (employee_id, period_id, base_salary, overtime_hours, overtime_rate,
             gross_salary, tax_amount, deduction_amount, net_salary, generated_by_email)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getEmployeeId());
            ps.setInt(2, p.getPeriodId());
            ps.setBigDecimal(3, p.getBaseSalary());
            ps.setBigDecimal(4, p.getOvertimeHours());
            ps.setBigDecimal(5, p.getOvertimeRate());
            ps.setBigDecimal(6, p.getGrossSalary());
            ps.setBigDecimal(7, p.getTaxAmount());
            ps.setBigDecimal(8, p.getDeductionAmount());
            ps.setBigDecimal(9, p.getNetSalary());
            ps.setString(10, p.getGeneratedByEmail());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Payroll> listByEmployee(int employeeId) throws SQLException {
        List<Payroll> list = new ArrayList<>();

        String sql = "SELECT * FROM payroll WHERE employee_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Payroll p = new Payroll();
                p.setPayrollId(rs.getInt("payroll_id"));
                p.setEmployeeId(rs.getInt("employee_id"));
                p.setPeriodId(rs.getInt("period_id"));
                p.setBaseSalary(rs.getBigDecimal("base_salary"));
                p.setOvertimeHours(rs.getBigDecimal("overtime_hours"));
                p.setOvertimeRate(rs.getBigDecimal("overtime_rate"));
                p.setGrossSalary(rs.getBigDecimal("gross_salary"));
                p.setTaxAmount(rs.getBigDecimal("tax_amount"));
                p.setDeductionAmount(rs.getBigDecimal("deduction_amount"));
                p.setNetSalary(rs.getBigDecimal("net_salary"));
                p.setGeneratedByEmail(rs.getString("generated_by_email"));
                list.add(p);
            }
        }
        return list;
    }
}
