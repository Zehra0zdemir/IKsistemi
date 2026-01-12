package com.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequest {
    private int leaveId;
    private int employeeId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private String status;
    private String reason;
    private Integer approvedBy;
    private LocalDateTime requestDate;
    private LocalDateTime approvalDate;
    private String employeeName;
    private String approverName;
    
    public LeaveRequest() {}
    
    public LeaveRequest(int employeeId, String leaveType, LocalDate startDate,
                       LocalDate endDate, String reason) {
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = "PENDING";
        // Toplam gün sayısını hesapla (başlangıç ve bitiş dahil)
        this.totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    public boolean isPending() { return "PENDING".equals(status); }
    public boolean isApproved() { return "APPROVED".equals(status); }
    public boolean isRejected() { return "REJECTED".equals(status); }
    
    public String getLeaveTypeDisplay() {
        switch (leaveType) {
            case "ANNUAL": return "Yıllık İzin";
            case "SICK": return "Hastalık İzni";
            case "UNPAID": return "Ücretsiz İzin";
            case "MATERNITY": return "Doğum İzni";
            case "PATERNITY": return "Babalık İzni";
            default: return leaveType;
        }
    }
    
    // Getters & Setters
    public int getLeaveId() { return leaveId; }
    public void setLeaveId(int leaveId) { this.leaveId = leaveId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    
    @Override
    public String toString() {
        return getLeaveTypeDisplay() + ": " + startDate + " - " + endDate + " (" + totalDays + " gün)";
    }
}