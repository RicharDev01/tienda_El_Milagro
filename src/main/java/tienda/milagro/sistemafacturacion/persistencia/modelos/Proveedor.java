package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROVEEDOR")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRV_ID")
    private Long id;

    @Column(name = "PRV_NOMBRE")
    private String nombreProveedor;

    @Column(name = "PRV_CONTACTO")
    private String contacto;

    @Column(name = "PRV_ESTADO")
    private Boolean esActivo;

    @Column(name = "PRV_FEC_REG")
    private LocalDateTime fechaRegistro;

    @Column(name = "PRV_FEC_MOD")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos = new ArrayList<>();

}
