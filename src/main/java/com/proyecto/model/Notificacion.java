package com.proyecto.model;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";
    
    
    private String tipo = "BAJO_STOCK";
    
    // Constructor simplificado
    public Notificacion(Medicamento medicamento, Sede sede, String mensaje, String tipo) {
        this.medicamento = medicamento;
        this.sede = sede;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }
    
    
}