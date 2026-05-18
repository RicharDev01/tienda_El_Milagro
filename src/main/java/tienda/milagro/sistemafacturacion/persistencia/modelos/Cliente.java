package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLIENTE")
public class Cliente {

    @Id
    @Column(name = "CLI_DUI", length = 10)
    private String dui;

    // CLI_ID se genera en base de datos (SERIAL) y se mantiene como identificador interno.
    @Column(name = "CLI_ID", unique = true, insertable = false, updatable = false)
    private Long idInterno;

    @Column(name = "CLI_PRIMER_NOMBRE", nullable = false, length = 50)
    private String primerNombre;

    @Column(name = "CLI_SEGUNDO_NOMBRE", length = 50)
    private String segundoNombre;

    @Column(name = "CLI_PRIMER_APELLIDO", nullable = false, length = 50)
    private String primerApellido;

    @Column(name = "CLI_SEGUNDO_APELLIDO", length = 50)
    private String segundoApellido;

    @Column(name = "CLI_FECHA_NAC")
    private LocalDate fechaNacimiento;

    @Column(name = "CLI_ESTADO", nullable = false)
    private Boolean esActivo;

    @Column(name = "CLI_FEC_REG", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "CLI_FEC_MOD")
    private LocalDateTime fechaModificacion;

    // ------------------------------------------------------------------
    // Constructores
    // ------------------------------------------------------------------
 
    /** Constructor sin argumentos requerido por JPA. */
    public Cliente() {
    }
 
    /** Constructor completo para uso en pruebas o inicializacion directa. */
    public Cliente(String dui,
                   String primerNombre,
                   String segundoNombre,
                   String primerApellido,
                   String segundoApellido,
                   LocalDate fechaNacimiento,
                   Boolean esActivo,
                   LocalDateTime fechaRegistro,
                   LocalDateTime fechaModificacion) {
        this.dui = dui;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.esActivo = esActivo;
        this.fechaRegistro = fechaRegistro;
        this.fechaModificacion = fechaModificacion;
    }
 
    // ------------------------------------------------------------------
    // Getters y Setters
    // ------------------------------------------------------------------
 
    public String getDui() {
        return dui;
    }
 
    public void setDui(String dui) {
        this.dui = dui;
    }
 
    /**
     * Solo getter: idInterno es asignado por la BD y nunca debe modificarse
     * desde la aplicacion (insertable=false, updatable=false).
     */
    public Long getIdInterno() {
        return idInterno;
    }
 
    public String getPrimerNombre() {
        return primerNombre;
    }
 
    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }
 
    public String getSegundoNombre() {
        return segundoNombre;
    }
 
    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }
 
    public String getPrimerApellido() {
        return primerApellido;
    }
 
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }
 
    public String getSegundoApellido() {
        return segundoApellido;
    }
 
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }
 
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
 
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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
 
    // ------------------------------------------------------------------
    // Utilidades de objeto
    // ------------------------------------------------------------------
 
    @Override
    public String toString() {
        return "Cliente{" +
                "dui='" + dui + '\'' +
                ", idInterno=" + idInterno +
                ", primerNombre='" + primerNombre + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", esActivo=" + esActivo +
                '}';
    }
}

