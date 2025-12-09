package com.example.demo.repositories;

import com.example.demo.models.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    List<Subcategory> findByCategoryId(Long categoryId);
}
