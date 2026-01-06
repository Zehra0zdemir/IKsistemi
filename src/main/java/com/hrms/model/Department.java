package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Department {
    private int departmentId;
    private String departmentName;
    private String description;
    private Integer managerId;
    private BigDecimal budget;
    private LocalDateTime createdAt;
    private String managerName;
    
    public Department() {}
    
    public Department(String departmentName, String description, BigDecimal budget) {
        this.departmentName = departmentName;
        this.description = description;
        this.budget = budget;
    }
    
    // Getters & Setters
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    
    @Override
    public String toString() {
        return departmentName + " (ID: " + departmentId + ")";
    }
}