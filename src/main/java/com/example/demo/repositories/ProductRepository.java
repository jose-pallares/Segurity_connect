package com.example.demo.repositories;

import com.example.demo.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    // Buscar por nombre
    List<Product> findByNameContainingIgnoreCase(String name);

    // Buscar por categoría
    List<Product> findByCategoryId(Long categoryId);

    // Buscar por subcategoría
    List<Product> findBySubcategoryId(Long subcategoryId);

    // Buscar por categoría + subcategoría (plain)
    List<Product> findByCategoryIdAndSubcategoryId(Long categoryId, Long subcategoryId);

    // Combinación de categoría + nombre
    List<Product> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name);

    // Combinación de subcategoría + nombre
    List<Product> findBySubcategoryIdAndNameContainingIgnoreCase(Long subcategoryId, String name);

    // Combinación completa categoría + subcategoría + nombre
    List<Product> findByCategoryIdAndSubcategoryIdAndNameContainingIgnoreCase(
            Long categoryId, Long subcategoryId, String name


    
    );

    Page<Product> findAll(Pageable pageable);

    List<Product> findTop5ByOrderByCantidadVendidaDesc();


}
