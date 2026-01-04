package com.hrms.controller;

import com.hrms.dao.PayPeriodDAO;
import com.hrms.dao.PayrollDAO;
import com.hrms.model.PayPeriod;
import com.hrms.model.Payroll;
import com.hrms.services.PayrollService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PayrollController {

    public record Result(boolean success, String message, Payroll payroll) {}

    private final PayrollService payrollService = new PayrollService();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final PayPeriodDAO payPeriodDAO = new PayPeriodDAO();

    // 1) Açık dönemleri getir
    public List<PayPeriod> getOpenPeriods() throws SQLException {
        return payPeriodDAO.listOpenPeriods();
    }

    // 2) Çalışanın bordro geçmişi
    public List<Payroll> getPayrollHistory(int employeeId) throws SQLException {
        return payrollDAO.listByEmployee(employeeId);
    }

    // 3) Bordro oluştur (hesapla + kaydet)
    public Result generatePayroll(
            int employeeId,
            int periodId,
            BigDecimal baseSalary,
            BigDecimal overtimeHours,
            BigDecimal overtimeRate,
            BigDecimal deductionAmount,
            BigDecimal taxRate,
            String generatedByEmail
    ) {
        try {
            Payroll payroll = payrollService.generatePayroll(
                    employeeId, periodId,
                    baseSalary, overtimeHours, overtimeRate,
                    deductionAmount, taxRate,
                    generatedByEmail
            );
            return new Result(true, "Bordro oluşturuldu.", payroll);
        } catch (IllegalStateException e) {
            return new Result(false, e.getMessage(), null);
        } catch (SQLException e) {
            return new Result(false, "DB hatası: " + e.getMessage(), null);
        }
    }
}
