package com.inserta.gestioncompetencias.controlador;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.inserta.gestioncompetencias.modelo.Servicio;
import com.inserta.gestioncompetencias.repositorio.AsignacionRepository;
import com.inserta.gestioncompetencias.repositorio.ServicioRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    private final ServicioRepository repo;
    private final AsignacionRepository asignacionRepo;

    public ServicioController(ServicioRepository repo, AsignacionRepository asignacionRepo) {
        this.repo = repo;
        this.asignacionRepo = asignacionRepo;
    }

    @GetMapping("/consultar")
    @Operation(summary = "Listar servicios")
    public List<Servicio> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/{id}")
    @Operation(summary = "Consultar un servicio por id")
    public Servicio consultarUno(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear un servicio")
    public Servicio crear(@Valid @RequestBody Servicio s) {
        validarFechas(s);
        s.setId(null);
        if (s.getActivo() == null) s.setActivo(true);
        return repo.save(s);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar un servicio")
    public Servicio actualizar(@PathVariable Long id, @Valid @RequestBody Servicio datos) {
        validarFechas(datos);
        Servicio s = consultarUno(id);
        s.setNombre(datos.getNombre());
        s.setCliente(datos.getCliente());
        s.setFechaInicio(datos.getFechaInicio());
        s.setFechaFin(datos.getFechaFin());
        s.setActivo(datos.getActivo() == null ? true : datos.getActivo());
        return repo.save(s);
    }

    @Transactional
    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar un servicio y sus asignaciones")
    public void borrar(@PathVariable Long id) {
        consultarUno(id);
        asignacionRepo.deleteByServicioId(id);
        repo.deleteById(id);
    }

    private void validarFechas(Servicio s) {
        if (s.getFechaInicio() != null && s.getFechaFin() != null && s.getFechaFin().isBefore(s.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha fin no puede ser anterior a la fecha de inicio");
        }
    }
}
