package com.inserta.gestioncompetencias.controlador;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.inserta.gestioncompetencias.modelo.Certificacion;
import com.inserta.gestioncompetencias.repositorio.CertificacionRepository;
import com.inserta.gestioncompetencias.repositorio.ProfesionalRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/certificaciones")
public class CertificacionController {
    private final CertificacionRepository repo;
    private final ProfesionalRepository profesionalRepo;

    public CertificacionController(CertificacionRepository repo, ProfesionalRepository profesionalRepo) {
        this.repo = repo;
        this.profesionalRepo = profesionalRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Listar certificaciones")
    public List<Certificacion> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/profesional/{id}")
    @Operation(summary = "Listar certificaciones de un profesional")
    public List<Certificacion> porProfesional(@PathVariable Long id) {
        return repo.findByProfesionalIdOrderByFechaCaducidad(id);
    }

    @GetMapping("/proximas")
    @Operation(summary = "Listar certificaciones que caducan en los proximos N dias")
    public List<Certificacion> proximas(@RequestParam(defaultValue = "60") int dias) {
        if (dias < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los dias no pueden ser negativos");
        LocalDate hoy = LocalDate.now();
        return repo.findByFechaCaducidadBetweenOrderByFechaCaducidad(hoy, hoy.plusDays(dias));
    }

    @GetMapping("/caducadas")
    @Operation(summary = "Listar certificaciones caducadas")
    public List<Certificacion> caducadas() {
        return repo.findByFechaCaducidadBeforeOrderByFechaCaducidad(LocalDate.now());
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear una certificacion para un profesional")
    public Certificacion crear(@Valid @RequestBody Certificacion datos) {
        Long profesionalId = datos.getProfesional() == null ? null : datos.getProfesional().getId();
        if (profesionalId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar profesional");
        datos.setId(null);
        datos.setProfesional(profesionalRepo.findById(profesionalId).orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
        return repo.save(datos);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar una certificacion")
    public Certificacion actualizar(@PathVariable Long id, @Valid @RequestBody Certificacion datos) {
        Certificacion c = repo.findById(id).orElseThrow(() -> new RuntimeException("Certificacion no encontrada"));
        c.setNombre(datos.getNombre());
        c.setEntidad(datos.getEntidad());
        c.setFechaCaducidad(datos.getFechaCaducidad());
        return repo.save(c);
    }

    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar una certificacion")
    public void borrar(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Certificacion no encontrada");
        repo.deleteById(id);
    }
}
