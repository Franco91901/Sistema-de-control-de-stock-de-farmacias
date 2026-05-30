package com.proyecto.repository;

import com.proyecto.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    // Buscar notificaciones por sede
    List<Notificacion> findBySedeIdSedeOrderByFechaDesc(Long idSede);
    
    // Buscar notificaciones por sede y estado
    List<Notificacion> findBySedeIdSedeAndEstado(Long idSede, String estado);
    
    // Buscar notificaciones por tipo
    List<Notificacion> findBySedeIdSedeAndTipo(Long idSede, String tipo);
    
    // Buscar notificaciones pendientes por sede
    List<Notificacion> findBySedeIdSedeAndEstadoOrderByFechaDesc(Long idSede, String estado);
    
    // Buscar notificaciones recientes (últimos 7 días)
    @Query("SELECT n FROM Notificacion n WHERE n.sede.idSede = :idSede AND n.fecha >= :fechaInicio ORDER BY n.fecha DESC")
    List<Notificacion> findNotificacionesRecientes(
        @Param("idSede") Long idSede,
        @Param("fechaInicio") LocalDateTime fechaInicio
    );
    
    // Contar notificaciones pendientes por sede
    Long countBySedeIdSedeAndEstado(Long idSede, String estado);
    
    // Buscar notificaciones por medicamento
    List<Notificacion> findByMedicamentoIdMedicamento(Long idMedicamento);
}