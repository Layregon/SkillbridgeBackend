package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.inserta.gestioncompetencias.modelo.Asignacion;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByProfesionalIdOrderByFechaInicioDesc(Long profesionalId);
    List<Asignacion> findByServicioId(Long servicioId);
    List<Asignacion> findByActivaTrue();
    boolean existsByProfesionalIdAndServicioIdAndActivaTrue(Long profesionalId, Long servicioId);
    void deleteByProfesionalId(Long profesionalId);
    void deleteByServicioId(Long servicioId);

    @Query("SELECT COALESCE(SUM(a.porcentajeDedicacion), 0) FROM Asignacion a WHERE a.profesional.id = :profesionalId AND a.activa = true")
    Long sumaDedicacionActiva(@Param("profesionalId") Long profesionalId);
}
