package com.inserta.gestioncompetencias.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Representa un servicio o proyecto activo de un cliente.
 * Los profesionales se relacionan con los servicios mediante Asignacion.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servicios", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El cliente es obligatorio")
    @Size(max = 150, message = "El cliente no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String cliente;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "servicio")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Asignacion> asignaciones = new ArrayList<>();
}
