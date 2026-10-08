package com.inserta.gestioncompetencias.controlador;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.inserta.gestioncompetencias.modelo.Asignacion;
import com.inserta.gestioncompetencias.repositorio.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {
    private final AsignacionRepository repo;
    private final ProfesionalRepository profesionalRepo;
    private final ServicioRepository servicioRepo;

    public AsignacionController(AsignacionRepository repo, ProfesionalRepository profesionalRepo, ServicioRepository servicioRepo) {
        this.repo = repo;
        this.profesionalRepo = profesionalRepo;
        this.servicioRepo = servicioRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Listar asignaciones")
    public List<Asignacion> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/profesional/{id}")
    @Operation(summary = "Listar asignaciones de un profesional")
    public List<Asignacion> porProfesional(@PathVariable Long id) {
        return repo.findByProfesionalIdOrderByFechaInicioDesc(id);
    }

    @GetMapping("/consultar/servicio/{id}")
    @Operation(summary = "Listar asignaciones de un servicio")
    public List<Asignacion> porServicio(@PathVariable Long id) {
        return repo.findByServicioId(id);
    }

    @PostMapping("/crear")
    @Operation(summary = "Asignar un profesional a un servicio")
    public Asignacion crear(@Valid @RequestBody Asignacion datos) {
        Long profesionalId = datos.getProfesional() == null ? null : datos.getProfesional().getId();
        Long servicioId = datos.getServicio() == null ? null : datos.getServicio().getId();
        if (profesionalId == null || servicioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profesional y servicio son obligatorios");
        }
        if (datos.getActiva() == null) datos.setActiva(true);
        validarFechas(datos);
        if (Boolean.TRUE.equals(datos.getActiva()) && repo.existsByProfesionalIdAndServicioIdAndActivaTrue(profesionalId, servicioId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una asignacion activa de ese profesional al servicio");
        }
        datos.setId(null);
        datos.setProfesional(profesionalRepo.findById(profesionalId).orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
        datos.setServicio(servicioRepo.findById(servicioId).orElseThrow(() -> new RuntimeException("Servicio no encontrado")));
        return repo.save(datos);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar una asignacion")
    public Asignacion actualizar(@PathVariable Long id, @Valid @RequestBody Asignacion datos) {
        validarFechas(datos);
        Asignacion a = repo.findById(id).orElseThrow(() -> new RuntimeException("Asignacion no encontrada"));
        a.setPorcentajeDedicacion(datos.getPorcentajeDedicacion());
        a.setRol(datos.getRol());
        a.setActiva(datos.getActiva() == null ? true : datos.getActiva());
        a.setFechaInicio(datos.getFechaInicio());
        a.setFechaFin(datos.getFechaFin());
        return repo.save(a);
    }

    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar una asignacion")
    public void borrar(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Asignacion no encontrada");
        repo.deleteById(id);
    }

    private void validarFechas(Asignacion a) {
        if (a.getFechaInicio() != null && a.getFechaFin() != null && a.getFechaFin().isBefore(a.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha fin no puede ser anterior a la fecha de inicio");
        }
    }
}
