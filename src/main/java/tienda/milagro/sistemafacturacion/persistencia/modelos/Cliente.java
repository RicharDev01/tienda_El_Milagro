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

}

