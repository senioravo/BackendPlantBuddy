package com.example.BackendPlantBuddy.controller;

import com.example.BackendPlantBuddy.dto.PlantelPlantDTO;
import com.example.BackendPlantBuddy.service.PlantelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plantel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlantelController {
    
    private final PlantelService plantelService;
    
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<PlantelPlantDTO>> obtenerPlantelPorUsuario(@PathVariable Integer userId) {
        List<PlantelPlantDTO> plantel = plantelService.obtenerPlantelPorUsuario(userId);
        return ResponseEntity.ok(plantel);
    }
    
    @PostMapping("/agregar")
    public ResponseEntity<PlantelPlantDTO> agregarPlanta(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Integer productoId = (Integer) request.get("productoId");
            String customTitle = (String) request.get("customTitle");
            String notes = (String) request.get("notes");
            
            PlantelPlantDTO plantel = plantelService.agregarPlantaAlPlantel(userId, productoId, customTitle, notes);
            return ResponseEntity.ok(plantel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/regar")
    public ResponseEntity<PlantelPlantDTO> regarPlanta(@RequestBody Map<String, Integer> request) {
        try {
            Integer userId = request.get("userId");
            Integer productoId = request.get("productoId");
            
            PlantelPlantDTO plantel = plantelService.regarPlanta(userId, productoId);
            return ResponseEntity.ok(plantel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/actualizar-titulo")
    public ResponseEntity<PlantelPlantDTO> actualizarTitulo(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Integer productoId = (Integer) request.get("productoId");
            String customTitle = (String) request.get("customTitle");
            
            PlantelPlantDTO plantel = plantelService.actualizarTituloPersonalizado(userId, productoId, customTitle);
            return ResponseEntity.ok(plantel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/actualizar-notas")
    public ResponseEntity<PlantelPlantDTO> actualizarNotas(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Integer productoId = (Integer) request.get("productoId");
            String notes = (String) request.get("notes");
            
            PlantelPlantDTO plantel = plantelService.actualizarNotas(userId, productoId, notes);
            return ResponseEntity.ok(plantel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/toggle-notificaciones")
    public ResponseEntity<PlantelPlantDTO> toggleNotificaciones(@RequestBody Map<String, Integer> request) {
        try {
            Integer userId = request.get("userId");
            Integer productoId = request.get("productoId");
            
            PlantelPlantDTO plantel = plantelService.alternarNotificaciones(userId, productoId);
            return ResponseEntity.ok(plantel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminarPlanta(@RequestBody Map<String, Integer> request) {
        try {
            Integer userId = request.get("userId");
            Integer productoId = request.get("productoId");
            
            plantelService.eliminarPlantaDelPlantel(userId, productoId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
