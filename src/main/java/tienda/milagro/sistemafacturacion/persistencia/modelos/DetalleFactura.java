package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(
        name = "DETALLE_FACTURA",
        uniqueConstraints = @UniqueConstraint(name = "UK_DETALLE_FACTURA_FAC_PRO", columnNames = {"FAC_ID", "PRO_ID"})
)
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DET_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAC_ID", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRO_ID", nullable = false)
    private Producto producto;

    @Column(name = "DET_CANTIDAD", nullable = false)
    private Integer cantidad;

    @Column(name = "DET_PRECIO_UNITARIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "DET_COSTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

}

