package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    // PERMITIR DESCRIPCIONES LARGAS
    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "cantidad_vendida", nullable = false)
    private Integer cantidadVendida;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "subcategory_id")
    private Long subcategoryId;

    // Nombre visible en catálogo
    @Transient
    private String categoryName;

    // Nuevo: nombre visible de subcategoría
    @Transient
    private String subcategoryName;
}
