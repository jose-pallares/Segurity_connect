// package com.example.demo.controllers;

// import com.example.demo.models.Pedido;
// import com.example.demo.repositories.PedidoRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// import java.security.Principal;
// import java.util.List;

// @Controller
// public class InicioController {

//     @Autowired
//     private PedidoRepository pedidoRepository;

//     @GetMapping("/inicio")
//     public String inicioDelUsuario(Model model, Principal principal) {

//         // SI NO ESTÁ LOGUEADO → enviar al login
//         if (principal == null) {
//             return "redirect:/login";
//         }

//         // Usuario logueado (Spring Security lo da automático)
//         String email = principal.getName();

//         // Buscar pedidos del usuario
//         List<Pedido> pedidos = pedidoRepository.findByUserEmailOrderByFechaDesc(email);

//         // Enviar a la vista
//         model.addAttribute("pedidos", pedidos);

//         return "inicio";  // Tu template inicio.html
//     }
// }
