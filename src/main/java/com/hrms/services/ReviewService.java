package com.hrms.service;

import com.hrms.model.Review;

import java.time.LocalDateTime;

public class ReviewService {

    public record Result(boolean success, String message, Review review) {}

    public Result createReview(
            String evaluatorEmail,
            int employeeId,
            int tech, int comm, int team, int lead,
            String strengths,
            String improvements
    ) {
        // 1) Basit validasyonlar
        if (evaluatorEmail == null || evaluatorEmail.isBlank())
            return new Result(false, "Değerlendiren email boş olamaz.", null);

        if (employeeId <= 0)
            return new Result(false, "Çalışan ID geçersiz.", null);

        String scoreErr = validateScore(tech, "Teknik");
        if (scoreErr != null) return new Result(false, scoreErr, null);

        scoreErr = validateScore(comm, "İletişim");
        if (scoreErr != null) return new Result(false, scoreErr, null);

        scoreErr = validateScore(team, "Takım Çalışması");
        if (scoreErr != null) return new Result(false, scoreErr, null);

        scoreErr = validateScore(lead, "Liderlik");
        if (scoreErr != null) return new Result(false, scoreErr, null);

        if (strengths == null || strengths.isBlank())
            return new Result(false, "Güçlü yönler boş olamaz.", null);

        if (improvements == null || improvements.isBlank())
            return new Result(false, "Gelişim alanları boş olamaz.", null);

        // 2) Ortalama
        double avg = (tech + comm + team + lead) / 4.0;

        // 3) Review nesnesi üret
        Review review = new Review(
                evaluatorEmail.trim(),
                employeeId,
                tech, comm, team, lead,
                strengths.trim(),
                improvements.trim(),
                avg,
                LocalDateTime.now()
        );

        return new Result(true, "OK", review);
    }

    private String validateScore(int value, String fieldName) {
        if (value < 1 || value > 10) {
            return fieldName + " puanı 1-10 arasında olmalı.";
        }
        return null;
    }
}
