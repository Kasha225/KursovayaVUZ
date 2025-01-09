package com.example.kursovaya.Repository;

import com.example.kursovaya.Entity.Reaction;
import com.example.kursovaya.Entity.ReactionType;
import com.example.kursovaya.Entity.Reviews;
import com.example.kursovaya.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByReviewAndUser(Reviews review, User user);
    long countByReviewAndReactionType(Reviews review, ReactionType reactionType);
    List<Reaction> findByUser(User user);
}