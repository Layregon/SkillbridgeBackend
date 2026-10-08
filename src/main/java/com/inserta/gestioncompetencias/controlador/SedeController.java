package com.inserta.gestioncompetencias.controlador;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.inserta.gestioncompetencias.modelo.Sede;
import com.inserta.gestioncompetencias.repositorio.SedeRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {
    private final SedeRepository repo;

    public SedeController(SedeRepository repo) { this.repo = repo; }

    @GetMapping("/consultar")
    @Operation(summary = "Listar sedes")
    public List<Sede> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/{id}")
    @Operation(summary = "Consultar una sede por id")
    public Sede consultarUno(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear una sede")
    public Sede crear(@Valid @RequestBody Sede sede) {
        sede.setId(null);
        return repo.save(sede);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar una sede")
    public Sede actualizar(@PathVariable Long id, @Valid @RequestBody Sede datos) {
        Sede s = consultarUno(id);
        s.setCiudad(datos.getCiudad());
        s.setPais(datos.getPais());
        return repo.save(s);
    }

    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar una sede")
    public void borrar(@PathVariable Long id) {
        consultarUno(id);
        repo.deleteById(id);
    }
}
