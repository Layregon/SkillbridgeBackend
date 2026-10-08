package com.inserta.gestioncompetencias.controlador;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.inserta.gestioncompetencias.dto.CapacidadProfesionalDTO;
import com.inserta.gestioncompetencias.dto.CapacidadResumenDTO;
import com.inserta.gestioncompetencias.modelo.Profesional;
import com.inserta.gestioncompetencias.repositorio.AsignacionRepository;
import com.inserta.gestioncompetencias.repositorio.ProfesionalRepository;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/capacidad")
public class CapacidadController {
    private final ProfesionalRepository profesionalRepo;
    private final AsignacionRepository asignacionRepo;

    public CapacidadController(ProfesionalRepository profesionalRepo, AsignacionRepository asignacionRepo) {
        this.profesionalRepo = profesionalRepo;
        this.asignacionRepo = asignacionRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Calcular ocupacion y estado de todos los profesionales")
    public List<CapacidadProfesionalDTO> consultar() {
        List<CapacidadProfesionalDTO> salida = new ArrayList<>();
        for (Profesional p : profesionalRepo.findAll()) salida.add(convertir(p));
        return salida;
    }

    @GetMapping("/resumen")
    @Operation(summary = "Obtener resumen general de capacidad")
    public CapacidadResumenDTO resumen() {
        List<CapacidadProfesionalDTO> lista = consultar();
        long sinAsignar = lista.stream().filter(c -> c.ocupacion() == 0).count();
        long parciales = lista.stream().filter(c -> c.ocupacion() > 0 && c.ocupacion() < 100).count();
        long completos = lista.stream().filter(c -> c.ocupacion() == 100).count();
        long sobrecargados = lista.stream().filter(c -> c.ocupacion() > 100).count();
        double media = lista.stream().mapToInt(CapacidadProfesionalDTO::ocupacion).average().orElse(0.0);
        return new CapacidadResumenDTO(lista.size(), Math.round(media * 10.0) / 10.0,
                sinAsignar, parciales, completos, sobrecargados);
    }

    private CapacidadProfesionalDTO convertir(Profesional p) {
        int ocupacion = asignacionRepo.sumaDedicacionActiva(p.getId()).intValue();
        String estado;
        if (ocupacion == 0) estado = "SIN_ASIGNAR";
        else if (ocupacion < 100) estado = "DISPONIBLE";
        else if (ocupacion == 100) estado = "COMPLETO";
        else estado = "SOBRECARGADO";

        return new CapacidadProfesionalDTO(p.getId(), p.getNombre(), p.getSede().getCiudad(),
                p.getPerfil().getNombre(), ocupacion, estado);
    }
}
