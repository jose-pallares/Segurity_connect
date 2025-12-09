package com.example.demo.controllers;

import com.example.demo.models.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CatalogoController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/catalogo")
    public String verCatalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<Product> productos;

        // ==============================
        // LÓGICA DE FILTROS
        // ==============================
        if (categoryId == null && subcategoryId == null && (nombre == null || nombre.isEmpty())) {
            productos = productService.obtenerTodosLosProductos();
        }
        else if (categoryId != null && subcategoryId == null && (nombre == null || nombre.isEmpty())) {
            productos = productService.buscarPorCategoria(categoryId);
        }
        else if (subcategoryId != null && (nombre == null || nombre.isEmpty())) {
            productos = productService.buscarPorSubcategoria(subcategoryId);
        }
        else if (categoryId != null && subcategoryId != null && (nombre == null || nombre.isEmpty())) {
            productos = productService.buscarPorCategoriaYSubcategoriaYNombre(categoryId, subcategoryId, "");
        }
        else if (categoryId != null && subcategoryId != null && nombre != null) {
            productos = productService.buscarPorCategoriaYSubcategoriaYNombre(categoryId, subcategoryId, nombre);
        }
        else if (categoryId != null && nombre != null) {
            productos = productService.buscarPorCategoriaYNombre(categoryId, nombre);
        }
        else {
            productos = productService.buscarPorNombre(nombre);
        }

        // ==============================
        // PAGINACIÓN (12 productos)
        // ==============================
        int pageSize = 12;
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, productos.size());

        List<Product> paginaActual = productos.subList(fromIndex, toIndex);

        int totalPaginas = (int) Math.ceil((double) productos.size() / pageSize);

        // ==============================
        // CATEGORÍAS
        // ==============================
        var categorias = categoryService.findAll();
        var subcategorias = categoryService.findSubcategoriesByCategory(categoryId);

        for (Product p : paginaActual) {
            categorias.stream()
                    .filter(c -> c.getId().equals(p.getCategoryId()))
                    .findFirst()
                    .ifPresent(cat -> p.setCategoryName(cat.getName()));
        }

        // ==============================
        // ENVIAR A VISTA
        // ==============================
        model.addAttribute("productos", paginaActual);
        model.addAttribute("categorias", categorias);
        model.addAttribute("subcategorias", subcategorias);

        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);

        model.addAttribute("nombre", nombre);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("subcategoryId", subcategoryId);

        return "catalogo";
    }


    // ========================================
    // DETALLE DE PRODUCTO (CORREGIDO)
    // ========================================
    @GetMapping("/catalogo/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model) {

        Product producto = productService.obtenerProductoPorId(id);

        if (producto == null) {
            return "redirect:/catalogo";
        }

        var categorias = categoryService.findAll();
        categorias.stream()
                .filter(c -> c.getId().equals(producto.getCategoryId()))
                .findFirst()
                .ifPresent(cat -> producto.setCategoryName(cat.getName()));

        model.addAttribute("producto", producto);

        return "detalle-producto";
    }

}
