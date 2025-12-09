package com.example.demo.controllers;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/email")
public class EmailAdminController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String form(Model model) {
        return "admin-email";
    }

    @PostMapping("/send")
    public String sendEmail(
            @RequestParam("destinatarios") String destinatarios,
            @RequestParam("asunto") String asunto,
            @RequestParam("mensaje") String mensaje,
            Model model
    ) {
        try {
            String[] lista = destinatarios.split(",");

            emailService.sendCustomEmail(lista, asunto, mensaje);

            model.addAttribute("success", "Correo enviado correctamente.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al enviar correo: " + e.getMessage());
        }

        return "admin-email";
    }
}
