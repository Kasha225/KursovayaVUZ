package com.example.kursovaya.Services;

import com.example.kursovaya.Entity.Reviews;
import com.example.kursovaya.Repository.ReactionRepository;
import com.example.kursovaya.Repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewsService {
    @Autowired
    private ReactionRepository reactionRepository;

    private final ReviewsRepository reviewRepository;

    public ReviewsService(ReviewsRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Reviews> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Reviews> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Reviews saveReview(Reviews review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

}