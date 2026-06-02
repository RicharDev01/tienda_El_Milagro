package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ClienteNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.FacturaNoEncontradaExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ProductoNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ReporteInvalidoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.StockInsuficienteExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ClienteRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.FacturaRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProductoRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.StockRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.UsuarioRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Cliente;
import tienda.milagro.sistemafacturacion.persistencia.modelos.DetalleFactura;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Factura;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class FacturaServicioImpl implements FacturaServicio {

    private static final BigDecimal PORCENTAJE_IVA = new BigDecimal("0.13");

    private final FacturaRepositorio facturaRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final StockRepositorio stockRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public FacturaServicioImpl(FacturaRepositorio facturaRepositorio,
                               ClienteRepositorio clienteRepositorio,
                               ProductoRepositorio productoRepositorio,
                               StockRepositorio stockRepositorio,
                               UsuarioRepositorio usuarioRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.stockRepositorio = stockRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional
    public Factura generarFactura(Factura facturaSolicitud) {
        validarSolicitudFactura(facturaSolicitud);

        Usuario usuario = obtenerUsuarioValido(facturaSolicitud);
        Cliente cliente = obtenerClienteValido(facturaSolicitud.getCliente().getDui());

        Set<Long> productosUnicos = new HashSet<>();
        List<DetalleFactura> detallesProcesados = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (DetalleFactura detalleSolicitud : facturaSolicitud.getDetalles()) {
            validarDetalleSolicitud(detalleSolicitud);

            Long idProducto = detalleSolicitud.getProducto().getId();
            if (!productosUnicos.add(idProducto)) {
                throw new IllegalArgumentException("No se permite repetir el mismo producto en una factura.");
            }

            Producto producto = productoRepositorio.findById(idProducto)
                    .orElseThrow(() -> new ProductoNoEncontradoExcepcion(idProducto));

            if (!Boolean.TRUE.equals(producto.getEsActivo())) {
                throw new IllegalArgumentException("El producto con id " + idProducto + " esta inactivo.");
            }

            Stock stock = stockRepositorio.findByProductoId(idProducto)
                    .orElseThrow(() -> new StockInsuficienteExcepcion(
                            "No existe registro de stock para el producto con id: " + idProducto));

            validarStockDisponible(producto, stock, detalleSolicitud.getCantidad());

            BigDecimal precioHistorico = producto.getPrecioProducto().setScale(2, RoundingMode.HALF_UP);
            BigDecimal costo = precioHistorico
                    .multiply(BigDecimal.valueOf(detalleSolicitud.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleFactura detalle = new DetalleFactura();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleSolicitud.getCantidad());

            /*
             * Se almacena el precio historico del producto para preservar
             * la integridad de facturas antiguas aunque el producto cambie su precio.
             */
            detalle.setPrecioUnitario(precioHistorico);
            detalle.setCosto(costo);
            detallesProcesados.add(detalle);

            subtotal = subtotal.add(costo);
        }

        Factura factura = new Factura();
        factura.setId(generarIdFactura());
        factura.setUsuario(usuario);
        factura.setCliente(cliente);
        factura.setFecha(facturaSolicitud.getFecha() != null ? facturaSolicitud.getFecha() : LocalDate.now());
        factura.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));

        BigDecimal montoIva = calcularIva(factura.getSubtotal());
        factura.setIva(PORCENTAJE_IVA);
        factura.setTotal(factura.getSubtotal().add(montoIva).setScale(2, RoundingMode.HALF_UP));
        factura.setFechaRegistro(LocalDateTime.now());
        factura.setFechaModificacion(null);

        for (DetalleFactura detalle : detallesProcesados) {
            detalle.setFactura(factura);
            factura.getDetalles().add(detalle);
        }

        for (DetalleFactura detalle : detallesProcesados) {
            Stock stock = stockRepositorio.findByProductoId(detalle.getProducto().getId())
                    .orElseThrow(() -> new StockInsuficienteExcepcion(
                            "No existe registro de stock para el producto con id: " + detalle.getProducto().getId()));
            actualizarStock(stock, detalle.getCantidad());
            stockRepositorio.save(stock);
        }

        return facturaRepositorio.save(factura);
    }

    @Override
    public List<Factura> consultarFacturas(LocalDate fechaInicio, LocalDate fechaFin, String duiCliente) {
        if (duiCliente != null && !duiCliente.isBlank() && fechaInicio != null && fechaFin != null) {
            return facturaRepositorio.findByFechaBetweenAndClienteDui(fechaInicio, fechaFin, duiCliente);
        }

        if (duiCliente != null && !duiCliente.isBlank()) {
            return facturaRepositorio.findByClienteDui(duiCliente);
        }

        if (fechaInicio != null && fechaFin != null) {
            return facturaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
        }

        return facturaRepositorio.findAll().stream()
                .sorted(Comparator.comparing(Factura::getFecha).reversed())
                .toList();
    }

    @Override
    public Factura consultarFacturaPorId(String idFactura) {
        return facturaRepositorio.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaExcepcion(idFactura));
    }

    @Override
    public BigDecimal calcularIva(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser nulo ni negativo.");
        }

        return subtotal.multiply(PORCENTAJE_IVA).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, Object> generarReporteMensual(Integer mes, Integer anio) {
        if (mes == null || anio == null || mes < 1 || mes > 12 || anio < 2000) {
            throw new ReporteInvalidoExcepcion("Los parametros de reporte son invalidos.");
        }

        YearMonth periodo = YearMonth.of(anio, mes);
        LocalDate fechaInicio = periodo.atDay(1);
        LocalDate fechaFin = periodo.atEndOfMonth();

        List<Factura> facturas = facturaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
        BigDecimal totalMensual = facturaRepositorio.sumarTotalPorRango(fechaInicio, fechaFin)
                .setScale(2, RoundingMode.HALF_UP);

        return Map.of(
                "mes", mes,
                "anio", anio,
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin,
                "cantidadFacturas", facturas.size(),
                "totalMensual", totalMensual,
                "facturas", facturas
        );
    }

    private void validarSolicitudFactura(Factura facturaSolicitud) {
        if (facturaSolicitud == null) {
            throw new IllegalArgumentException("La solicitud de factura es obligatoria.");
        }

        if (facturaSolicitud.getCliente() == null || facturaSolicitud.getCliente().getDui() == null
                || facturaSolicitud.getCliente().getDui().isBlank()) {
            throw new IllegalArgumentException("Debe especificar el DUI del cliente.");
        }

        if (facturaSolicitud.getUsuario() == null || facturaSolicitud.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Debe especificar el usuario que genera la factura.");
        }

        if (facturaSolicitud.getDetalles() == null || facturaSolicitud.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La factura debe incluir al menos un detalle.");
        }
    }

    private void validarDetalleSolicitud(DetalleFactura detalleSolicitud) {
        if (detalleSolicitud == null || detalleSolicitud.getProducto() == null || detalleSolicitud.getProducto().getId() == null) {
            throw new IllegalArgumentException("Cada detalle debe incluir un producto valido.");
        }

        if (detalleSolicitud.getCantidad() == null || detalleSolicitud.getCantidad() < 1) {
            throw new IllegalArgumentException("La cantidad de cada detalle debe ser mayor o igual a 1.");
        }
    }

    private Usuario obtenerUsuarioValido(Factura facturaSolicitud) {
        Long idUsuario = facturaSolicitud.getUsuario().getId();
        return usuarioRepositorio.findById(idUsuario)
                .orElseThrow(UsuarioNoEncontradoExcepcion::new);
    }

    private Cliente obtenerClienteValido(String dui) {
        Cliente cliente = clienteRepositorio.findByDui(dui)
                .orElseThrow(() -> new ClienteNoEncontradoExcepcion(dui));

        if (!Boolean.TRUE.equals(cliente.getEsActivo())) {
            throw new IllegalArgumentException("El cliente con DUI " + dui + " se encuentra inactivo.");
        }

        return cliente;
    }

    private void validarStockDisponible(Producto producto, Stock stock, Integer cantidadSolicitada) {
        if (stock.getCantidad() == null || stock.getCantidad() < cantidadSolicitada) {
            throw new StockInsuficienteExcepcion(
                    "Stock insuficiente para el producto " + producto.getNombreProducto() + ". Disponible: " +
                            (stock.getCantidad() == null ? 0 : stock.getCantidad()) +
                            ", solicitado: " + cantidadSolicitada);
        }
    }

    private void actualizarStock(Stock stock, Integer cantidadSolicitada) {
        stock.setCantidad(stock.getCantidad() - cantidadSolicitada);
    }

    private String generarIdFactura() {
        List<String> ids = facturaRepositorio.buscarIdsFacturaDesc();
        if (ids.isEmpty()) {
            return "F00001";
        }

        String ultimoId = ids.getFirst();

        try {
            int correlativo = Integer.parseInt(ultimoId.substring(1));
            return String.format("F%05d", correlativo + 1);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("No fue posible generar el correlativo de factura.");
        }
    }
}

