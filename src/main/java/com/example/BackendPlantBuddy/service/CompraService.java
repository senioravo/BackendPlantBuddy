package com.example.BackendPlantBuddy.service;

import com.example.BackendPlantBuddy.dto.*;
import com.example.BackendPlantBuddy.entity.*;
import com.example.BackendPlantBuddy.mapper.EntityMapper;
import com.example.BackendPlantBuddy.repository.CompraRepository;
import com.example.BackendPlantBuddy.repository.ProductoRepository;
import com.example.BackendPlantBuddy.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompraService {
    
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntityMapper mapper;
    
    public List<CompraDTO> obtenerComprasPorUsuario(Integer userId) {
        return compraRepository.findByUsuario_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toCompraDTO)
                .collect(Collectors.toList());
    }
    
    public CompraDTO obtenerCompraPorId(Integer compraId) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
        return mapper.toCompraDTO(compra);
    }
    
    @Transactional
    public CompraDTO crearCompra(CreateCompraRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear la compra
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setShippingAddress(request.getShippingAddress());
        compra.setPaymentMethod(request.getPaymentMethod());
        compra.setStatus(EstadoCompra.PENDING);
        compra.setCreatedAt(LocalDateTime.now());
        
        // Procesar los items
        Set<DetalleCompra> detalles = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (CartItemRequestDTO item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoId()));
            
            // Verificar stock
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }
            
            // Crear detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            detalle.setSubtotal(subtotal);
            
            detalles.add(detalle);
            total = total.add(subtotal);
            
            // Actualizar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }
        
        compra.setTotal(total);
        compra.setDetalles(detalles);
        
        Compra guardada = compraRepository.save(compra);
        return mapper.toCompraDTO(guardada);
    }
    
    @Transactional
    public CompraDTO actualizarEstadoCompra(Integer compraId, EstadoCompra nuevoEstado) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
        
        compra.setStatus(nuevoEstado);
        
        Compra actualizada = compraRepository.save(compra);
        return mapper.toCompraDTO(actualizada);
    }
    
    @Transactional
    public CompraDTO cancelarCompra(Integer compraId) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
        
        if (compra.getStatus() == EstadoCompra.COMPLETED) {
            throw new RuntimeException("No se puede cancelar una compra completada");
        }
        
        // Restaurar stock
        for (DetalleCompra detalle : compra.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
        
        compra.setStatus(EstadoCompra.CANCELLED);
        
        Compra actualizada = compraRepository.save(compra);
        return mapper.toCompraDTO(actualizada);
    }
}
