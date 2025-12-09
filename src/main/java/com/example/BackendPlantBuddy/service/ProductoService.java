package com.example.BackendPlantBuddy.service;

import com.example.BackendPlantBuddy.dto.ProductoDTO;
import com.example.BackendPlantBuddy.entity.Producto;
import com.example.BackendPlantBuddy.mapper.EntityMapper;
import com.example.BackendPlantBuddy.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final EntityMapper mapper;
    
    public List<ProductoDTO> obtenerTodosLosProductos() {
        return productoRepository.findByDisponibleTrue()
                .stream()
                .map(mapper::toProductoDTO)
                .collect(Collectors.toList());
    }
    
    public ProductoDTO obtenerProductoPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return mapper.toProductoDTO(producto);
    }
    
    public List<ProductoDTO> buscarProductos(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodosLosProductos();
        }
        
        return productoRepository.buscarProductos(busqueda)
                .stream()
                .map(mapper::toProductoDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDTO> buscarProductosPorCategoria(String categoria, String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return productoRepository.findByCategoria_NombreAndDisponibleTrue(categoria)
                    .stream()
                    .map(mapper::toProductoDTO)
                    .collect(Collectors.toList());
        }
        
        return productoRepository.buscarProductosPorCategoria(busqueda, categoria)
                .stream()
                .map(mapper::toProductoDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDTO> obtenerProductosDestacados() {
        return productoRepository.findTop10ByDisponibleTrueOrderByRatingDesc()
                .stream()
                .map(mapper::toProductoDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductoDTO actualizarStock(Integer id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        producto.setStock(nuevoStock);
        producto.setDisponible(nuevoStock > 0);
        
        Producto actualizado = productoRepository.save(producto);
        return mapper.toProductoDTO(actualizado);
    }
}
