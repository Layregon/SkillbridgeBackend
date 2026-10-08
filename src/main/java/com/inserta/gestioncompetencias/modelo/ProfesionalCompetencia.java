package com.inserta.gestioncompetencias.modelo;

import java.math.BigDecimal;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidad intermedia entre Profesional y Competencia.
 * Guarda los datos propios de la relacion: nivel, anos de experiencia
 * y si la competencia ha sido verificada.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profesional_competencias",
       uniqueConstraints = @UniqueConstraint(columnNames = {"profesional_id", "competencia_id"}))
public class ProfesionalCompetencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesional_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profesional profesional;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Competencia competencia;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer nivel;

    @DecimalMin("0.0")
    @Column(name = "anios_experiencia", precision = 4, scale = 1)
    private BigDecimal anosExperiencia;

    private Boolean verificada;
}
