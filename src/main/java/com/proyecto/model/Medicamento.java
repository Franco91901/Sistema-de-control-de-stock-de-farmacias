package com.proyecto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicamento", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_sede", "nombre"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medicamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento", columnDefinition = "INT")
    private Long idMedicamento;
    
    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    @NotNull(message = "La sede es obligatoria")
    private Sede sede;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
    
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    
    @Column(name = "stock_total", nullable = false)
    private Integer stockTotal = 0; 
    
    @Transient
    private String nombreSede;
    
    // Constructor sin ID para creación
    public Medicamento(Sede sede, String nombre, String descripcion) {
        this.sede = sede;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockTotal = 0;
    }
    
    
}