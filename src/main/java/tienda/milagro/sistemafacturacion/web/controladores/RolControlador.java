package tienda.milagro.sistemafacturacion.web.controladores;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tienda.milagro.sistemafacturacion.dominio.repositorios.RolRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.RolRespuestaDto;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolControlador {

    private final RolRepositorio rolRepositorio;

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<RolRespuestaDto> listar() {
        return rolRepositorio.findAll()
                .stream()
                .map(rol -> {
                    RolRespuestaDto dto = new RolRespuestaDto();
                    dto.setId(rol.getId());
                    dto.setNombre(rol.getNombreRol());
                    return dto;
                })
                .toList();
    }
}
