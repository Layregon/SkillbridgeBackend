package com.inserta.gestioncompetencias.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secreto;

    @Value("${app.jwt.expiracion-ms}")
    private long expiracionMs;

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        Date fin = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(fin)
                .signWith(clave())
                .compact();
    }

    public String extraerUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean esValido(String token, String username) {
        Claims c = claims(token);
        return username.equals(c.getSubject()) && c.getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
