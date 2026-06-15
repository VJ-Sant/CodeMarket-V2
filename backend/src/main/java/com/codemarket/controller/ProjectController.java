package com.codemarket.controller;

import com.codemarket.dto.ProjectDtos.*;
import com.codemarket.entity.User;
import com.codemarket.service.ProjectService;
import com.codemarket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/projects")

    public List<ProjectResponse> list(@RequestParam(required = false) String category,
                                      @RequestParam(required = false) String search,
                                      @RequestParam(required = false) BigDecimal minPrice,
                                      @RequestParam(required = false) BigDecimal maxPrice) {
        return projectService.list(category, search, minPrice, maxPrice);

    }

    @GetMapping("/projects/{id}")
    public ProjectResponse get(@PathVariable Long id) {
        return projectService.get(id);
    }

    @PostMapping("/projects")
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request, Authentication authentication) {
        return projectService.create(request, currentUser(authentication));
    }

    @PutMapping("/projects/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, Authentication authentication) {
        return projectService.update(id, request, currentUser(authentication));
    }

    @DeleteMapping("/projects/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        projectService.delete(id, currentUser(authentication));
    }

    @PostMapping("/projects/{id}/buy")
    public OrderResponse buy(@PathVariable Long id, Authentication authentication) {
        return projectService.buy(id, currentUser(authentication));
    }


    @GetMapping("/projects/{id}/download")
    public DownloadResponse download(@PathVariable Long id, Authentication authentication) {
        return projectService.download(id, currentUser(authentication));
    }


    @PostMapping("/projects/{id}/reviews")
    public ReviewResponse review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request, Authentication authentication) {
        return projectService.review(id, request, currentUser(authentication));
    }

    @GetMapping("/projects/{id}/reviews")
    public List<ReviewResponse> reviews(@PathVariable Long id) {
        return projectService.reviews(id);
    }

    @GetMapping("/me/purchases")
    public List<OrderResponse> purchases(Authentication authentication) {
        return projectService.purchases(currentUser(authentication));
    }

    @GetMapping("/me/sales")
    public List<OrderResponse> sales(Authentication authentication) {
        return projectService.sales(currentUser(authentication));
    }

    private User currentUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }
}
