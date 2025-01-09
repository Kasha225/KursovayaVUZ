package com.example.kursovaya.DTO;

import com.example.kursovaya.Entity.ReactionType;
import lombok.Data;

@Data
public class ReactionRequest {
    private Long reviewId;
    private ReactionType reactionType;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}

