package com.inserta.gestioncompetencias.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.inserta.gestioncompetencias.config.JwtService;
import com.inserta.gestioncompetencias.dto.LoginRequest;
import com.inserta.gestioncompetencias.dto.LoginResponse;
import com.inserta.gestioncompetencias.modelo.Usuario;
import com.inserta.gestioncompetencias.repositorio.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepo;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UsuarioRepository usuarioRepo) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.usuarioRepo = usuarioRepo;
    }

    @PostMapping("/login")
    @Operation(summary = "Login y obtencion de token JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(datos.username(), datos.password()));
            Usuario usuario = usuarioRepo.findByUsername(datos.username()).orElseThrow();
            String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol());
            return ResponseEntity.ok(new LoginResponse(token, usuario.getUsername(), usuario.getRol()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contrasena incorrectos");
        }
    }
}
