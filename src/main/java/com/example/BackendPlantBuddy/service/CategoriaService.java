package com.example.BackendPlantBuddy.service;

import com.example.BackendPlantBuddy.dto.CategoriaDTO;
import com.example.BackendPlantBuddy.entity.Categoria;
import com.example.BackendPlantBuddy.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerCategoriaPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
        return convertirADTO(categoria);
    }
    
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerCategoriaPorNombre(String nombre) {
        Categoria categoria = categoriaRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con nombre: " + nombre));
        return convertirADTO(categoria);
    }
    
    private CategoriaDTO convertirADTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .createdAt(categoria.getCreatedAt())
                .updatedAt(categoria.getUpdatedAt())
                .build();
    }
}
