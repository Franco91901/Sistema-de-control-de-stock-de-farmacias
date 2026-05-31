package com.proyecto.core.orden.domain.model;

import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.core.sede.domain.model.Sede;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orden")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Long idOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoOrden tipo = TipoOrden.COMPRA;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede_destino")
    private Sede sedeDestino;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
