package com.inserta.gestioncompetencias.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Usuario de acceso a la aplicacion.
 * Se utiliza por Spring Security para autenticar y aplicar los roles ADMIN/USER.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String username;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String rol;

    @Column(nullable = false)
    private Boolean activo = true;
}
