package com.example.demo.controllers;

import com.example.demo.models.Pedido;
import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.PedidoRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductRepository productRepository;

    // Mostrar inicio cuando accedes a "/inicio"
    @GetMapping("/inicio")
    public String mostrarInicio(HttpSession session, Model model) {

        User usuario = (User) session.getAttribute("usuario");

        if (usuario != null) {
            model.addAttribute("usuario", usuario);

            // Últimos pedidos del usuario
            List<Pedido> pedidos = pedidoRepository.findByUserOrderByFechaDesc(usuario);
            model.addAttribute("pedidos", pedidos);
        }

        // 🔥 TOP 5 productos más vendidos
        List<Product> topVendidos = productRepository.findTop5ByOrderByCantidadVendidaDesc();
        model.addAttribute("topVendidos", topVendidos);

        return "inicio";
    }
}
