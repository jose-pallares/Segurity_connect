package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la tabla users
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Información del comprador
    private String nombre;
    private String telefono;
    private String contacto;
    private String direccion;

    // Método de pago
    private String tipoPago;
    private String tarjeta;

    private LocalDateTime fecha;

    // Relación con items del pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PedidoItem> items;

    public Double getTotal() {
        if (items == null) return 0.0;
        return items.stream()
                .filter(i -> i.getPrecioUnitario() != null && i.getCantidad() != null)
                .mapToDouble(i -> i.getCantidad() * i.getPrecioUnitario())
                .sum();
    }
}
