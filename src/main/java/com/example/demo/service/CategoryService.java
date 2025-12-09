package com.example.demo.service;

import com.example.demo.models.Category;
import com.example.demo.models.Subcategory;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    List<Subcategory> findSubcategoriesByCategory(Long categoryId);
}
