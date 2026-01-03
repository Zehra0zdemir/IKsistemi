package com.hrms.model;

import java.time.LocalDateTime;

public class Review {

    private int reviewId;                 // DB'den gelen id
    private final String evaluatorEmail;  // değerlendiren
    private final int employeeId;         // değerlendirilen

    private final int tech;
    private final int comm;
    private final int team;
    private final int lead;

    private final String strengths;
    private final String improvements;

    private final String goals;           // ✅ hedefler eklendi

    private final double overallRating;
    private final String status;          // SUBMITTED, APPROVED vs
    private final LocalDateTime createdAt;

    public Review(
            String evaluatorEmail,
            int employeeId,
            int tech, int comm, int team, int lead,
            String strengths,
            String improvements,
            String goals,
            double overallRating,
            String status,
            LocalDateTime createdAt
    ) {
        this.evaluatorEmail = evaluatorEmail;
        this.employeeId = employeeId;
        this.tech = tech;
        this.comm = comm;
        this.team = team;
        this.lead = lead;
        this.strengths = strengths;
        this.improvements = improvements;
        this.goals = goals;
        this.overallRating = overallRating;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- GETTERS ---

    public int getReviewId() { return reviewId; }
    public String getEvaluatorEmail() { return evaluatorEmail; }
    public int getEmployeeId() { return employeeId; }
    public int getTech() { return tech; }
    public int getComm() { return comm; }
    public int getTeam() { return team; }
    public int getLead() { return lead; }
    public String getStrengths() { return strengths; }
    public String getImprovements() { return improvements; }
    public String getGoals() { return goals; }      // ✅ getter
    public double getOverallRating() { return overallRating; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // --- SETTER (sadece DB'den gelen ID için) ---
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }
}
