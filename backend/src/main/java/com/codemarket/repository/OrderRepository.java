package com.codemarket.repository;

import com.codemarket.entity.Order;
import com.codemarket.entity.Project;
import com.codemarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByBuyerAndProject(User buyer, Project project);
    List<Order> findByBuyer(User buyer);
    List<Order> findByProjectSeller(User seller);
}
