package com.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PayPeriod {
    private int periodId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // OPEN / CLOSED
    private LocalDateTime createdAt;

    public PayPeriod() {}

    public PayPeriod(int periodId, LocalDate startDate, LocalDate endDate, String status) {
        this.periodId = periodId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getPeriodId() { return periodId; }
    public void setPeriodId(int periodId) { this.periodId = periodId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PayPeriod{" +
                "periodId=" + periodId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}
