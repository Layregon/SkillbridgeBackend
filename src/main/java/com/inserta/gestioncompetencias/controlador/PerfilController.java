package com.inserta.gestioncompetencias.controlador;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.inserta.gestioncompetencias.modelo.Perfil;
import com.inserta.gestioncompetencias.repositorio.PerfilRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilController {
    private final PerfilRepository repo;

    public PerfilController(PerfilRepository repo) { this.repo = repo; }

    @GetMapping("/consultar")
    @Operation(summary = "Listar perfiles")
    public List<Perfil> consultar() { return repo.findAll(); }

    @GetMapping("/consultar/{id}")
    @Operation(summary = "Consultar un perfil por id")
    public Perfil consultarUno(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear un perfil")
    public Perfil crear(@Valid @RequestBody Perfil perfil) {
        perfil.setId(null);
        return repo.save(perfil);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar un perfil")
    public Perfil actualizar(@PathVariable Long id, @Valid @RequestBody Perfil datos) {
        Perfil p = consultarUno(id);
        p.setNombre(datos.getNombre());
        return repo.save(p);
    }

    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Borrar un perfil")
    public void borrar(@PathVariable Long id) {
        consultarUno(id);
        repo.deleteById(id);
    }
}
