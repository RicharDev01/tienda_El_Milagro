package tienda.milagro.sistemafacturacion.persistencia.modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    // Getters y Setters
    // ------------------------------------------------------------------
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getNombreProveedor() {
        return nombreProveedor;
    }
 
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
 
    public String getContacto() {
        return contacto;
    }
 
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
 
    public Boolean getEsActivo() {
        return esActivo;
    }
 
    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }
 
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
 
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
 
    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
 
    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
 
    public List<Producto> getProductos() {
        return productos;
    }
 
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
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
