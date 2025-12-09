package com.example.BackendPlantBuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private CategoriaDTO categoria;
    private Boolean disponible;
    private String imagenUrl;
    private BigDecimal rating;
    private PlantaDetalleDTO plantaDetalle;
}
