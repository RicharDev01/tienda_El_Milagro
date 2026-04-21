package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USU_ID")
    private Long id;

    @Column(name = "USU_USERNAME", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "USU_CLAVE", nullable = false, length = 255)
    private String clave;

    @Column(name = "USU_ESTADO", nullable = false)
    private Boolean esActivo;

    @Column(name = "USU_FEC_REG", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "USU_FEC_MOD")
    private LocalDateTime fechaModificacion;

    // En seguridad, suele ser util cargar roles junto al usuario al autenticar.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USUARIO_ROL",
            joinColumns = @JoinColumn(name = "USU_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROL_ID")
    )
    private Set<Rol> roles = new HashSet<>();

}

