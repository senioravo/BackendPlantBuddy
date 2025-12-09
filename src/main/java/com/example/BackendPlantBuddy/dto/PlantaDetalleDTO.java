package com.example.BackendPlantBuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantaDetalleDTO {
    private String nombreCientifico;
    private String tipo;
    private String luzRequerida;
    private String riegoFrecuencia;
    private BigDecimal temperaturaMin;
    private BigDecimal temperaturaMax;
    private Boolean toxicidad;
    private Integer alturaPromedioCm;
    private String cuidados;
}
