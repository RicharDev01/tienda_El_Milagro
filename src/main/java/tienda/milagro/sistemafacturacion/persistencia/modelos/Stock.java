package tienda.milagro.sistemafacturacion.persistencia.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "STOCK")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STK_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRO_ID", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "STK_CANTIDAD")
    private Integer cantidad;

}
