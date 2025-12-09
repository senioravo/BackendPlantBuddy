package com.example.BackendPlantBuddy.mapper;

import com.example.BackendPlantBuddy.dto.*;
import com.example.BackendPlantBuddy.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    
    public ProductoDTO toProductoDTO(Producto producto) {
        if (producto == null) return null;
        
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoria(producto.getCategoria() != null ? toCategoriaDTO(producto.getCategoria()) : null);
        dto.setDisponible(producto.getDisponible());
        // Convertir el nombre del producto a formato de drawable resource
        dto.setImagenUrl(convertToDrawableName(producto.getNombre()));
        dto.setRating(producto.getRating());
        
        if (producto.getPlantaDetalle() != null) {
            dto.setPlantaDetalle(toPlantaDetalleDTO(producto.getPlantaDetalle()));
        }
        
        return dto;
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
    
    public PlantaDetalleDTO toPlantaDetalleDTO(PlantaDetalle detalle) {
        if (detalle == null) return null;
        
        PlantaDetalleDTO dto = new PlantaDetalleDTO();
        dto.setNombreCientifico(detalle.getNombreCientifico());
        dto.setTipo(detalle.getTipo());
        dto.setLuzRequerida(detalle.getLuzRequerida());
        dto.setRiegoFrecuencia(detalle.getRiegoFrecuencia());
        dto.setTemperaturaMin(detalle.getTemperaturaMin());
        dto.setTemperaturaMax(detalle.getTemperaturaMax());
        dto.setToxicidad(detalle.getToxicidad());
        dto.setAlturaPromedioCm(detalle.getAlturaPromedioCm());
        dto.setCuidados(detalle.getCuidados());
        
        return dto;
    }
    
    public UsuarioDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;
        
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setProfileImageUrl(usuario.getProfileImageUrl());
        
        return dto;
    }
    
    public PlantelPlantDTO toPlantelPlantDTO(PlantelPlant plantelPlant) {
        if (plantelPlant == null) return null;
        
        PlantelPlantDTO dto = new PlantelPlantDTO();
        dto.setId(plantelPlant.getId());
        dto.setUserId(plantelPlant.getUsuario().getId());
        dto.setProductId(plantelPlant.getProducto().getId());
        dto.setPlantName(plantelPlant.getPlantName());
        dto.setPlantDescription(plantelPlant.getPlantDescription());
        dto.setPlantImageUrl(plantelPlant.getPlantImageUrl());
        dto.setAddedAt(plantelPlant.getAddedAt());
        dto.setLastWateredDate(plantelPlant.getLastWateredDate());
        dto.setWateringFrequencyDays(plantelPlant.getWateringFrequencyDays());
        dto.setNotes(plantelPlant.getNotes());
        dto.setNotificationsEnabled(plantelPlant.getNotificationsEnabled());
        dto.setCustomTitle(plantelPlant.getCustomTitle());
        
        return dto;
    }
    
    public CompraDTO toCompraDTO(Compra compra) {
        if (compra == null) return null;
        
        CompraDTO dto = new CompraDTO();
        dto.setId(compra.getId());
        dto.setUserId(compra.getUsuario().getId());
        dto.setTotal(compra.getTotal());
        dto.setShippingAddress(compra.getShippingAddress());
        dto.setPaymentMethod(compra.getPaymentMethod());
        dto.setStatus(compra.getStatus());
        dto.setCreatedAt(compra.getCreatedAt());
        
        if (compra.getDetalles() != null) {
            dto.setDetalles(
                compra.getDetalles().stream()
                    .map(this::toDetalleCompraDTO)
                    .toList()
            );
        }
        
        return dto;
    }
    
    public DetalleCompraDTO toDetalleCompraDTO(DetalleCompra detalle) {
        if (detalle == null) return null;
        
        DetalleCompraDTO dto = new DetalleCompraDTO();
        dto.setProductoId(detalle.getProducto().getId());
        dto.setProductoNombre(detalle.getProducto().getNombre());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        
        return dto;
    }
    
    public CategoriaDTO toCategoriaDTO(Categoria categoria) {
        if (categoria == null) return null;
        
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        
        return dto;
    }
}
