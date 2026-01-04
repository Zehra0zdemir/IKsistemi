package com.hrms.services;

import com.hrms.dao.PayrollDAO;
import com.hrms.model.Payroll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class PayrollService {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    /**
     * Bordro hesaplar ve veritabanına kaydeder
     */
    public Payroll generatePayroll(
            int employeeId,
            int periodId,
            BigDecimal baseSalary,
            BigDecimal overtimeHours,
            BigDecimal overtimeRate,
            BigDecimal deductionAmount,
            BigDecimal taxRate,
            String generatedByEmail
    ) throws SQLException {

        // 1Aynı dönem + çalışan kontrolü
        if (payrollDAO.existsForEmployeeAndPeriod(employeeId, periodId)) {
            throw new IllegalStateException("Bu çalışan için bu dönemde bordro zaten oluşturulmuş.");
        }

        //  Mesai ücreti
        BigDecimal overtimePay = overtimeHours.multiply(overtimeRate);

        // Brüt maaş
        BigDecimal grossSalary = baseSalary.add(overtimePay);

        // Vergi
        BigDecimal taxAmount = grossSalary
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);

        //  Net maaş
        BigDecimal netSalary = grossSalary
                .subtract(taxAmount)
                .subtract(deductionAmount)
                .setScale(2, RoundingMode.HALF_UP);

        //  Payroll nesnesi oluştur
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employeeId);
        payroll.setPeriodId(periodId);
        payroll.setBaseSalary(baseSalary);
        payroll.setOvertimeHours(overtimeHours);
        payroll.setOvertimeRate(overtimeRate);
        payroll.setGrossSalary(grossSalary);
        payroll.setTaxAmount(taxAmount);
        payroll.setDeductionAmount(deductionAmount);
        payroll.setNetSalary(netSalary);
        payroll.setGeneratedByEmail(generatedByEmail);

        //  DB’ye kaydet
        int payrollId = payrollDAO.insertPayroll(payroll);
        payroll.setPayrollId(payrollId);

        return payroll;
    }
}
