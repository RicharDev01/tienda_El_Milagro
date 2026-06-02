package tienda.milagro.sistemafacturacion.persistencia.gestiones;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UsuarioRespuestaDto {

    private Long id;
    private String nombreUsuario;
    private Boolean esActivo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaModificacion;
    private Set<RolRespuestaDto> roles;

}