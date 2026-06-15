package com.codemarket.repository;

import com.codemarket.entity.Project;
import com.codemarket.entity.Review;
import com.codemarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBuyerAndProject(User buyer, Project project);
    List<Review> findByProject(Project project);
}
