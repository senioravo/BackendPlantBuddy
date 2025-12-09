package com.example.BackendPlantBuddy.dto;

import com.example.BackendPlantBuddy.entity.EstadoCompra;
import com.example.BackendPlantBuddy.entity.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    private Integer id;
    private Integer userId;
    private BigDecimal total;
    private String shippingAddress;
    private MetodoPago paymentMethod;
    private EstadoCompra status;
    private LocalDateTime createdAt;
    private List<DetalleCompraDTO> detalles;
}
