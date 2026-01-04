package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payroll {
    private int payrollId;
    private int employeeId;
    private int periodId;

    private BigDecimal baseSalary;
    private BigDecimal overtimeHours;
    private BigDecimal overtimeRate;

    private BigDecimal grossSalary;
    private BigDecimal taxAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netSalary;

    private String generatedByEmail;
    private LocalDateTime createdAt;

    public Payroll() {}

    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getPeriodId() { return periodId; }
    public void setPeriodId(int periodId) { this.periodId = periodId; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public BigDecimal getOvertimeRate() { return overtimeRate; }
    public void setOvertimeRate(BigDecimal overtimeRate) { this.overtimeRate = overtimeRate; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(BigDecimal deductionAmount) { this.deductionAmount = deductionAmount; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public String getGeneratedByEmail() { return generatedByEmail; }
    public void setGeneratedByEmail(String generatedByEmail) { this.generatedByEmail = generatedByEmail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Payroll{" +
                "payrollId=" + payrollId +
                ", employeeId=" + employeeId +
                ", periodId=" + periodId +
                ", baseSalary=" + baseSalary +
                ", overtimeHours=" + overtimeHours +
                ", overtimeRate=" + overtimeRate +
                ", grossSalary=" + grossSalary +
                ", taxAmount=" + taxAmount +
                ", deductionAmount=" + deductionAmount +
                ", netSalary=" + netSalary +
                ", generatedByEmail='" + generatedByEmail + '\'' +
                '}';
    }
}
