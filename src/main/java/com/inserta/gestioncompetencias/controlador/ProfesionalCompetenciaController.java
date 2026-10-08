package com.inserta.gestioncompetencias.controlador;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.inserta.gestioncompetencias.modelo.*;
import com.inserta.gestioncompetencias.repositorio.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profesional-competencias")
public class ProfesionalCompetenciaController {
    private final ProfesionalCompetenciaRepository repo;
    private final ProfesionalRepository profesionalRepo;
    private final CompetenciaRepository competenciaRepo;

    public ProfesionalCompetenciaController(ProfesionalCompetenciaRepository repo,
                                             ProfesionalRepository profesionalRepo,
                                             CompetenciaRepository competenciaRepo) {
        this.repo = repo;
        this.profesionalRepo = profesionalRepo;
        this.competenciaRepo = competenciaRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Listar relaciones profesional-competencia")
    public List<ProfesionalCompetencia> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/profesional/{id}")
    @Operation(summary = "Listar competencias de un profesional")
    public List<ProfesionalCompetencia> porProfesional(@PathVariable Long id) {
        return repo.findByProfesionalIdOrderByCompetenciaNombre(id);
    }

    @PostMapping("/crear")
    @Operation(summary = "Asignar una competencia a un profesional")
    public ProfesionalCompetencia crear(@Valid @RequestBody ProfesionalCompetencia datos) {
        Long profesionalId = datos.getProfesional() == null ? null : datos.getProfesional().getId();
        Long competenciaId = datos.getCompetencia() == null ? null : datos.getCompetencia().getId();
        if (profesionalId == null || competenciaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profesional y competencia son obligatorios");
        }
        if (repo.existsByProfesionalIdAndCompetenciaId(profesionalId, competenciaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El profesional ya tiene esa competencia");
        }
        datos.setId(null);
        datos.setProfesional(profesionalRepo.findById(profesionalId).orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
        datos.setCompetencia(competenciaRepo.findById(competenciaId).orElseThrow(() -> new RuntimeException("Competencia no encontrada")));
        return repo.save(datos);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar nivel, experiencia y verificacion de una competencia")
    public ProfesionalCompetencia actualizar(@PathVariable Long id, @Valid @RequestBody ProfesionalCompetencia datos) {
        ProfesionalCompetencia pc = repo.findById(id).orElseThrow(() -> new RuntimeException("Relacion no encontrada"));
        pc.setNivel(datos.getNivel());
        pc.setAnosExperiencia(datos.getAnosExperiencia());
        pc.setVerificada(datos.getVerificada());
        return repo.save(pc);
    }

    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Eliminar una competencia asignada a un profesional")
    public void borrar(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Relacion no encontrada");
        repo.deleteById(id);
    }
}
