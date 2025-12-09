package com.example.demo.repositories;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar por email (útil para login)
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(String role);

    // ✅ Verificar si existe un usuario con un correo dado
    boolean existsByEmail(String email);
}
