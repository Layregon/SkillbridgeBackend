package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inserta.gestioncompetencias.modelo.ProfesionalCompetencia;

public interface ProfesionalCompetenciaRepository extends JpaRepository<ProfesionalCompetencia, Long> {
    List<ProfesionalCompetencia> findByProfesionalIdOrderByCompetenciaNombre(Long profesionalId);
    List<ProfesionalCompetencia> findByCompetenciaId(Long competenciaId);
    boolean existsByProfesionalIdAndCompetenciaId(Long profesionalId, Long competenciaId);
    long countByCompetenciaIdAndVerificadaTrue(Long competenciaId);
    void deleteByProfesionalId(Long profesionalId);
    void deleteByCompetenciaId(Long competenciaId);
}
