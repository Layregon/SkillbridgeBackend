package com.inserta.gestioncompetencias.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inserta.gestioncompetencias.modelo.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);
    List<Servicio> findByActivo(Boolean activo);
}
