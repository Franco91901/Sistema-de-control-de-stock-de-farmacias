package com.proyecto.core.lote.domain.model;

import java.time.LocalDate;

import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.sede.domain.model.Sede;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "lote")
@Getter @Setter
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

    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    @NotNull(message = "La sede es obligatoria")
    private Sede sede;

    @NotBlank(message = "El código de lote es obligatorio")
    @Column(name = "codigo_lote", nullable = false, unique = true, length = 50)
    private String codigoLote;

    @NotNull(message = "La fecha de caducidad es obligatoria")
    @Column(name = "fecha_caducidad", nullable = false)
    @FutureOrPresent(message = "La fecha de caducidad debe ser hoy o futura")
    private LocalDate fechaCaducidad;

    @NotNull(message = "El stock del lote es obligatorio")
    @Positive(message = "El stock debe ser mayor a cero")
    @Column(name = "stock_lote", nullable = false)
    private Integer stockLote;

    public Lote(Medicamento medicamento, Sede sede, String codigoLote, LocalDate fechaCaducidad, Integer stockLote) {
        this.medicamento = medicamento;
        this.sede = sede;
        this.codigoLote = codigoLote;
        this.fechaCaducidad = fechaCaducidad;
        this.stockLote = stockLote;
    }
}
