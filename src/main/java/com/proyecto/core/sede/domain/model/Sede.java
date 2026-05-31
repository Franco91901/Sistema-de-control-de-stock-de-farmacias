package com.proyecto.core.sede.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sede")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede", columnDefinition = "INT")
    private Long idSede;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(name = "direccion", nullable = false, length = 150)
    private String direccion;

    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MedicamentoSede> medicamentoSedes = new ArrayList<>();

    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notificacion> notificaciones = new ArrayList<>();

    @OneToMany(mappedBy = "sede", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Usuario> usuarios = new ArrayList<>();

    public Long getIdSede() { return idSede; }
    public void setIdSede(Long idSede) { this.idSede = idSede; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
