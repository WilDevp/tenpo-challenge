package com.tenpo.tenpochallenge.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for the calculation request
 */
@Data
public class CalculationRequest {
    
    @NotNull(message = "The first number cannot be null")
    private BigDecimal num1;
    
    @NotNull(message = "The second number cannot be null")
    private BigDecimal num2;
}
