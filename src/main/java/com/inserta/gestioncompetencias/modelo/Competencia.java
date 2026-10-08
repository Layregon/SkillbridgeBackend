package com.inserta.gestioncompetencias.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Representa una competencia tecnica del catalogo de la organizacion.
 * La relacion con los profesionales se guarda en ProfesionalCompetencia.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "competencias", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Competencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la competencia es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El área es obligatoria")
    @Size(max = 100, message = "El área no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String area;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "competencia")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProfesionalCompetencia> profesionalCompetencias = new ArrayList<>();
}
