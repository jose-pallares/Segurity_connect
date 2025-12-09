package com.example.demo.service;

import com.example.demo.models.Product;
import com.example.demo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> obtenerTodosLosProductos() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> buscarPorNombre(String nombre) {
        return productRepository.findByNameContainingIgnoreCase(nombre);
    }

    @Override
    public List<Product> buscarPorCategoria(Long categoriaId) {
        return productRepository.findByCategoryId(categoriaId);
    }

    @Override
    public List<Product> buscarPorSubcategoria(Long subcategoryId) {
        return productRepository.findBySubcategoryId(subcategoryId);
    }

    @Override
    public List<Product> buscarPorCategoriaYNombre(Long categoriaId, String nombre) {
        return productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoriaId, nombre);
    }

    @Override
    public void importarProductosCsv(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            br.lines().skip(1).forEach(linea -> {
                String[] datos = linea.split(",");

                if (datos.length < 6) return;

                Product product = new Product();
                product.setName(datos[0]);
                product.setPrice(Double.valueOf(datos[1]));
                product.setDescription(datos[2]);
                product.setStock(Integer.valueOf(datos[3]));
                product.setCantidadVendida(Integer.valueOf(datos[4]));
                product.setCategoryId(Long.valueOf(datos[5]));

                if (datos.length > 6 && !datos[6].isEmpty()) {
                    product.setSubcategoryId(Long.valueOf(datos[6]));
                }

                if (!productRepository.existsByName(product.getName())) {
                    productRepository.save(product);
                }
            });
        }
    }

    @Override
    public int importarProductosCsvConReporte(MultipartFile file) throws IOException {
        int contadorNuevos = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            contadorNuevos = (int) br.lines().skip(1).map(linea -> {
                String[] datos = linea.split(",");

                if (datos.length < 6) return 0;

                Product product = new Product();
                product.setName(datos[0]);
                product.setPrice(Double.valueOf(datos[1]));
                product.setDescription(datos[2]);
                product.setStock(Integer.valueOf(datos[3]));
                product.setCantidadVendida(Integer.valueOf(datos[4]));
                product.setCategoryId(Long.valueOf(datos[5]));

                if (datos.length > 6 && !datos[6].isEmpty()) {
                    product.setSubcategoryId(Long.valueOf(datos[6]));
                }

                if (!productRepository.existsByName(product.getName())) {
                    productRepository.save(product);
                    return 1;
                }
                return 0;
            }).mapToInt(i -> i).sum();
        }

        return contadorNuevos;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> obtenerProductosFiltrados(String ordenarPor, String orden, String nombre, Integer cantidad) {
        List<Product> productos = productRepository.findAll();

        // Filtrar por nombre si existe
        if (nombre != null && !nombre.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getName() != null &&
                            p.getName().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Ordenación por ventas o stock
        if ("ventas".equalsIgnoreCase(ordenarPor)) {
            productos.sort(Comparator.comparing(Product::getCantidadVendida));
        } else {
            productos.sort(Comparator.comparing(Product::getStock));
        }

        // Orden Ascendente o Descendente
        if ("descendente".equalsIgnoreCase(orden)) {
            productos = productos.stream()
                    .sorted((a, b) -> {
                        if ("ventas".equalsIgnoreCase(ordenarPor)) {
                            return b.getCantidadVendida() - a.getCantidadVendida();
                        }
                        return b.getStock() - a.getStock();
                    })
                    .collect(Collectors.toList());
        }

        // TOP N (Cantidad de elementos a mostrar)
        if (cantidad != null && cantidad > 0) {
            productos = productos.stream()
                    .limit(cantidad)
                    .collect(Collectors.toList());
        }

        return productos;
    }


    @Override
    public Product obtenerProductoPorId(Long id) {
        return productRepository.findById(id).orElse(null);
    }


    @Override
    public List<Product> buscarPorCategoriaYSubcategoriaYNombre(Long categoriaId, Long subcategoriaId, String nombre) {
        return productRepository.findByCategoryIdAndSubcategoryIdAndNameContainingIgnoreCase(
            categoriaId, subcategoriaId, nombre
    );
    }
    
    @Override
    public Page<Product> obtenerProductosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }


}
