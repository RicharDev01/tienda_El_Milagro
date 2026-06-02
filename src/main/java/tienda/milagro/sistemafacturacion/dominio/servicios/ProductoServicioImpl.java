package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ProductoNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ProveedorNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProductoRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProveedorRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.StockRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Proveedor;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoServicioImpl implements ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final ProveedorRepositorio proveedorRepositorio;
    private final StockRepositorio stockRepositorio;

    public ProductoServicioImpl(ProductoRepositorio productoRepositorio,
                                ProveedorRepositorio proveedorRepositorio,
                                StockRepositorio stockRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.proveedorRepositorio = proveedorRepositorio;
        this.stockRepositorio = stockRepositorio;
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepositorio.findAll();
    }

    @Override
    public List<Producto> listarActivos() {
        return productoRepositorio.findByEsActivoTrue();
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepositorio
                .findByNombreProductoContainingIgnoreCaseAndEsActivoTrue(nombre);
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoExcepcion(id));
    }

    @Override
    @Transactional
    public Producto registrarProducto(Producto solicitud) {
        Proveedor proveedor = validarProveedor(solicitud);
        validarNombreProducto(solicitud.getNombreProducto());
        validarPrecio(solicitud.getPrecioProducto());
        validarCantidadInicial(solicitud.getCantidadInicial());

        Producto producto = crearProducto(solicitud, proveedor);
        Producto productoGuardado = guardarProducto(producto);
        Stock stockInicial = crearStockInicial(productoGuardado, solicitud.getCantidadInicial());
        guardarStock(stockInicial);

        return productoGuardado;
    }

    @Override
    @Transactional
    public Producto actualizar(Long id, Producto productoActualizado) {
        Proveedor proveedor = validarProveedor(productoActualizado);
        validarNombreProducto(productoActualizado.getNombreProducto());
        validarPrecio(productoActualizado.getPrecioProducto());

        Producto productoExistente = buscarPorId(id);

        productoExistente.setNombreProducto(productoActualizado.getNombreProducto());
        productoExistente.setPrecioProducto(productoActualizado.getPrecioProducto());
        productoExistente.setProveedor(proveedor);
        productoExistente.setFechaModificacion(LocalDateTime.now());
        // La fechaRegistro y esActivo se preservan tal como estaban.

        return productoRepositorio.save(productoExistente);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setEsActivo(false);
        productoRepositorio.save(producto);
    }

    // -------------------------------------------------------------------------
    // Métodos privados de apoyo
    // -------------------------------------------------------------------------

    /**
     * Valida que el precio no sea nulo ni negativo.
     *
     * @param precio valor a validar
     * @throws IllegalArgumentException si el precio es nulo o menor a cero
     */
    private void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio del producto debe ser mayor a cero.");
        }
    }

    private Proveedor validarProveedor(Producto producto) {
        if (producto == null || producto.getProveedor() == null || producto.getProveedor().getId() == null) {
            throw new IllegalArgumentException("Debe especificar un proveedor valido.");
        }

        Long idProveedor = producto.getProveedor().getId();
        return proveedorRepositorio.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNoEncontradoExcepcion(idProveedor));
    }

    private void validarNombreProducto(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
    }

    private void validarCantidadInicial(Integer cantidadInicial) {
        if (cantidadInicial == null || cantidadInicial < 0) {
            throw new IllegalArgumentException("La cantidad inicial debe ser mayor o igual a 0.");
        }
    }

    private Producto crearProducto(Producto solicitud, Proveedor proveedor) {
        Producto producto = new Producto();
        producto.setProveedor(proveedor);
        producto.setNombreProducto(solicitud.getNombreProducto().trim());
        producto.setPrecioProducto(solicitud.getPrecioProducto());
        producto.setEsActivo(true);
        producto.setFechaRegistro(LocalDateTime.now());
        producto.setFechaModificacion(null);
        return producto;
    }

    private Stock crearStockInicial(Producto productoGuardado, Integer cantidadInicial) {
        Stock stock = new Stock();
        stock.setCantidad(cantidadInicial);

        /*
         * Todo producto registrado debe poseer
         * un registro de stock asociado desde su creacion.
         */
        stock.setProducto(productoGuardado);
        productoGuardado.setStock(stock);
        return stock;
    }

    private Producto guardarProducto(Producto producto) {
        return productoRepositorio.save(producto);
    }

    private void guardarStock(Stock stock) {
        stockRepositorio.save(stock);
    }
}