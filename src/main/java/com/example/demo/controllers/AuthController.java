package com.example.demo.controllers;

import com.example.demo.models.Cart;
import com.example.demo.models.User;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ============================
    // MOSTRAR LOGIN
    // ============================
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {

        //  CREAR ID ÚNICO POR SESIÓN
        if (session.getAttribute("guest_id") == null) {
            session.setAttribute("guest_id", UUID.randomUUID().toString());
        }

        return "login";
    }

    // ============================
    // MOSTRAR REGISTRO
    // ============================
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    // ============================
    // REGISTRO
    // ============================
    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String confirmar,
                                   Model model) {

        if (!emailValido(email)) {
            model.addAttribute("error", "El correo no es válido.");
            return "registro";
        }

        if (!passwordValida(password)) {
            model.addAttribute("error", "Contraseña débil.");
            return "registro";
        }

        if (!password.equals(confirmar)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email ya registrado.");
            return "registro";
        }

        User usuario = new User();
        usuario.setName(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRole(email.equalsIgnoreCase("admin@gmail.com") ? "admin" : "user");
        usuario.setEstado("Desconectado");

        userRepository.save(usuario);

        model.addAttribute("mensaje", "Usuario registrado con éxito.");
        return "login";
    }

    // ============================
    // LOGIN (CORREGIDO 100%)
    // ============================
    @PostMapping("/acceder")
    public String iniciarSesion(@RequestParam String email,
                                @RequestParam String password,
                                Model model,
                                HttpSession session) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "login";
        }

        User user = userOptional.get();

        if (user.isSuspended()) {
            model.addAttribute("error", "Cuenta suspendida.");
            return "login";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "login";
        }

        //  Limpieza segura
        session.removeAttribute("usuario");

        user.setEstado("En línea");
        userRepository.save(user);

        session.setAttribute("usuario", user);

        //  SOLO TRANSFERIR CARRITO DE ESTA SESIÓN
        List<Cart> carritoInvitado = (List<Cart>) session.getAttribute("carrito_invitado");

        if (carritoInvitado != null && !carritoInvitado.isEmpty()) {

            for (Cart item : carritoInvitado) {

                Cart existente = cartRepository.findByUserIdAndProductId(
                        user.getId(),
                        item.getProduct().getId()
                );

                if (existente != null) {
                    existente.setCantidad(existente.getCantidad() + item.getCantidad());
                    cartRepository.save(existente);
                } else {

                    Cart nuevo = new Cart();
                    nuevo.setUserId(user.getId());
                    nuevo.setProduct(item.getProduct());
                    nuevo.setCantidad(item.getCantidad());
                    cartRepository.save(nuevo);
                }
            }

            //  limpiar el carrito invitado solo de ESA pestaña
            session.removeAttribute("carrito_invitado");
        }

        return "redirect:/inicio";
    }

    // ============================
    // LOGOUT
    // ============================
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        User usuario = (User) session.getAttribute("usuario");

        if (usuario != null) {
            usuario.setEstado("Desconectado");
            userRepository.save(usuario);
        }

        session.invalidate();
        return "redirect:/principal";
    }

    // ============================
    // VALIDACIONES
    // ============================
    private boolean emailValido(String email) {
        String regex = "^(?![0-9]+$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    private boolean passwordValida(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
        return password.matches(regex);
    }
}
