package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.models.Product;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    // Encriptar contraseñas
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Mostrar panel de administración (usuarios + productos + pedidos)
    @GetMapping
    public String index(HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        // Cargar usuarios y productos
        List<User> usuarios = userRepository.findAll();
        List<Product> productos = productService.obtenerTodosLosProductos();

        // Cargar pedidos desde los productos (si hay lista de pedidos asociada)
        // Ejemplo: cada producto tiene lista de pedidos, o se pueden obtener desde el servicio
        // (ajústalo según tu modelo)
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("productos", productos);

        //Mostrar el panel principal
        return "admin-panel";
    }

    //Crear usuario (vista)
    @GetMapping("/create")
    public String create(HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("user", new User());
        return "users/create";
    }

    // Guardar usuario
    @PostMapping
    public String store(@ModelAttribute User user, HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        // Verificar si ya existe correo
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo ya existe");
            model.addAttribute("user", user);
            return "users/create";
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Refrescar datos del panel
        List<User> usuarios = userRepository.findAll();
        List<Product> productos = productService.obtenerTodosLosProductos();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("productos", productos);
        model.addAttribute("mensaje", "Usuario creado correctamente.");

        return "admin-panel";
    }

    // Editar usuario
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "users/edit";
        } else {
            return "redirect:/users";
        }
    }

    // Actualizar usuario
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute User updatedUser, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            userRepository.save(user);
        }

        return "redirect:/users";
    }

    // Suspender usuario
    @GetMapping("/delete/{id}")
    public String suspend(@PathVariable Long id, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setSuspended(true);
            userRepository.save(user);
        }

        return "redirect:/users";
    }

    // Reactivar usuario
    @GetMapping("/reactivate/{id}")
    public String reactivate(@PathVariable Long id, HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setSuspended(false);
            userRepository.save(user);
        }

        return "redirect:/users";
    }
}
