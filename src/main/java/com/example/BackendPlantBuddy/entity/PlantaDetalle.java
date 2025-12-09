package com.example.BackendPlantBuddy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plantas_detalle", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "producto")
@EqualsAndHashCode(exclude = "producto")
public class PlantaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne
    @JoinColumn(name = "producto_id", unique = true)
    private Producto producto;
    
    @Column(name = "nombre_cientifico")
    private String nombreCientifico;
    
    @Column(length = 100)
    private String tipo;
    
    @Column(name = "luz_requerida", length = 50)
    private String luzRequerida;
    
    @Column(name = "riego_frecuencia", length = 50)
    private String riegoFrecuencia;
    
    @Column(name = "temperatura_min", precision = 5, scale = 2)
    private BigDecimal temperaturaMin;
    
    @Column(name = "temperatura_max", precision = 5, scale = 2)
    private BigDecimal temperaturaMax;
    
    @Column(nullable = false)
    private Boolean toxicidad = false;
    
    @Column(name = "altura_promedio_cm")
    private Integer alturaPromedioCm;
    
    @Column(columnDefinition = "TEXT")
    private String cuidados;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
