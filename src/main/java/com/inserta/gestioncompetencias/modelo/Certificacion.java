package com.inserta.gestioncompetencias.modelo;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Representa una certificacion asociada a un profesional.
 * La fecha de caducidad permite generar avisos de renovacion.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificaciones")
public class Certificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la certificación es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 150)
    private String entidad;

    @NotNull
    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesional_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profesional profesional;
}
