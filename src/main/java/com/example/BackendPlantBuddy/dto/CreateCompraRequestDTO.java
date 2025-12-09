package com.example.BackendPlantBuddy.dto;

import com.example.BackendPlantBuddy.entity.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompraRequestDTO {
    
    @NotNull(message = "El userId es requerido")
    private Integer userId;
    
    @NotBlank(message = "La dirección de envío es requerida")
    private String shippingAddress;
    
    @NotNull(message = "El método de pago es requerido")
    private MetodoPago paymentMethod;
    
    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<CartItemRequestDTO> items;
}
