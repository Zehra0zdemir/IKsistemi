package com.hrms.services;

import com.hrms.model.Review;

import java.time.LocalDateTime;

public class ReviewService {

    public record Result(boolean success, String message, Review review) {}

    public Result createReview(
            String evaluatorEmail,
            int employeeId,
            int tech, int comm, int team, int lead,
            String strengths,
            String improvements,
            String goals
    ) {
        if (employeeId <= 0) {
            return new Result(false, "employeeId geçersiz", null);
        }

        // goals normalize
        if (goals != null && goals.isBlank()) goals = null;

        double overall = (tech + comm + team + lead) / 4.0;

        // ✅ Review constructor artık goals dahil 12 parametre
        Review review = new Review(
                evaluatorEmail,
                employeeId,
                tech, comm, team, lead,
                strengths,
                improvements,
                goals,              // ✅ EKLENDİ (12. parametre mantığı buradan geliyor)
                overall,
                "SUBMITTED",
                LocalDateTime.now()
        );

        // ❌ setGoals yoksa ÇAĞIRMA
        return new Result(true, "OK", review);
    }
}
