package com.example.demo.controllers;

import com.example.demo.models.Cart;
import com.example.demo.models.Product;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/invitado")
public class InvitadoController {

    private static final String CART_SESSION = "carrito_invitado";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ========================================
    // INICIO INVITADO
    // ========================================
    @GetMapping("/inicio")
    public String inicioInvitado(Model model) {
        List<Product> topVendidos = productRepository.findTop5ByOrderByCantidadVendidaDesc();
        model.addAttribute("topVendidos", topVendidos);
        return "Invitado/InicioInvitado";
    }

    // ========================================
    // CATÁLOGO INVITADO
    // ========================================
    @GetMapping("/catalogo")
    public String catalogoInvitado(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            Model model) {

        List<Product> productos;

        if (categoryId == null && subcategoryId == null && (nombre == null || nombre.isEmpty())) {
            productos = productService.obtenerTodosLosProductos();
        } else if (categoryId != null && subcategoryId == null && (nombre == null || nombre.isEmpty())) {
            productos = productService.buscarPorCategoria(categoryId);
        } else if (subcategoryId != null && (nombre == null || nombre.isEmpty())) {
            productos = productService.buscarPorSubcategoria(subcategoryId);
        } else if (categoryId != null && subcategoryId != null && (nombre == null || nombre.isEmpty())) {
            productos = productService.buscarPorCategoriaYSubcategoriaYNombre(categoryId, subcategoryId, "");
        } else if (categoryId != null && subcategoryId != null && nombre != null) {
            productos = productService.buscarPorCategoriaYSubcategoriaYNombre(categoryId, subcategoryId, nombre);
        } else if (categoryId != null && nombre != null) {
            productos = productService.buscarPorCategoriaYNombre(categoryId, nombre);
        } else {
            productos = productService.buscarPorNombre(nombre);
        }

        var categorias = categoryService.findAll();
        var subcategorias = categoryService.findSubcategoriesByCategory(categoryId);

        for (Product p : productos) {
            categorias.stream()
                    .filter(c -> c.getId().equals(p.getCategoryId()))
                    .findFirst()
                    .ifPresent(cat -> p.setCategoryName(cat.getName()));
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("subcategorias", subcategorias);
        model.addAttribute("nombre", nombre);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("subcategoryId", subcategoryId);

        return "Invitado/catalogo-invitado";
    }

    // ========================================
    // DETALLE PRODUCTO INVITADO
    // ========================================
    @GetMapping("/producto/{id}")
    public String verDetalleProductoInvitado(@PathVariable Long id, Model model) {

        Product producto = productService.obtenerProductoPorId(id);

        if (producto == null) {
            return "redirect:/invitado/catalogo";
        }

        var categorias = categoryService.findAll();

        categorias.stream()
                .filter(c -> c.getId().equals(producto.getCategoryId()))
                .findFirst()
                .ifPresent(cat -> producto.setCategoryName(cat.getName()));

        model.addAttribute("producto", producto);

        return "Invitado/detalle-producto-invitado";
    }

    // ========================================
    // VER CARRITO
    // ========================================
    @GetMapping("/carrito")
    public String carritoInvitado(Model model, HttpSession session) {

        List<Cart> carrito = (List<Cart>) session.getAttribute(CART_SESSION);

        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        double total = carrito.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getCantidad())
                .sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        return "Invitado/carrito-invitado";
    }

    // ========================================
    // AGREGAR AL CARRITO
    // ========================================
    @GetMapping("/carrito/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id, HttpSession session) {

        Product producto = productRepository.findById(id).orElse(null);

        if (producto != null) {

            List<Cart> carrito = (List<Cart>) session.getAttribute(CART_SESSION);

            if (carrito == null) {
                carrito = new ArrayList<>();
            }

            boolean encontrado = false;

            for (Cart item : carrito) {
                if (item.getProduct().getId().equals(id)) {
                    item.setCantidad(item.getCantidad() + 1);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                Cart nuevo = new Cart();
                nuevo.setProduct(producto);
                nuevo.setCantidad(1);
                carrito.add(nuevo);
            }

            session.setAttribute(CART_SESSION, carrito);
        }

        return "redirect:/invitado/carrito";
    }

    // ========================================
    // SUMAR
    // ========================================
    @PostMapping("/carrito/sumar")
    public String sumarCantidad(@RequestParam Long productId, HttpSession session) {

        List<Cart> carrito = (List<Cart>) session.getAttribute(CART_SESSION);

        if (carrito != null) {
            for (Cart item : carrito) {
                if (item.getProduct().getId().equals(productId)) {
                    item.setCantidad(item.getCantidad() + 1);
                    break;
                }
            }
        }

        session.setAttribute(CART_SESSION, carrito);
        return "redirect:/invitado/carrito";
    }

    // ========================================
    // RESTAR
    // ========================================
    @PostMapping("/carrito/restar")
    public String restarCantidad(@RequestParam Long productId, HttpSession session) {

        List<Cart> carrito = (List<Cart>) session.getAttribute(CART_SESSION);

        if (carrito != null) {
            for (Cart item : carrito) {
                if (item.getProduct().getId().equals(productId)) {
                    if (item.getCantidad() > 1) {
                        item.setCantidad(item.getCantidad() - 1);
                    }
                    break;
                }
            }
        }

        session.setAttribute(CART_SESSION, carrito);
        return "redirect:/invitado/carrito";
    }

    // ========================================
    // ELIMINAR
    // ========================================
    @PostMapping("/carrito/eliminar")
    public String eliminarProducto(@RequestParam Long productId, HttpSession session) {

        List<Cart> carrito = (List<Cart>) session.getAttribute(CART_SESSION);

        if (carrito != null) {
            carrito.removeIf(item -> item.getProduct().getId().equals(productId));
        }

        session.setAttribute(CART_SESSION, carrito);
        return "redirect:/invitado/carrito";
    }
}
