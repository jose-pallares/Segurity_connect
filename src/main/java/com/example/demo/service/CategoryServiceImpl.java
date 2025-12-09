package com.example.demo.service;

import com.example.demo.models.Category;
import com.example.demo.models.Subcategory;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Subcategory> findSubcategoriesByCategory(Long categoryId) {
        if (categoryId == null) return List.of();
        return subcategoryRepository.findByCategoryId(categoryId);
    }
}
