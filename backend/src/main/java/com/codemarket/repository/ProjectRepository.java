package com.codemarket.repository;

import com.codemarket.entity.Project;
import com.codemarket.entity.ProjectStatus;
import com.codemarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findBySeller(User seller);
    List<Project> findByCategoryIgnoreCaseAndStatus(String category, ProjectStatus status);
}
