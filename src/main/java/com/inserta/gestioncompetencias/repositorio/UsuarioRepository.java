package com.inserta.gestioncompetencias.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.inserta.gestioncompetencias.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // BINARY obliga a MySQL a comparar el usuario respetando mayusculas y minusculas.
    // De esta forma "admin" no es lo mismo que "ADMIN" o "Admin".
    @Query(value = "SELECT * FROM usuarios WHERE BINARY username = :username LIMIT 1", nativeQuery = true)
    Optional<Usuario> findByUsername(@Param("username") String username);
}
