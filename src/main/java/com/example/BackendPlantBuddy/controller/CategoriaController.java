package com.example.BackendPlantBuddy.controller;

import com.example.BackendPlantBuddy.dto.CategoriaDTO;
import com.example.BackendPlantBuddy.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodas() {
        List<CategoriaDTO> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            CategoriaDTO categoria = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<CategoriaDTO> buscarPorNombre(@RequestParam String nombre) {
        try {
            CategoriaDTO categoria = categoriaService.obtenerCategoriaPorNombre(nombre);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
