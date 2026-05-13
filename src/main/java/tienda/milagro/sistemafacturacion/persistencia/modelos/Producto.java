package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCTO")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRV_ID", nullable = false)
    private Proveedor proveedor;

    @Column(name = "PRO_NOMBRE")
    private String nombreProducto;

    @Column(name = "PRO_PRECIO_UNITARIO")
    private BigDecimal precioProducto;

    @Column(name = "PRO_ESTADO")
    private Boolean esActivo;

    @Column(name = "PROD_FEC_REG")
    private LocalDateTime fechaRegistro;

    @Column(name = "PROD_FEC_MOD")
    private LocalDateTime fechaModificacion;

    @OneToOne(mappedBy = "producto")
    private Stock stock;

}
