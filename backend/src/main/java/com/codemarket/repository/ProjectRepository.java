package com.codemarket.repository;

import com.codemarket.entity.Project;
import com.codemarket.entity.ProjectStatus;
import com.codemarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findBySeller(User seller);
    List<Project> findByCategoryIgnoreCaseAndStatus(String category, ProjectStatus status);

    @Query("""
            select p from Project p
            where p.status = :status
              and (:category is null or lower(p.category) = lower(:category))
              and (:search is null or lower(p.title) like lower(concat('%', :search, '%'))
                   or lower(p.description) like lower(concat('%', :search, '%'))
                   or lower(p.techStack) like lower(concat('%', :search, '%')))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            order by p.createdAt desc
            """)
    List<Project> searchPublished(@Param("status") ProjectStatus status,
                                  @Param("category") String category,
                                  @Param("search") String search,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice);
}
