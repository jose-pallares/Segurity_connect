package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedido_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Pedido
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Relación con Producto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    @Transient
    public Double getSubtotal() {
        return cantidad != null && precioUnitario != null ? cantidad * precioUnitario : 0.0;
    }
}
