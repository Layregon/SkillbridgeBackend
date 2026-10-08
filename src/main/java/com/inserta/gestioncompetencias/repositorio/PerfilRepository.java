package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inserta.gestioncompetencias.modelo.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    List<Perfil> findByNombreContainingIgnoreCase(String nombre);
}
