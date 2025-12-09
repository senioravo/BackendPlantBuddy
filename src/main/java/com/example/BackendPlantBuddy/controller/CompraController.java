package com.example.BackendPlantBuddy.controller;

import com.example.BackendPlantBuddy.dto.CompraDTO;
import com.example.BackendPlantBuddy.dto.CreateCompraRequestDTO;
import com.example.BackendPlantBuddy.entity.EstadoCompra;
import com.example.BackendPlantBuddy.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompraController {
    
    private final CompraService compraService;
    
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<CompraDTO>> obtenerComprasPorUsuario(@PathVariable Integer userId) {
        List<CompraDTO> compras = compraService.obtenerComprasPorUsuario(userId);
        return ResponseEntity.ok(compras);
    }
    
    @GetMapping("/{compraId}")
    public ResponseEntity<CompraDTO> obtenerCompraPorId(@PathVariable Integer compraId) {
        try {
            CompraDTO compra = compraService.obtenerCompraPorId(compraId);
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/crear")
    public ResponseEntity<?> crearCompra(@Valid @RequestBody CreateCompraRequestDTO request) {
        try {
            CompraDTO compra = compraService.crearCompra(request);
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/finalizar")
    public ResponseEntity<?> finalizarCompra(@Valid @RequestBody CreateCompraRequestDTO request) {
        try {
            CompraDTO compra = compraService.crearCompra(request);
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{compraId}/estado")
    public ResponseEntity<CompraDTO> actualizarEstado(
            @PathVariable Integer compraId,
            @RequestBody Map<String, String> request
    ) {
        try {
            String estadoStr = request.get("estado");
            EstadoCompra estado = EstadoCompra.valueOf(estadoStr);
            
            CompraDTO compra = compraService.actualizarEstadoCompra(compraId, estado);
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{compraId}/cancelar")
    public ResponseEntity<CompraDTO> cancelarCompra(@PathVariable Integer compraId) {
        try {
            CompraDTO compra = compraService.cancelarCompra(compraId);
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
