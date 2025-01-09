package com.example.kursovaya.Controllers;

import com.example.kursovaya.DTO.ReactionRequest;
import com.example.kursovaya.Entity.Reaction;
import com.example.kursovaya.Entity.ReactionType;
import com.example.kursovaya.Entity.Reviews;
import com.example.kursovaya.Entity.User;
import com.example.kursovaya.Repository.ReactionRepository;
import com.example.kursovaya.Repository.ReviewsRepository;
import com.example.kursovaya.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// REST-контроллер для управления реакциями (лайки и дизлайки на отзывы)
@RestController
@RequestMapping("/reactions")
public class ReactionController {

    private final ReactionRepository reactionRepository;
    private final ReviewsRepository reviewsRepository;
    private final UserRepository userRepository;

    public ReactionController(ReactionRepository reactionRepository, ReviewsRepository reviewsRepository, UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.reviewsRepository = reviewsRepository;
        this.userRepository = userRepository;
    }

    // Метод для получения всех реакций текущего пользователя
    @GetMapping
    public ResponseEntity<?> getUserReactions(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");  // Получение текущего пользователя из сессии
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not authenticated")); // Возвращение ошибки 401, если пользователь не авторизован
        }
        List<Reaction> reactions = reactionRepository.findByUser(loggedInUser); // Получение списка реакций пользователя
        Map<Long, String> userReactions = reactions.stream()  // Преобразование списка реакций в Map, где ключ - ID отзыва, значение - тип реакции
                .collect(Collectors.toMap(
                        reaction -> reaction.getReview().getId(),
                        reaction -> reaction.getReactionType().name()
                ));
        return ResponseEntity.ok(userReactions); // Возвращение списка реакций пользователя
    }

    // Метод для добавления или обновления реакции
    @PostMapping
    public ResponseEntity<?> addReaction(@RequestBody ReactionRequest request, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user"); // Получение текущего пользователя из сессии
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not authenticated")); // Возвращение ошибки 401, если пользователь не авторизован
        }
        Reviews review = reviewsRepository.findById(request.getReviewId()) // Получение отзыва по ID или выброс исключения, если отзыв не найден
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Reaction existingReaction = reactionRepository.findByReviewAndUser(review, loggedInUser);  // Проверка, существует ли уже реакция на данный отзыв от текущего пользователя
        if (existingReaction != null) {
            if (existingReaction.getReactionType() == request.getReactionType()) { // Если тип реакции совпадает с новым запросом, удаляем существующую реакцию
                reactionRepository.delete(existingReaction);
                return ResponseEntity.ok().build();
            } else {
                existingReaction.setReactionType(request.getReactionType()); // Если тип реакции отличается, обновляем её
                reactionRepository.save(existingReaction);
                return ResponseEntity.ok().build();
            }
        }
        Reaction reaction = new Reaction(); // Если реакция отсутствует, создаем новую
        reaction.setReview(review);
        reaction.setUser(loggedInUser);
        reaction.setReactionType(request.getReactionType());
        reactionRepository.save(reaction); // Сохраняем новую реакцию в базе данных
        return ResponseEntity.ok().build();
    }
}