package com.example.demo.repositories;

import com.example.demo.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

   
    Cart findByUserIdAndProductId(Long userId, Long productId);
}
