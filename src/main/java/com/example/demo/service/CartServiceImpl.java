package com.example.demo.service;

import com.example.demo.models.Cart;
import com.example.demo.models.Product;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public void addToCart(Long productId, Long userId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        Cart existente = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + 1);
            cartRepository.save(existente);
        } else {
            Cart nuevo = new Cart();
            nuevo.setProduct(product);
            nuevo.setUserId(userId);
            nuevo.setCantidad(1);
            cartRepository.save(nuevo);
        }
    }

    @Override
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public void clearCart(Long userId) {
        List<Cart> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
    }

    @Override
    public void updateCantidad(Long cartId, Integer cantidad) {
        Cart item = cartRepository.findById(cartId).orElse(null);

        if (item != null && cantidad != null && cantidad > 0) {
            item.setCantidad(cantidad);
            cartRepository.save(item);
        }
    }

    @Override
    public void vaciarCarrito(Long userId) {
        clearCart(userId);
    }

    @Override
    public Cart getItemById(Long cartId) {
        return cartRepository.findById(cartId).orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void confirmarCompra(Long userId) {
        List<Cart> carrito = cartRepository.findByUserId(userId);

        for (Cart item : carrito) {
            Product producto = item.getProduct();
            int cantidadComprada = item.getCantidad();

            if (producto.getStock() >= cantidadComprada) {
                producto.setStock(producto.getStock() - cantidadComprada);
                producto.setCantidadVendida(producto.getCantidadVendida() + cantidadComprada);
                productRepository.save(producto);
            } else {
                throw new RuntimeException("No hay suficiente stock para: " + producto.getName());
            }
        }

        clearCart(userId);
    }
}
