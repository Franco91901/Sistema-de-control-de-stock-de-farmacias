package com.proyecto.core.medicamento.domain.model;

import com.proyecto.core.sede.domain.model.Sede;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "medicamento_sede",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_medicamento", "id_sede"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MedicamentoSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Column(name = "stock_total", nullable = false)
    private Integer stockTotal = 0;

    public MedicamentoSede(Medicamento medicamento, Sede sede) {
        this.medicamento = medicamento;
        this.sede = sede;
        this.stockTotal = 0;
    }
}
