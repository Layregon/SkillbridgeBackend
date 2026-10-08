package com.inserta.gestioncompetencias.controlador;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacion(MethodArgumentNotValidException e) {
        Map<String, String> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
        String mensaje = errores.values().stream().findFirst().orElse("Revisa los datos introducidos");
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("error", "Datos incorrectos");
        respuesta.put("mensaje", mensaje);
        respuesta.put("campos", errores);
        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> integridad(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Conflicto de datos", "mensaje", "Registro duplicado o relacionado con otros datos"));
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> estado(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", "Operacion no completada", "mensaje", e.getReason() == null ? "Error" : e.getReason()));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> argumento(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Datos incorrectos", "mensaje", e.getMessage() == null ? "Error" : e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtime(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Operacion no completada", "mensaje", e.getMessage() == null ? "Error" : e.getMessage()));
    }
}
