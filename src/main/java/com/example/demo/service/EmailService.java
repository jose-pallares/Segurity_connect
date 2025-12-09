package com.example.demo.service;

import com.example.demo.models.Pedido;
import com.example.demo.models.User;

public interface EmailService {
    void sendOrderConfirmation(User user, Pedido pedido) throws Exception;
    
    void sendCustomEmail(String[] recipients, String subject, String message) throws Exception;
}

