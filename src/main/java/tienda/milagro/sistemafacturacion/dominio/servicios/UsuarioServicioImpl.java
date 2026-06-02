package tienda.milagro.sistemafacturacion.dominio.servicios;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioDuplicadoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.repositorios.RolRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.UsuarioRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.RolRespuestaDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioActualizarDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioCrearDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioRespuestaDto;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Rol;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioRespuestaDto registrar(UsuarioCrearDto dto) {
        if (usuarioRepositorio.findByNombreUsuario(dto.getNombreUsuario()).isPresent()) {
            throw new UsuarioDuplicadoExcepcion(dto.getNombreUsuario());
        }

        Set<Rol> roles = resolverRoles(dto.getRolesIds());

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setClave(passwordEncoder.encode(dto.getClave()));
        usuario.setEsActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setRoles(roles);

        return mapearADto(usuarioRepositorio.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioRespuestaDto modificar(Long id, UsuarioActualizarDto dto) {
        Usuario usuario = obtenerUsuarioOExcepcion(id);

        usuarioRepositorio.findByNombreUsuario(dto.getNombreUsuario())
                .filter(encontrado -> !encontrado.getId().equals(id))
                .ifPresent(duplicado -> {
                    throw new UsuarioDuplicadoExcepcion(dto.getNombreUsuario());
                });

        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setFechaModificacion(LocalDateTime.now());
        usuario.setRoles(resolverRoles(dto.getRolesIds()));

        if (dto.getClave() != null && !dto.getClave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(dto.getClave()));
        }

        return mapearADto(usuarioRepositorio.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDto> listarTodos() {
        return usuarioRepositorio.findAll()
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDto> listarActivos() {
        return usuarioRepositorio.findByEsActivoTrue()
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioRespuestaDto buscarPorId(Long id) {
        return mapearADto(obtenerUsuarioOExcepcion(id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        obtenerUsuarioOExcepcion(id);
        usuarioRepositorio.deleteById(id);
    }

    @Override
    @Transactional
    public UsuarioRespuestaDto cambiarEstado(Long id) {
        Usuario usuario = obtenerUsuarioOExcepcion(id);
        usuario.setEsActivo(!usuario.getEsActivo());
        usuario.setFechaModificacion(LocalDateTime.now());
        return mapearADto(usuarioRepositorio.save(usuario));
    }

    // --- Metodos privados auxiliares ---

    private Usuario obtenerUsuarioOExcepcion(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(UsuarioNoEncontradoExcepcion::new);
    }

    private Set<Rol> resolverRoles(Set<Long> rolesIds) {
        Set<Rol> roles = new HashSet<>();
        for (Long rolId : rolesIds) {
            Rol rol = rolRepositorio.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + rolId));
            roles.add(rol);
        }
        return roles;
    }

    private UsuarioRespuestaDto mapearADto(Usuario usuario) {
        UsuarioRespuestaDto dto = new UsuarioRespuestaDto();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setEsActivo(usuario.getEsActivo());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setFechaModificacion(usuario.getFechaModificacion());

        Set<RolRespuestaDto> rolesDto = usuario.getRoles()
                .stream()
                .map(rol -> {
                    RolRespuestaDto rolDto = new RolRespuestaDto();
                    rolDto.setId(rol.getId());
                    rolDto.setNombre(rol.getNombreRol());
                    return rolDto;
                })
                .collect(Collectors.toSet());

        dto.setRoles(rolesDto);
        return dto;
    }

}