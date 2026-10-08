package com.inserta.gestioncompetencias.controlador;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.inserta.gestioncompetencias.modelo.Perfil;
import com.inserta.gestioncompetencias.modelo.Profesional;
import com.inserta.gestioncompetencias.modelo.Sede;
import com.inserta.gestioncompetencias.repositorio.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {
    private final ProfesionalRepository profesionalRepo;
    private final PerfilRepository perfilRepo;
    private final SedeRepository sedeRepo;
    private final ProfesionalCompetenciaRepository pcRepo;
    private final CertificacionRepository certificacionRepo;
    private final AsignacionRepository asignacionRepo;

    public ProfesionalController(ProfesionalRepository profesionalRepo, PerfilRepository perfilRepo,
                                 SedeRepository sedeRepo, ProfesionalCompetenciaRepository pcRepo,
                                 CertificacionRepository certificacionRepo, AsignacionRepository asignacionRepo) {
        this.profesionalRepo = profesionalRepo;
        this.perfilRepo = perfilRepo;
        this.sedeRepo = sedeRepo;
        this.pcRepo = pcRepo;
        this.certificacionRepo = certificacionRepo;
        this.asignacionRepo = asignacionRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Listar profesionales")
    public List<Profesional> consultar() { return profesionalRepo.findAll(); }

    @GetMapping("/consultar/{id}")
    @Operation(summary = "Consultar un profesional por id")
    public Profesional consultarUno(@PathVariable Long id) {
        return profesionalRepo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
    }

    @GetMapping("/filtrar")
    @Operation(summary = "Filtrar profesionales por nombre, sede y perfil")
    public List<Profesional> filtrar(@RequestParam(required = false) String nombre,
                                     @RequestParam(required = false) Long sedeId,
                                     @RequestParam(required = false) Long perfilId) {
        String filtroNombre = (nombre == null || nombre.isBlank()) ? null : nombre;
        return profesionalRepo.filtrar(filtroNombre, sedeId, perfilId);
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear un profesional")
    public Profesional crear(@Valid @RequestBody Profesional datos) {
        datos.setId(null);
        if (datos.getActivo() == null) datos.setActivo(true);
        datos.setPerfil(buscarPerfil(datos));
        datos.setSede(buscarSede(datos));
        return profesionalRepo.save(datos);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar un profesional")
    public Profesional actualizar(@PathVariable Long id, @Valid @RequestBody Profesional datos) {
        Profesional p = consultarUno(id);
        p.setNombre(datos.getNombre());
        p.setEmail(datos.getEmail());
        p.setAntiguedad(datos.getAntiguedad());
        p.setActivo(datos.getActivo() == null ? true : datos.getActivo());
        p.setPerfil(buscarPerfil(datos));
        p.setSede(buscarSede(datos));
        return profesionalRepo.save(p);
    }

    @Transactional
    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar un profesional y sus relaciones")
    public void borrar(@PathVariable Long id) {
        consultarUno(id);
        pcRepo.deleteByProfesionalId(id);
        certificacionRepo.deleteByProfesionalId(id);
        asignacionRepo.deleteByProfesionalId(id);
        profesionalRepo.deleteById(id);
    }

    private Perfil buscarPerfil(Profesional datos) {
        if (datos.getPerfil() == null || datos.getPerfil().getId() == null) throw new IllegalArgumentException("Debe indicar perfil");
        return perfilRepo.findById(datos.getPerfil().getId()).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
    }

    private Sede buscarSede(Profesional datos) {
        if (datos.getSede() == null || datos.getSede().getId() == null) throw new IllegalArgumentException("Debe indicar sede");
        return sedeRepo.findById(datos.getSede().getId()).orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }
}
