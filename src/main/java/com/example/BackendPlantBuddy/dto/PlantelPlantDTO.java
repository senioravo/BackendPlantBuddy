package com.example.BackendPlantBuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantelPlantDTO {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String plantName;
    private String plantDescription;
    private String plantImageUrl;
    private LocalDateTime addedAt;
    private LocalDateTime lastWateredDate;
    private Integer wateringFrequencyDays;
    private String notes;
    private Boolean notificationsEnabled;
    private String customTitle;
}
