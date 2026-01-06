package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Attendance {
    private int attendanceId;
    private int employeeId;
    private LocalDate attendanceDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal hoursWorked;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private String employeeName;
    
    public Attendance() {}
    
    public Attendance(int employeeId, LocalDate attendanceDate, LocalTime checkIn) {
        this.employeeId = employeeId;
        this.attendanceDate = attendanceDate;
        this.checkIn = checkIn;
        this.status = "PRESENT";
    }
    
    public boolean isPresent() { return "PRESENT".equals(status); }
    public boolean hasCheckedOut() { return checkOut != null; }
    public boolean isOvertime() { 
        return hoursWorked != null && hoursWorked.compareTo(new BigDecimal("8.0")) > 0; 
    }
    
    // Getters & Setters
    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public LocalTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalTime checkIn) { this.checkIn = checkIn; }
    public LocalTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalTime checkOut) { this.checkOut = checkOut; }
    public BigDecimal getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(BigDecimal hoursWorked) { this.hoursWorked = hoursWorked; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    @Override
    public String toString() {
        return attendanceDate + " - " + status + " (" + hoursWorked + " saat)";
    }
}