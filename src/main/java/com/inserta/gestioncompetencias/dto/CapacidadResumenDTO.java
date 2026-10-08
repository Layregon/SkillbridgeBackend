package com.inserta.gestioncompetencias.dto;

public record CapacidadResumenDTO(
    long totalProfesionales,
    double ocupacionMedia,
    long sinAsignar,
    long disponibilidadParcial,
    long ocupacionCompleta,
    long sobrecargados
) {}
