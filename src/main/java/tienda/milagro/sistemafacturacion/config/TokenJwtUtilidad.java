package tienda.milagro.sistemafacturacion.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class TokenJwtUtilidad {

    private final String secretoJwt;
    private final long expiracionMs;

    public TokenJwtUtilidad(
            @Value("${seguridad.jwt.secreto:clave-super-secreta-jwt-para-sistema-facturacion-el-milagro-2026}") String secretoJwt,
            @Value("${seguridad.jwt.expiracion-ms:86400000}") long expiracionMs) {
        this.secretoJwt = secretoJwt;
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusMillis(expiracionMs);

        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombreRol())
                .toList();

        return Jwts.builder()
                .subject(usuario.getNombreUsuario())
                .claim("roles", roles)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(obtenerClaveSecreta())
                .compact();
    }

    public String obtenerNombreUsuario(String token) {
        return obtenerClaims(token).getSubject();
    }

    public boolean esTokenValido(String token, String nombreUsuario) {
        Claims claims = obtenerClaims(token);
        boolean tokenExpirado = claims.getExpiration().before(new Date());
        return claims.getSubject().equals(nombreUsuario) && !tokenExpirado;
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveSecreta())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenerClaveSecreta() {
        return Keys.hmacShaKeyFor(secretoJwt.getBytes(StandardCharsets.UTF_8));
    }

}

