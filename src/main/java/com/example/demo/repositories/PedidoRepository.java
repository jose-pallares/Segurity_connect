package com.example.demo.repositories;

import com.example.demo.models.Pedido;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUserOrderByFechaDesc(User user);
    List<Pedido> findByUserEmailOrderByFechaDesc(String email);


}
