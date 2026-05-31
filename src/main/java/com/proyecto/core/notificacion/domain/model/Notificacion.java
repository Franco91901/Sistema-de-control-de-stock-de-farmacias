package com.proyecto.core.notificacion.domain.model;

import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.sede.domain.model.Sede;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion", columnDefinition = "INT")
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    @NotNull(message = "El medicamento es obligatorio")
    private Medicamento medicamento;

    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    @NotNull(message = "La sede es obligatoria")
    private Sede sede;

    @NotBlank(message = "El mensaje es obligatorio")
    @Column(name = "mensaje", nullable = false, length = 255)
    private String mensaje;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50)
    private TipoNotificacion tipo = TipoNotificacion.BAJO_STOCK;

    public Notificacion(Medicamento medicamento, Sede sede, String mensaje, TipoNotificacion tipo) {
        this.medicamento = medicamento;
        this.sede = sede;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoNotificacion.PENDIENTE;
    }
}
