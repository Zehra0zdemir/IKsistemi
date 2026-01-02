package com.hrms.model;

import java.time.LocalDateTime;

public class Review {
    private final String evaluatorEmail;   // değerlendiren (şimdilik login olan)
    private final int employeeId;          // değerlendirilen (şimdilik elle gireceğiz / sonra seçmeli liste)
    private final int tech;
    private final int comm;
    private final int team;
    private final int lead;
    private final String strengths;
    private final String improvements;
    private final double overallRating;
    private final LocalDateTime createdAt;

    public Review(String evaluatorEmail,
                  int employeeId,
                  int tech, int comm, int team, int lead,
                  String strengths, String improvements,
                  double overallRating,
                  LocalDateTime createdAt) {
        this.evaluatorEmail = evaluatorEmail;
        this.employeeId = employeeId;
        this.tech = tech;
        this.comm = comm;
        this.team = team;
        this.lead = lead;
        this.strengths = strengths;
        this.improvements = improvements;
        this.overallRating = overallRating;
        this.createdAt = createdAt;
    }

    public String getEvaluatorEmail() { return evaluatorEmail; }
    public int getEmployeeId() { return employeeId; }
    public int getTech() { return tech; }
    public int getComm() { return comm; }
    public int getTeam() { return team; }
    public int getLead() { return lead; }
    public String getStrengths() { return strengths; }
    public String getImprovements() { return improvements; }
    public double getOverallRating() { return overallRating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
