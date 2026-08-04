package com.gasmanager.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaFaltanteDTO {

    private Long empleadoId;
    private String tipo;
    private LocalDate fecha;
    private BigDecimal cantidad;
    private BigDecimal monto;
    private String observaciones;
    private String autorizadoPor;
}