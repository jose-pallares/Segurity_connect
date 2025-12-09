package com.example.demo.listeners;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        User user = (User) event.getSession().getAttribute("usuario");
        if (user != null) {
            user.setEstado("Desconectado");
            userRepository.save(user);
        }
    }
}
