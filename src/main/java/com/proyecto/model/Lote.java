package com.proyecto.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lote")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote", columnDefinition = "INT")
    private Long idLote;
    
    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    @NotNull(message = "El medicamento es obligatorio")
    private Medicamento medicamento;
    
    @NotBlank(message = "El código de lote es obligatorio")
    @Column(name = "codigo_lote", nullable = false, length = 50)
    private String codigoLote;
    
    @NotNull(message = "La fecha de caducidad es obligatoria")
    @Column(name = "fecha_caducidad", nullable = false)
    @FutureOrPresent(message = "La fecha de caducidad debe ser hoy o futura")
    private LocalDate fechaCaducidad;
    
    @NotNull(message = "El stock del lote es obligatorio")
    @Positive(message = "El stock debe ser mayor a cero")
    @Column(name = "stock_lote", nullable = false)
    private Integer stockLote;
    
    // Constructor sin ID
    public Lote(Medicamento medicamento, String codigoLote, LocalDate fechaCaducidad, Integer stockLote) {
        this.medicamento = medicamento;
        this.codigoLote = codigoLote;
        this.fechaCaducidad = fechaCaducidad;
        this.stockLote = stockLote;
    }
}