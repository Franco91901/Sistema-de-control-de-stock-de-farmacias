package com.proyecto.core.sede.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sede")
@Getter @Setter
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

}
