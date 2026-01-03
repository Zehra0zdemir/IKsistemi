package com.hrms.service;

import com.hrms.model.Review;

import java.time.LocalDateTime;

public class ReviewService {

    // Controller ve View aynı şeyi konuşsun diye tek tip dönüş
    public record Result(boolean success, String message, Review review) {}

    public Result createReview(
            String evaluatorEmail,
            int employeeId,
            int tech, int comm, int team, int lead,
            String strengths,
            String improvements
    ) {
        if (evaluatorEmail == null || evaluatorEmail.isBlank())
            return new Result(false, "Değerlendiren email boş olamaz.", null);

        if (employeeId <= 0)
            return new Result(false, "Çalışan ID geçersiz.", null);

        String err;
        if ((err = validateScore(tech, "Teknik")) != null) return new Result(false, err, null);
        if ((err = validateScore(comm, "İletişim")) != null) return new Result(false, err, null);
        if ((err = validateScore(team, "Takım Çalışması")) != null) return new Result(false, err, null);
        if ((err = validateScore(lead, "Liderlik")) != null) return new Result(false, err, null);

        if (strengths == null || strengths.isBlank())
            return new Result(false, "Güçlü yönler boş olamaz.", null);

        if (improvements == null || improvements.isBlank())
            return new Result(false, "Gelişim alanları boş olamaz.", null);

        double avg = (tech + comm + team + lead) / 4.0;

        Review review = new Review(
                evaluatorEmail.trim(),
                employeeId,
                tech, comm, team, lead,
                strengths.trim(),
                improvements.trim(),
                avg,
                "SUBMITTED",
                LocalDateTime.now()
        );

        return new Result(true, "OK", review);
    }

    private String validateScore(int value, String name) {
        if (value < 1 || value > 10) return name + " puanı 1-10 arasında olmalı.";
        return null;
    }
}
