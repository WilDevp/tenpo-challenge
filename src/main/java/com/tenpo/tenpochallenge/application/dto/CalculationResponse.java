package com.tenpo.tenpochallenge.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    private BigDecimal num1;
    private BigDecimal num2;
    private BigDecimal sum;
    private BigDecimal percentage;
    private BigDecimal result;
}