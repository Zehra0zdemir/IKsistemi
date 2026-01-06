package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Position {
    private int positionId;
    private String positionTitle;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String level;
    private LocalDateTime createdAt;
    
    public Position() {}
    
    public Position(String positionTitle, String description, 
                   BigDecimal minSalary, BigDecimal maxSalary, String level) {
        this.positionTitle = positionTitle;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.level = level;
    }
    
    // Getters & Setters
    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }
    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }
    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return positionTitle + " (" + level + ")";
    }
}