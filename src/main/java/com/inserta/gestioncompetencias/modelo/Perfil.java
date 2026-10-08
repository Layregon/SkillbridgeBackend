package com.inserta.gestioncompetencias.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.Pattern;

/**
 * Representa el perfil profesional o especialidad principal de la organizacion.
 * Un perfil puede estar asociado a varios profesionales.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "perfiles", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del perfil es obligatorio")
    @Pattern(regexp = "^[\\p{L}0-9 +#./_-]+$", message = "El perfil contiene caracteres no permitidos")
    @Column(nullable = false, length = 80)
    private String nombre;

    @OneToMany(mappedBy = "perfil")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Profesional> profesionales = new ArrayList<>();
}
