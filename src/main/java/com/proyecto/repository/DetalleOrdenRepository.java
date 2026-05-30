package com.proyecto.repository;

import com.proyecto.model.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {    
    List<DetalleOrden> findByOrdenIdOrden(Long idOrden);
    
    @Query("""
    	    SELECT d FROM DetalleOrden d
    	    WHERE (:estado IS NULL OR d.estado = :estado)
    	      AND (:sede IS NULL OR d.medicamento.sede.nombre = :sede)
    	""")
    	List<DetalleOrden> listarPorEstadoYSede(String estado, String sede);
}