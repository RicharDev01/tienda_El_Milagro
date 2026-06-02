package tienda.milagro.sistemafacturacion.persistencia.gestiones;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UsuarioActualizarDto {

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String nombreUsuario;

    @Size(min = 6, message = "La clave debe tener al menos 6 caracteres.")
    private String clave;

    @NotEmpty(message = "Debe asignar al menos un rol.")
    private Set<Long> rolesIds;

}