package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FACTURA")
public class Factura {

    @Id
    @Column(name = "FAC_ID", length = 10)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLI_DUI", nullable = false)
    private Cliente cliente;

    @Column(name = "FAC_FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "FAC_SUBTOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "FAC_IVA", nullable = false, precision = 4, scale = 2)
    private BigDecimal iva;

    @Column(name = "FAC_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "FAC_FEC_REG", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "FAC_FEC_MOD")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "factura")
    private List<DetalleFactura> detalles = new ArrayList<>();

}

