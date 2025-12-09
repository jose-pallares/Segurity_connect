package com.example.demo.service;

import com.example.demo.models.Product;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<Product> obtenerTodosLosProductos();

    // Busqueda parcial por nombre
    List<Product> buscarPorNombre(String nombre);

    // Filtrar por categoría (Long)
    List<Product> buscarPorCategoria(Long categoriaId);

    // Filtrar por subcategoría (Long)
    List<Product> buscarPorSubcategoria(Long subcategoryId);

    // Búsqueda combinada
    List<Product> buscarPorCategoriaYNombre(Long categoriaId, String nombre);

    // Importación CSV
    void importarProductosCsv(MultipartFile file) throws IOException;

    int importarProductosCsvConReporte(MultipartFile file) throws IOException;

    // Compatibilidad
    List<Product> findAll();

    // Filtrado general
    List<Product> obtenerProductosFiltrados(String ordenarPor, String orden, String nombre, Integer cantidad);

    Product obtenerProductoPorId(Long id);


    List<Product> buscarPorCategoriaYSubcategoriaYNombre(Long categoriaId, Long subcategoriaId, String nombre);
    
    Page<Product> obtenerProductosPaginados(int page, int size);


}
