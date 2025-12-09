package com.example.demo.controllers;

import com.example.demo.models.Cart;
import com.example.demo.models.Pedido;
import com.example.demo.models.PedidoItem;
import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.PedidoRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Mostrar formulario de checkout
    @GetMapping
    public String showCheckout(HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        List<Cart> carrito = cartService.getCartByUserId(usuario.getId());

        if (carrito.isEmpty()) {
            model.addAttribute("error", "El carrito está vacío.");
            return "carrito";
        }

        model.addAttribute("carrito", carrito);
        return "checkout";
    }

    // Confirmar compra
    @PostMapping("/confirmar")
    public String confirmarCompra(
            @RequestParam String nombre,
            @RequestParam String telefono,
            @RequestParam String contacto,
            @RequestParam String direccion,
            @RequestParam String tipo_pago,
            @RequestParam(required = false) String tarjeta,
            HttpSession session,
            Model model) {

        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Cart> carrito = cartService.getCartByUserId(usuario.getId());
        if (carrito.isEmpty()) {
            model.addAttribute("error", "No hay productos en el carrito.");
            return "carrito";
        }

        // Crear pedido principal
        Pedido pedido = new Pedido();
        pedido.setUser(user);
        pedido.setNombre(nombre);
        pedido.setTelefono(telefono);
        pedido.setContacto(contacto);
        pedido.setDireccion(direccion);
        pedido.setTipoPago(tipo_pago);
        pedido.setFecha(LocalDateTime.now());

        if (!tipo_pago.equalsIgnoreCase("efectivo")) {
            pedido.setTarjeta(tarjeta);
        } else {
            pedido.setTarjeta("Pago en Efectivo");
        }

        // Crear items del pedido
        List<PedidoItem> items = new ArrayList<>();
        for (Cart item : carrito) {
            Product producto = item.getProduct();
            int cantidad = item.getCantidad();

            if (producto.getStock() < cantidad) {
                model.addAttribute("error", "No hay suficiente stock para: " + producto.getName());
                return "carrito";
            }

            // Actualizar stock y ventas
            producto.setStock(producto.getStock() - cantidad);
            producto.setCantidadVendida(producto.getCantidadVendida() + cantidad);
            productRepository.save(producto);

            // Crear item y asociar al pedido
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            pedidoItem.setProduct(producto);
            pedidoItem.setCantidad(cantidad);
            pedidoItem.setPrecioUnitario(producto.getPrice());
            items.add(pedidoItem);
        }

        pedido.setItems(items);
        pedidoRepository.save(pedido); // Guarda el pedido y sus items automáticamente

        // Intentar enviar correo de confirmación
        try {
            emailService.sendOrderConfirmation(user, pedido);
            model.addAttribute("mensajeEmail", "Se envió la confirmación al correo: " + user.getEmail());
        } catch (Exception ex) {
            logger.error("Error enviando correo de confirmación para pedido {}: {}", pedido.getId(), ex.getMessage(), ex);
            model.addAttribute("mensajeEmail", "No fue posible enviar la confirmación por correo. Por favor contacte soporte.");
        }

        // Vaciar carrito
        cartService.clearCart(usuario.getId());

        // Pasar datos a la vista usando getTotal() del pedido
        model.addAttribute("pedido", pedido);
        model.addAttribute("items", items);
        model.addAttribute("total", pedido.getTotal());
        model.addAttribute("mensaje", "Pedido confirmado correctamente.");

        return "compra_exitosa";
    }
}
