package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String estado = "Desconectado";  // Estado del usuario: "En línea" o "Desconectado"

    private String role;  // Rol del usuario: "admin" o "user"

    // 🔹 Campo para manejar suspensión manual o automática
    private boolean suspended = false;

    // 🔸 Indica si el usuario ha solicitado reactivación
    private boolean reactivationRequested = false;

    // 🔸 Fecha en la que el usuario pidió reactivación
    private LocalDateTime reactivationRequestDate;

    // 🕒 Fecha del último inicio de sesión
    @Column(name = "last_login")
    private LocalDateTime lastOnline;
    
    @Column(name = "estimated_suspend")
    private LocalDateTime estimatedSuspend;

    // 🔹 Si el usuario está activo o no (por inactividad)
    private boolean active = true;

    public User() {
    }

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.estado = "Desconectado";
        this.suspended = false;
        this.active = true;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public LocalDateTime getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(LocalDateTime lastOnline) {
        this.lastOnline = lastOnline;
    }
    
    public LocalDateTime getEstimatedSuspend() {
        return estimatedSuspend;
    }

    public void setEstimatedSuspend(LocalDateTime estimatedSuspend) {
        this.estimatedSuspend = estimatedSuspend;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isReactivationRequested() {
        return reactivationRequested;
    }

    public void setReactivationRequested(boolean reactivationRequested) {
        this.reactivationRequested = reactivationRequested;
    }

    public LocalDateTime getReactivationRequestDate() {
        return reactivationRequestDate;
    }

    public void setReactivationRequestDate(LocalDateTime reactivationRequestDate) {
        this.reactivationRequestDate = reactivationRequestDate;
    }
}
