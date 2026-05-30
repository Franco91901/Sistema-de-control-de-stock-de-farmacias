package com.proyecto.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteRequestDTO {
    
    @NotNull(message = "El medicamento es obligatorio")
    private Long idMedicamento;
    
    @NotBlank(message = "El código de lote es obligatorio")
    private String codigoLote;
    
    @NotNull(message = "La fecha de caducidad es obligatoria")
    @FutureOrPresent(message = "La fecha de caducidad debe ser hoy o futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCaducidad;
    
    @NotNull(message = "El stock del lote es obligatorio")
    @Positive(message = "El stock debe ser mayor a cero")
    private Integer stockLote;
}