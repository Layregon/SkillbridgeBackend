package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.inserta.gestioncompetencias.modelo.Profesional;

public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    Optional<Profesional> findByEmail(String email);

    @Query("""
        SELECT p FROM Profesional p
        WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          AND (:sedeId IS NULL OR p.sede.id = :sedeId)
          AND (:perfilId IS NULL OR p.perfil.id = :perfilId)
        ORDER BY p.nombre
        """)
    List<Profesional> filtrar(@Param("nombre") String nombre,
                              @Param("sedeId") Long sedeId,
                              @Param("perfilId") Long perfilId);
}
