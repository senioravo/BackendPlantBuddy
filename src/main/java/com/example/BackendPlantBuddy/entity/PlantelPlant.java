package com.example.BackendPlantBuddy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plantel_plants", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantelPlant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Producto producto;
    
    @Column(name = "plant_name", nullable = false)
    private String plantName;
    
    @Column(name = "plant_description")
    private String plantDescription;
    
    @Column(name = "plant_image_url")
    private String plantImageUrl;
    
    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    @Column(name = "last_watered_date")
    private LocalDateTime lastWateredDate;
    
    @Column(name = "watering_frequency_days", nullable = false)
    private Integer wateringFrequencyDays = 7;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;
    
    @Column(name = "custom_title")
    private String customTitle;
}
