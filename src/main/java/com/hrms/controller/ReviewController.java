package com.hrms.controller;

import com.hrms.dao.ReviewDAO;
import com.hrms.service.ReviewService;

public class ReviewController {

    private static final ReviewService reviewService = new ReviewService();
    private static final ReviewDAO reviewDAO = new ReviewDAO();

    public static ReviewService.Result submit(
            String evaluatorEmail,
            int employeeId,
            int tech, int comm, int team, int lead,
            String strengths,
            String improvements,
            String goals
    ) {
        var res = reviewService.createReview(
                evaluatorEmail,
                employeeId,
                tech, comm, team, lead,
                strengths,
                improvements,
                goals
        );

        if (!res.success()) return res;

        try {
            int id = reviewDAO.insert(res.review());
            res.review().setReviewId(id);

            return new ReviewService.Result(
                    true,
                    "DB'ye kaydedildi. ID=" + id,
                    res.review()
            );

        } catch (Exception ex) {
            return new ReviewService.Result(
                    false,
                    "DB hatası: " + ex.getMessage(),
                    null
            );
        }
    }
}
