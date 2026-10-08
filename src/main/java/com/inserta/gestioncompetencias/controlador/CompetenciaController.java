package com.inserta.gestioncompetencias.controlador;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.inserta.gestioncompetencias.dto.CompetenciaRiesgoDTO;
import com.inserta.gestioncompetencias.modelo.Competencia;
import com.inserta.gestioncompetencias.repositorio.CompetenciaRepository;
import com.inserta.gestioncompetencias.repositorio.ProfesionalCompetenciaRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {
    private final CompetenciaRepository repo;
    private final ProfesionalCompetenciaRepository pcRepo;

    public CompetenciaController(CompetenciaRepository repo, ProfesionalCompetenciaRepository pcRepo) {
        this.repo = repo;
        this.pcRepo = pcRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Listar competencias")
    public List<Competencia> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/{id}")
    @Operation(summary = "Consultar una competencia por id")
    public Competencia consultarUno(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Competencia no encontrada"));
    }

    @GetMapping("/consultar/area")
    @Operation(summary = "Buscar competencias por area")
    public List<Competencia> porArea(@RequestParam String area) {
        return repo.findByAreaContainingIgnoreCase(area);
    }

    @GetMapping("/riesgo")
    @Operation(summary = "Listar competencias con cobertura verificada igual o inferior al umbral")
    public List<CompetenciaRiesgoDTO> riesgo(@RequestParam(defaultValue = "1") long umbral) {
        if (umbral < 0) throw new IllegalArgumentException("El umbral no puede ser negativo");
        List<CompetenciaRiesgoDTO> salida = new ArrayList<>();
        for (Competencia c : repo.findAll()) {
            long cobertura = pcRepo.countByCompetenciaIdAndVerificadaTrue(c.getId());
            if (cobertura <= umbral) salida.add(new CompetenciaRiesgoDTO(c.getId(), c.getNombre(), c.getArea(), cobertura));
        }
        return salida;
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear una competencia")
    public Competencia crear(@Valid @RequestBody Competencia c) {
        c.setId(null);
        return repo.save(c);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar una competencia")
    public Competencia actualizar(@PathVariable Long id, @Valid @RequestBody Competencia datos) {
        Competencia c = consultarUno(id);
        c.setNombre(datos.getNombre());
        c.setArea(datos.getArea());
        c.setDescripcion(datos.getDescripcion());
        return repo.save(c);
    }

    @Transactional
    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar una competencia y sus relaciones profesional-competencia")
    public void borrar(@PathVariable Long id) {
        consultarUno(id);
        pcRepo.deleteByCompetenciaId(id);
        repo.deleteById(id);
    }
}
