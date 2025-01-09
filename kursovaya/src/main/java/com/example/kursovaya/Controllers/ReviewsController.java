package com.example.kursovaya.Controllers;

import com.example.kursovaya.Entity.Reviews;
import com.example.kursovaya.Entity.Tag;
import com.example.kursovaya.Entity.User;
import com.example.kursovaya.Repository.ReactionRepository;
import com.example.kursovaya.Repository.ReviewsRepository;
import com.example.kursovaya.Repository.TagRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

// Контроллер для управления отзывами
@Controller
public class ReviewsController {

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private TagRepository tagRepository;

    // Метод для отображения отзывов текущего пользователя
    @GetMapping("/myreviews")
    public String showUserReviews(
            HttpSession session,
            Model model) {
        User loggedInUser = (User) session.getAttribute("user"); // Получение текущего пользователя из сессии
        if (loggedInUser == null) {
            return "redirect:/login"; // Перенаправление на страницу входа, если пользователь не авторизован
        }
        List<Reviews> userReviews = reviewsRepository.findByUserId(loggedInUser.getId());  // Получение отзывов пользователя из базы данны
        model.addAttribute("user", loggedInUser);
        model.addAttribute("userReviews", userReviews);
        return "myreviews";
    }

    // Метод для отображения страницы с отзывами
    @GetMapping("/reviews")
    public String showReviewPage(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            HttpSession session,
            Model model) {
        User loggedInUser = (User) session.getAttribute("user");   // Получение текущего пользователя из сессии

        if (loggedInUser == null) {
            return "redirect:/login"; // Перенаправление на страницу входа, если пользователь не авторизован
        }

        Pageable topThreePageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));  // Получение топ-3 отзывов (по дате создания, по убыванию)
        List<Reviews> topThreeReviews = reviewsRepository.findTop3Reviews(topThreePageable).getContent();
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt")); // Получение пагинированного списка отзывов (по 5 отзывов на страницу)
        Page<Reviews> paginatedReviews = reviewsRepository.findAllWithPagination(pageable);

        model.addAttribute("user", loggedInUser);
        model.addAttribute("topReviews", topThreeReviews);
        model.addAttribute("paginatedReviews", paginatedReviews.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginatedReviews.getTotalPages());

        return "reviews";
    }

    // Метод для добавления нового отзыва
    @PostMapping("/reviews")
    public String submitReview(
            @RequestParam String content,
            @RequestParam int rating,
            @RequestParam(required = false) String selectedTag,
            HttpSession session,
            Model model) {
        User loggedInUser = (User) session.getAttribute("user"); // Получение текущего пользователя из сессии
        if (loggedInUser == null) {
            return "redirect:/login"; // Перенаправление на страницу входа, если пользователь не авторизован
        }
        if (rating < 1 || rating > 5) { // Проверка корректности рейтинга
            model.addAttribute("error", "Rating must be between 1 and 5!");
            return "reviews";
        }
        Reviews review = new Reviews(); // Создание нового отзыва
        review.setUser(loggedInUser);
        review.setContent(content);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        if (review.getTags() == null) {
            review.setTags(new HashSet<>());
        }

        if (selectedTag != null && !selectedTag.isEmpty()) {
            Tag tag = tagRepository.findByName(selectedTag)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(selectedTag);
                        return tagRepository.save(newTag);
                    });
            review.getTags().add(tag); // Связываем отзыв с тегом
        }

        reviewsRepository.save(review); // Сохранение отзыва в базе данных
        model.addAttribute("message", "Review submitted successfully!");
        return "redirect:/reviews";
    }

    // Метод для удаления отзыва
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("user"); // Получение текущего пользователя из сессии
        if (loggedInUser == null) {
            return "redirect:/login"; // Перенаправление на страницу входа, если пользователь не авторизован
        }
        Reviews review = reviewsRepository.findById(id) // Получение отзыва по его ID или выброс исключения, если он не найден
                .orElseThrow(() -> new IllegalArgumentException("Отзыв с ID " + id + " не найден."));

        if (!review.getUser().getId().equals(loggedInUser.getId())) { // Проверка, является ли пользователь владельцем отзыва
            throw new SecurityException("Вы не можете удалить чужой отзыв.");
        }
        reviewsRepository.delete(review);  // Удаление отзыва из базы данных
        return "redirect:/myreviews";
    }

}
