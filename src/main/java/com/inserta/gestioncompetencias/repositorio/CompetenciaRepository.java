package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inserta.gestioncompetencias.modelo.Competencia;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {
    List<Competencia> findByNombreContainingIgnoreCase(String nombre);
    List<Competencia> findByAreaContainingIgnoreCase(String area);
}
