package com.inserta.gestioncompetencias.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Representa una sede de la organizacion mediante su ciudad y pais.
 * Una sede puede agrupar a varios profesionales.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sedes", uniqueConstraints = @UniqueConstraint(columnNames = {"ciudad", "pais"}))
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La ciudad es obligatoria")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "La ciudad solo puede contener letras y espacios")
    @Column(nullable = false, length = 100)
    private String ciudad;

    @NotBlank(message = "El país es obligatorio")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "El país solo puede contener letras y espacios")
    @Column(nullable = false, length = 100)
    private String pais;

    @OneToMany(mappedBy = "sede")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Profesional> profesionales = new ArrayList<>();
}
