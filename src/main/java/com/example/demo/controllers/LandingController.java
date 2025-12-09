package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    // Cuando entres a http://localhost:8080/
    @GetMapping("/")
    public String mostrarPrincipal() {
        return "principal"; // Muestra la vista principal.html
    }

    // También puedes mantener esta ruta si alguien entra manualmente a /principal
    @GetMapping("/principal")
    public String principal() {
        return "principal";
    }
}
