package com.example.kursovaya.Repository;

import com.example.kursovaya.Entity.Reviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    @Query("SELECT r FROM Reviews r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Reviews> findByUserId(@Param("userId") Long userId);
    @Query("SELECT r FROM Reviews r ORDER BY r.createdAt DESC")
    Page<Reviews> findTop3Reviews(Pageable pageable);
    @Query("SELECT r FROM Reviews r ORDER BY r.createdAt DESC")
    Page<Reviews> findAllWithPagination(Pageable pageable);
    @Query("SELECT r FROM Reviews r LEFT JOIN FETCH r.tags WHERE r.id = :id")
    Optional<Reviews> findByIdWithTags(@Param("id") Long id);
}