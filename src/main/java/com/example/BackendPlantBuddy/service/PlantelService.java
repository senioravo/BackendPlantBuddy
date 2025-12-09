package com.example.BackendPlantBuddy.service;

import com.example.BackendPlantBuddy.dto.PlantelPlantDTO;
import com.example.BackendPlantBuddy.entity.PlantelPlant;
import com.example.BackendPlantBuddy.entity.Producto;
import com.example.BackendPlantBuddy.entity.Usuario;
import com.example.BackendPlantBuddy.mapper.EntityMapper;
import com.example.BackendPlantBuddy.repository.PlantelPlantRepository;
import com.example.BackendPlantBuddy.repository.ProductoRepository;
import com.example.BackendPlantBuddy.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlantelService {
    
    private final PlantelPlantRepository plantelRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntityMapper mapper;
    
    public List<PlantelPlantDTO> obtenerPlantelPorUsuario(Integer userId) {
        return plantelRepository.findByUsuario_Id(userId)
                .stream()
                .map(mapper::toPlantelPlantDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PlantelPlantDTO agregarPlantaAlPlantel(Integer userId, Integer productoId, String customTitle, String notes) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Verificar si ya existe
        if (plantelRepository.existsByUsuario_IdAndProducto_Id(userId, productoId)) {
            throw new RuntimeException("La planta ya está en tu plantel");
        }
        
        PlantelPlant plantelPlant = new PlantelPlant();
        plantelPlant.setUsuario(usuario);
        plantelPlant.setProducto(producto);
        plantelPlant.setPlantName(producto.getNombre());
        plantelPlant.setPlantDescription(producto.getDescripcion());
        // Convertir nombre del producto a formato drawable
        plantelPlant.setPlantImageUrl(convertToDrawableName(producto.getNombre()));
        plantelPlant.setAddedAt(LocalDateTime.now());
        plantelPlant.setWateringFrequencyDays(7);
        plantelPlant.setNotificationsEnabled(true);
        plantelPlant.setCustomTitle(customTitle);
        plantelPlant.setNotes(notes);
        
        PlantelPlant guardado = plantelRepository.save(plantelPlant);
        return mapper.toPlantelPlantDTO(guardado);
    }
    
    @Transactional
    public PlantelPlantDTO regarPlanta(Integer userId, Integer productoId) {
        PlantelPlant plantelPlant = plantelRepository.findByUsuario_IdAndProducto_Id(userId, productoId)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada en tu plantel"));
        
        plantelPlant.setLastWateredDate(LocalDateTime.now());
        
        PlantelPlant actualizado = plantelRepository.save(plantelPlant);
        return mapper.toPlantelPlantDTO(actualizado);
    }
    
    @Transactional
    public PlantelPlantDTO actualizarTituloPersonalizado(Integer userId, Integer productoId, String customTitle) {
        PlantelPlant plantelPlant = plantelRepository.findByUsuario_IdAndProducto_Id(userId, productoId)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada en tu plantel"));
        
        plantelPlant.setCustomTitle(customTitle);
        
        PlantelPlant actualizado = plantelRepository.save(plantelPlant);
        return mapper.toPlantelPlantDTO(actualizado);
    }
    
    @Transactional
    public PlantelPlantDTO actualizarNotas(Integer userId, Integer productoId, String notes) {
        PlantelPlant plantelPlant = plantelRepository.findByUsuario_IdAndProducto_Id(userId, productoId)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada en tu plantel"));
        
        plantelPlant.setNotes(notes);
        
        PlantelPlant actualizado = plantelRepository.save(plantelPlant);
        return mapper.toPlantelPlantDTO(actualizado);
    }
    
    @Transactional
    public PlantelPlantDTO alternarNotificaciones(Integer userId, Integer productoId) {
        PlantelPlant plantelPlant = plantelRepository.findByUsuario_IdAndProducto_Id(userId, productoId)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada en tu plantel"));
        
        plantelPlant.setNotificationsEnabled(!plantelPlant.getNotificationsEnabled());
        
        PlantelPlant actualizado = plantelRepository.save(plantelPlant);
        return mapper.toPlantelPlantDTO(actualizado);
    }
    
    @Transactional
    public void eliminarPlantaDelPlantel(Integer userId, Integer productoId) {
        if (!plantelRepository.existsByUsuario_IdAndProducto_Id(userId, productoId)) {
            throw new RuntimeException("Planta no encontrada en tu plantel");
        }
        
        plantelRepository.deleteByUsuario_IdAndProducto_Id(userId, productoId);
    }
    
    /**
     * Convierte el nombre del producto a formato de nombre de drawable resource
     * Ejemplo: "Laurel de Flor Enano" -> "laurel_flor_enano"
     */
    private String convertToDrawableName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return "";
        }
        
        return productName
            .toLowerCase()
            // Eliminar acentos
            .replaceAll("á", "a")
            .replaceAll("é", "e")
            .replaceAll("í", "i")
            .replaceAll("ó", "o")
            .replaceAll("ú", "u")
            .replaceAll("ñ", "n")
            // Eliminar palabras comunes
            .replaceAll("\\b(de|del|la|el|los|las)\\b", "")
            // Convertir espacios y caracteres especiales a guiones bajos
            .replaceAll("[^a-z0-9]", "_")
            // Eliminar guiones bajos múltiples
            .replaceAll("_+", "_")
            // Eliminar guiones bajos al inicio y final
            .replaceAll("^_|_$", "");
    }
}
