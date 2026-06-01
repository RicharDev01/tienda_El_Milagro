package tienda.milagro.sistemafacturacion.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tienda.milagro.sistemafacturacion.dominio.repositorios.UsuarioRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import java.io.IOException;
import java.util.List;

@Component
public class FiltroJwtAutenticacion extends OncePerRequestFilter {

    private final TokenJwtUtilidad tokenJwtUtilidad;
    private final UsuarioRepositorio usuarioRepositorio;

    public FiltroJwtAutenticacion(TokenJwtUtilidad tokenJwtUtilidad,
                                  UsuarioRepositorio usuarioRepositorio) {
        this.tokenJwtUtilidad = tokenJwtUtilidad;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud,
                                    HttpServletResponse respuesta,
                                    FilterChain cadenaFiltros) throws ServletException, IOException {
        String encabezadoAutorizacion = solicitud.getHeader("Authorization");

        if (encabezadoAutorizacion == null || !encabezadoAutorizacion.startsWith("Bearer ")) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        String token = encabezadoAutorizacion.substring(7);
        String nombreUsuario;

        try {
            nombreUsuario = tokenJwtUtilidad.obtenerNombreUsuario(token);
        } catch (Exception excepcion) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        if (nombreUsuario == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario).orElse(null);

        if (usuario == null || !Boolean.TRUE.equals(usuario.getEsActivo())) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        if (!tokenJwtUtilidad.esTokenValido(token, usuario.getNombreUsuario())) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        List<SimpleGrantedAuthority> autoridades = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol()))
                .toList();

        UsernamePasswordAuthenticationToken autenticacion =
                new UsernamePasswordAuthenticationToken(usuario.getNombreUsuario(), null, autoridades);
        autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(solicitud));

        SecurityContextHolder.getContext().setAuthentication(autenticacion);
        cadenaFiltros.doFilter(solicitud, respuesta);
    }

}

