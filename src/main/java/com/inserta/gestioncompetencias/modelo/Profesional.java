package com.inserta.gestioncompetencias.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Representa a un profesional de la organizacion.
 * Cada profesional pertenece a un perfil y a una sede, y puede tener
 * competencias, certificaciones y asignaciones a servicios.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profesionales", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Profesional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "El nombre solo puede contener letras, espacios, puntos, apóstrofes y guiones")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debes introducir un email válido")
    @Column(nullable = false, length = 150)
    private String email;

    @NotNull(message = "La antigüedad es obligatoria")
    @Min(value = 0, message = "La antigüedad no puede ser negativa")
    @Max(value = 60, message = "La antigüedad no puede superar 60 años")
    @Column(nullable = false)
    private Integer antiguedad = 0;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "perfil_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Perfil perfil;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Sede sede;

    @OneToMany(mappedBy = "profesional")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProfesionalCompetencia> profesionalCompetencias = new ArrayList<>();

    @OneToMany(mappedBy = "profesional")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Certificacion> certificaciones = new ArrayList<>();

    @OneToMany(mappedBy = "profesional")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Asignacion> asignaciones = new ArrayList<>();
}
