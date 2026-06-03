package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tienda.milagro.sistemafacturacion.config.TokenJwtUtilidad;
import tienda.milagro.sistemafacturacion.dominio.excepciones.CredencialesInvalidasExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioInactivoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.repositorios.UsuarioRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import java.util.List;
import java.util.Map;

@Service
public class AutenticacionServicioImpl implements AutenticacionServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificadorContrasena;
    private final TokenJwtUtilidad tokenJwtUtilidad;

    public AutenticacionServicioImpl(UsuarioRepositorio usuarioRepositorio,
                                     PasswordEncoder codificadorContrasena,
                                     TokenJwtUtilidad tokenJwtUtilidad) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorContrasena = codificadorContrasena;
        this.tokenJwtUtilidad = tokenJwtUtilidad;
    }

    @Override
    public Map<String, Object> autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = validarCredenciales(nombreUsuario, contrasena);
        String token = generarToken(usuario);

        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombreRol())
                .toList();

        return Map.of(
                "idUsuario", usuario.getId(),
                "token", token,
                "tipo", "Bearer",
                "nombreUsuario", usuario.getNombreUsuario(),
                "roles", roles
        );
    }

    @Override
    public Usuario validarCredenciales(String nombreUsuario, String contrasena) {
        Usuario usuario = obtenerUsuarioPorNombreUsuario(nombreUsuario);

        if (!Boolean.TRUE.equals(usuario.getEsActivo())) {
            throw new UsuarioInactivoExcepcion();
        }

        boolean contrasenaValida = codificadorContrasena.matches(contrasena, usuario.getClave());
        if (!contrasenaValida) {
            throw new CredencialesInvalidasExcepcion();
        }

        return usuario;
    }

    @Override
    public String generarToken(Usuario usuario) {
        return tokenJwtUtilidad.generarToken(usuario);
    }

    private Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) {
        try {
            return usuarioRepositorio.findByNombreUsuarioIgnoreCase(nombreUsuario)
                    .orElseThrow(UsuarioNoEncontradoExcepcion::new);
        } catch (UsuarioNoEncontradoExcepcion excepcion) {
            // En inicio de sesion no se expone si el usuario existe o no.
            throw new CredencialesInvalidasExcepcion();
        }
    }

}

