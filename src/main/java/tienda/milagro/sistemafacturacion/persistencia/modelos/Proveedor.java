package tienda.milagro.sistemafacturacion.persistencia.modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "PROVEEDOR")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();

    // ------------------------------------------------------------------
    // Constructores
    // ------------------------------------------------------------------
 
    /** Constructor sin argumentos requerido por JPA. */
    public Proveedor() {
    }
 
    /** Constructor completo para uso en pruebas o inicializacion directa. */
    public Proveedor(String nombreProveedor,
                     String contacto,
                     Boolean esActivo,
                     LocalDateTime fechaRegistro,
                     LocalDateTime fechaModificacion) {
        this.nombreProveedor = nombreProveedor;
        this.contacto = contacto;
        this.esActivo = esActivo;
        this.fechaRegistro = fechaRegistro;
        this.fechaModificacion = fechaModificacion;
    }

    // ------------------------------------------------------------------
    // Utilidades de objeto
    // ------------------------------------------------------------------
 
    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombreProveedor='" + nombreProveedor + '\'' +
                ", esActivo=" + esActivo +
                '}';
    }

}
