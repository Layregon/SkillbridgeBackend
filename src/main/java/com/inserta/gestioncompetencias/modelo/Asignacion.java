package com.inserta.gestioncompetencias.modelo;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidad intermedia entre Profesional y Servicio.
 * Guarda el porcentaje de dedicacion, el rol y las fechas de la asignacion.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asignaciones")
public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesional_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profesional profesional;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Servicio servicio;

    @NotNull(message = "La dedicación es obligatoria")
    @Min(value = 1, message = "La dedicación mínima es 1%")
    @Max(value = 100, message = "La dedicación máxima es 100%")
    @Column(name = "porcentaje_dedicacion", nullable = false)
    private Integer porcentajeDedicacion;

    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 100, message = "El rol no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String rol;

    @Column(nullable = false)
    private Boolean activa = true;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
}
