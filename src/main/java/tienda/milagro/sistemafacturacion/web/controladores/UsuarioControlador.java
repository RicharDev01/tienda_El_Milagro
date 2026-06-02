package tienda.milagro.sistemafacturacion.web.controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda.milagro.sistemafacturacion.dominio.servicios.UsuarioServicio;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioActualizarDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioCrearDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioRespuestaDto;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Usuarios", description = "Gestion de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Devuelve la lista completa de usuarios registrados en el sistema."
    )
    public ResponseEntity<List<UsuarioRespuestaDto>> listar() {
        return ResponseEntity.ok(usuarioServicio.listarTodos());
    }

    @GetMapping("/listar/activos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Listar usuarios activos",
            description = "Devuelve unicamente los usuarios con estado activo."
    )
    public ResponseEntity<List<UsuarioRespuestaDto>> listarActivos() {
        return ResponseEntity.ok(usuarioServicio.listarActivos());
    }

    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Busca y devuelve un usuario especifico por su identificador unico."
    )
    public ResponseEntity<UsuarioRespuestaDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioServicio.buscarPorId(id));
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con los roles indicados. La clave se almacena cifrada."
    )
    public ResponseEntity<UsuarioRespuestaDto> registrar(@Valid @RequestBody UsuarioCrearDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioServicio.registrar(dto));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente. Si se envia una nueva clave, esta se cifra antes de almacenarse."
    )
    public ResponseEntity<UsuarioRespuestaDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioActualizarDto dto
    ) {
        return ResponseEntity.ok(usuarioServicio.modificar(id, dto));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina permanentemente un usuario del sistema por su ID."
    )
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cambiar-estado/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Cambiar estado del usuario",
            description = "Alterna el estado activo/inactivo de un usuario sin eliminarlo del sistema."
    )
    public ResponseEntity<UsuarioRespuestaDto> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioServicio.cambiarEstado(id));
    }

}