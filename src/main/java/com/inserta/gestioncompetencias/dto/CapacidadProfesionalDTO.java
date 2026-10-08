package com.inserta.gestioncompetencias.dto;

public record CapacidadProfesionalDTO(
    Long id,
    String nombre,
    String sede,
    String perfil,
    int ocupacion,
    String estado
) {}
