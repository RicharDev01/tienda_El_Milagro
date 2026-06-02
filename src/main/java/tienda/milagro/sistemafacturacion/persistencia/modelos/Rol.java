package tienda.milagro.sistemafacturacion.persistencia.modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "ROL")
@Setter @Getter
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROL_ID")
    private Long id;

    @Column(name = "ROL_NOMBRE", nullable = false, unique = true, length = 50)
    private String nombreRol;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<Usuario> usuarios = new HashSet<>();

}

