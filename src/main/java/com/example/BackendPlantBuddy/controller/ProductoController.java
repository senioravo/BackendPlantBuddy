package com.example.BackendPlantBuddy.controller;

import com.example.BackendPlantBuddy.dto.ProductoDTO;
import com.example.BackendPlantBuddy.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<ProductoDTO> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            ProductoDTO producto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscar(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoria
    ) {
        List<ProductoDTO> productos;
        
        if (categoria != null && !categoria.isEmpty()) {
            productos = productoService.buscarProductosPorCategoria(categoria, query);
        } else {
            productos = productoService.buscarProductos(query);
        }
        
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/destacados")
    public ResponseEntity<List<ProductoDTO>> obtenerDestacados() {
        List<ProductoDTO> productos = productoService.obtenerProductosDestacados();
        return ResponseEntity.ok(productos);
    }
}
