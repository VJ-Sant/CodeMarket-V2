package com.codemarket.dto;

import com.codemarket.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ProjectDtos {
    private ProjectDtos() {}

    public record ProjectRequest(@NotBlank String title, @NotBlank String description, @NotNull @Positive BigDecimal price,
                                 String category, String techStack, String previewUrl, String sourceUrl,
                                 String thumbnailUrl, ProjectStatus status) {}
    public record ProjectResponse(Long id, String title, String description, BigDecimal price, String category,
                                  String techStack, String previewUrl, String thumbnailUrl,
                                  ProjectStatus status, AuthDtos.UserResponse seller, LocalDateTime createdAt) {}
    public record OrderResponse(Long id, Long projectId, String projectTitle, BigDecimal amount, OrderStatus status, LocalDateTime createdAt) {}
    public record ReviewRequest(@NotNull @Min(1) @Max(5) Integer rating, String comment) {}
    public record ReviewResponse(Long id, Integer rating, String comment, AuthDtos.UserResponse buyer, LocalDateTime createdAt) {}
    public record DownloadResponse(Long projectId, String projectTitle, String sourceUrl) {}
}
