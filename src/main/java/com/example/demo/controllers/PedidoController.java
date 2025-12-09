package com.example.demo.controllers;

import com.example.demo.models.Pedido;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Página completa de Pedidos (para sección Pedidos en admin-panel)
    @GetMapping("/pedidos")
    @Transactional
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoRepository.findAll();
        cargarRelaciones(pedidos);

        // Ordenar por fecha DESC, evitando NPE
        pedidos.sort((p1, p2) -> {
            if (p1.getFecha() == null) return 1;
            if (p2.getFecha() == null) return -1;
            return p2.getFecha().compareTo(p1.getFecha());
        });

        model.addAttribute("pedidos", pedidos);
        return "pedidos"; // Thymeleaf: pedidos.html
    }

    // Fragmento de tabla de pedidos para incluir en admin-panel
    @GetMapping("/pedidos/tabla")
    @Transactional
    public String tablaPedidos(Model model) {
        List<Pedido> pedidos = pedidoRepository.findAll();
        cargarRelaciones(pedidos);

        // Ordenar por fecha DESC, evitando NPE
        pedidos.sort((p1, p2) -> {
            if (p1.getFecha() == null) return 1;
            if (p2.getFecha() == null) return -1;
            return p2.getFecha().compareTo(p1.getFecha());
        });

        model.addAttribute("pedidos", pedidos);
        return "tabla-pedidos :: tabla";
    }

    // Cargar relaciones para evitar LazyInitializationException
    private void cargarRelaciones(List<Pedido> pedidos) {
        pedidos.forEach(pedido -> {

            // Usuario
            if (pedido.getUser() != null) {
                pedido.getUser().getName(); // CORREGIDO: ahora coincide con el modelo User
            }

            // Items protegidos contra null
            if (pedido.getItems() != null) {
                pedido.getItems().forEach(item -> {
                    if (item.getProduct() != null) {
                        item.getProduct().getName();
                    }
                });
            }
        });
    }
}
