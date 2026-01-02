package com.hrms.controller;

import com.hrms.service.ReviewService;

public class ReviewController {

    private static final ReviewService reviewService = new ReviewService();

    public static ReviewService.Result submit(
            String evaluatorEmail,
            int employeeId,
            int tech, int comm, int team, int lead,
            String strengths,
            String improvements
    ) {
        // Şimdilik sadece service çağırıyoruz.
        // Sonraki adım: DAO ile DB’ye insert.
        return reviewService.createReview(
                evaluatorEmail, employeeId,
                tech, comm, team, lead,
                strengths, improvements
        );
    }
}
