package com.example.demo.seeders;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByRole("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword("123456");
            admin.setRole("admin");

            userRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin@example.com / 123456");
        } else {
            System.out.println("ℹ️ Ya existe un usuario administrador.");
        }
    }
}
