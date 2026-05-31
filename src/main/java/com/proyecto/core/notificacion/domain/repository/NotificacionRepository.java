package com.proyecto.core.notificacion.domain.repository;

import com.proyecto.core.notificacion.domain.model.EstadoNotificacion;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import com.proyecto.core.notificacion.domain.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findBySedeIdSedeOrderByFechaDesc(Long idSede);

    List<Notificacion> findBySedeIdSedeAndEstado(Long idSede, EstadoNotificacion estado);

    List<Notificacion> findBySedeIdSedeAndTipo(Long idSede, TipoNotificacion tipo);

    List<Notificacion> findBySedeIdSedeAndEstadoOrderByFechaDesc(Long idSede, EstadoNotificacion estado);

    @Query("SELECT n FROM Notificacion n WHERE n.sede.idSede = :idSede AND n.fecha >= :fechaInicio ORDER BY n.fecha DESC")
    List<Notificacion> findNotificacionesRecientes(
        @Param("idSede") Long idSede,
        @Param("fechaInicio") LocalDateTime fechaInicio
    );

    Long countBySedeIdSedeAndEstado(Long idSede, EstadoNotificacion estado);

    List<Notificacion> findByMedicamentoIdMedicamento(Long idMedicamento);
}
