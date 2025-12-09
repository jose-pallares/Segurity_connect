package com.example.demo.controllers;

import com.example.demo.models.Cart;
import com.example.demo.models.User;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/carrito")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Long userId = usuario.getId();
        List<Cart> items = cartService.getCartByUserId(userId);

        double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getCantidad())
                .sum();

        model.addAttribute("carrito", items);
        model.addAttribute("total", total);
        return "carrito";
    }

    @PostMapping("/agregar")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        cartService.addToCart(productId, usuario.getId());
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String removeFromCart(@RequestParam Long cartId, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        cartService.removeFromCart(cartId);
        return "redirect:/carrito";
    }

    @PostMapping("/editar")
    public String updateCantidad(@RequestParam Long cartId,
                                 @RequestParam Integer cantidad,
                                 HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        cartService.updateCantidad(cartId, cantidad);
        return "redirect:/carrito";
    }

    @PostMapping("/sumar")
    public String sumarCantidad(@RequestParam Long cartId, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Cart item = cartService.getItemById(cartId);
        if (item != null) {
            cartService.updateCantidad(cartId, item.getCantidad() + 1);
        }

        return "redirect:/carrito";
    }

    @PostMapping("/restar")
    public String restarCantidad(@RequestParam Long cartId, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Cart item = cartService.getItemById(cartId);

        if (item != null) {
            if (item.getCantidad() > 1) {
                cartService.updateCantidad(cartId, item.getCantidad() - 1);
            } else {
                cartService.removeFromCart(cartId);
            }
        }

        return "redirect:/carrito";
    }

    // ⭐ NUEVO — AJAX sin recargar la página
    @PostMapping("/actualizarCantidad")
    @ResponseBody
    public Map<String, Object> actualizarCantidadAjax(@RequestParam Long cartId,
                                                      @RequestParam Integer cantidad,
                                                      HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();

        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            respuesta.put("error", "no_login");
            return respuesta;
        }

        cartService.updateCantidad(cartId, cantidad);

        List<Cart> items = cartService.getCartByUserId(usuario.getId());

        double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getCantidad())
                .sum();

        Cart item = cartService.getItemById(cartId);

        respuesta.put("cantidad", item.getCantidad());
        respuesta.put("subtotal", item.getProduct().getPrice() * item.getCantidad());
        respuesta.put("total", total);

        return respuesta;
    }



    @GetMapping("/checkout")
public String checkout(HttpSession session, Model model) {
    User usuario = (User) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";

    List<Cart> items = cartService.getCartByUserId(usuario.getId());

    double total = items.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getCantidad())
            .sum();

    // se envían los datos al HTML
    model.addAttribute("carrito", items);
    model.addAttribute("total", total);

    return "checkout"; // vista que mostrarás después de confirmar
}


}
