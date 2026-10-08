package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inserta.gestioncompetencias.modelo.Sede;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByCiudadContainingIgnoreCase(String ciudad);
}
