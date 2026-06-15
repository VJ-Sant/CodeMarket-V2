package com.codemarket.service;

import com.codemarket.dto.ProjectDtos.*;
import com.codemarket.entity.*;
import com.codemarket.exception.ApiException;
import com.codemarket.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public ProjectService(ProjectRepository projectRepository, OrderRepository orderRepository, ReviewRepository reviewRepository) {
        this.projectRepository = projectRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<ProjectResponse> list(String category, String search, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Project> projects = projectRepository.searchPublished(
                ProjectStatus.PUBLISHED,
                normalize(category),
                normalize(search),
                minPrice,
                maxPrice
        );
        return projects.stream().map(ProjectService::toProjectResponse).toList();
    }

    public ProjectResponse get(Long id) {
        return toProjectResponse(findProject(id));
    }

    public ProjectResponse create(ProjectRequest request, User seller) {
        requireSellerRole(seller);
        Project project = Project.builder()
                .title(request.title()).description(request.description()).price(request.price()).category(request.category())
                .techStack(request.techStack()).previewUrl(request.previewUrl()).sourceUrl(request.sourceUrl())
                .thumbnailUrl(request.thumbnailUrl()).status(request.status() == null ? ProjectStatus.PUBLISHED : request.status())
                .seller(seller).build();
        return toProjectResponse(projectRepository.save(project));
    }

    public ProjectResponse update(Long id, ProjectRequest request, User seller) {
        Project project = findProject(id);
        requireProjectOwnerOrAdmin(project, seller);
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setPrice(request.price());
        project.setCategory(request.category());
        project.setTechStack(request.techStack());
        project.setPreviewUrl(request.previewUrl());
        project.setSourceUrl(request.sourceUrl());
        project.setThumbnailUrl(request.thumbnailUrl());
        project.setStatus(request.status() == null ? project.getStatus() : request.status());
        return toProjectResponse(projectRepository.save(project));
    }

    public void delete(Long id, User seller) {
        Project project = findProject(id);
        requireProjectOwnerOrAdmin(project, seller);
        projectRepository.delete(project);
    }

    public OrderResponse buy(Long projectId, User buyer) {
        Project project = findProject(projectId);
        if (project.getSeller().getId().equals(buyer.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sellers cannot buy their own project");
        }
        if (orderRepository.existsByBuyerAndProject(buyer, project)) {
            throw new ApiException(HttpStatus.CONFLICT, "Project already purchased");
        }
        Order order = orderRepository.save(Order.builder().buyer(buyer).project(project).amount(project.getPrice()).build());
        return toOrderResponse(order);
    }

    public List<OrderResponse> purchases(User buyer) {
        return orderRepository.findByBuyer(buyer).stream().map(ProjectService::toOrderResponse).toList();
    }

    public List<OrderResponse> sales(User seller) {
        return orderRepository.findByProjectSeller(seller).stream().map(ProjectService::toOrderResponse).toList();
    }

    public DownloadResponse download(Long projectId, User user) {
        Project project = findProject(projectId);
        boolean isOwner = project.getSeller().getId().equals(user.getId());
        boolean hasPurchased = orderRepository.existsByBuyerAndProject(user, project);
        if (!isOwner && !hasPurchased && user.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Purchase this project before downloading the source");
        }
        return new DownloadResponse(project.getId(), project.getTitle(), project.getSourceUrl());
    }

    public ReviewResponse review(Long projectId, ReviewRequest request, User buyer) {
        Project project = findProject(projectId);
        if (!orderRepository.existsByBuyerAndProject(buyer, project)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only buyers can review a project");
        }
        if (reviewRepository.existsByBuyerAndProject(buyer, project)) {
            throw new ApiException(HttpStatus.CONFLICT, "Project already reviewed");
        }
        Review review = reviewRepository.save(Review.builder().buyer(buyer).project(project).rating(request.rating()).comment(request.comment()).build());
        return toReviewResponse(review);
    }

    public List<ReviewResponse> reviews(Long projectId) {
        return reviewRepository.findByProject(findProject(projectId)).stream().map(ProjectService::toReviewResponse).toList();
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private void requireSellerRole(User user) {
        if (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only sellers can create projects");
        }
    }

    private void requireProjectOwnerOrAdmin(Project project, User user) {
        if (!project.getSeller().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the seller can modify this project");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getTitle(), project.getDescription(), project.getPrice(), project.getCategory(),
                project.getTechStack(), project.getPreviewUrl(), project.getThumbnailUrl(), project.getStatus(),
                UserService.toResponse(project.getSeller()), project.getCreatedAt());
    }

    private static OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getProject().getId(), order.getProject().getTitle(), order.getAmount(), order.getStatus(), order.getCreatedAt());
    }

    private static ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(review.getId(), review.getRating(), review.getComment(), UserService.toResponse(review.getBuyer()), review.getCreatedAt());
    }
}
