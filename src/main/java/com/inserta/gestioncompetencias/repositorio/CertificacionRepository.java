package com.inserta.gestioncompetencias.repositorio;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inserta.gestioncompetencias.modelo.Certificacion;

public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {
    List<Certificacion> findByProfesionalIdOrderByFechaCaducidad(Long profesionalId);
    List<Certificacion> findByFechaCaducidadBetweenOrderByFechaCaducidad(LocalDate desde, LocalDate hasta);
    List<Certificacion> findByFechaCaducidadBeforeOrderByFechaCaducidad(LocalDate fecha);
    void deleteByProfesionalId(Long profesionalId);
}
