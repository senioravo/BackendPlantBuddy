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
        System.out.println("========================================");
        System.out.println("POST /compras/crear recibido");
        System.out.println("UserId: " + request.getUserId());
        System.out.println("Items: " + request.getItems().size());
        System.out.println("Dirección: " + request.getShippingAddress());
        System.out.println("Método de pago: " + request.getPaymentMethod());
        System.out.println("========================================");
        
        try {
            CompraDTO compra = compraService.crearCompra(request);
            System.out.println("Compra creada exitosamente. ID: " + compra.getId());
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            System.err.println("ERROR al crear compra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/finalizar")
    public ResponseEntity<?> finalizarCompra(@Valid @RequestBody CreateCompraRequestDTO request) {
        System.out.println("========================================");
        System.out.println("PUT /compras/finalizar recibido");
        System.out.println("UserId: " + request.getUserId());
        System.out.println("Items: " + request.getItems().size());
        System.out.println("Dirección: " + request.getShippingAddress());
        System.out.println("Método de pago: " + request.getPaymentMethod());
        System.out.println("========================================");
        
        try {
            CompraDTO compra = compraService.crearCompra(request);
            System.out.println("Compra creada exitosamente. ID: " + compra.getId());
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            System.err.println("ERROR al crear compra: " + e.getMessage());
            e.printStackTrace();
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
