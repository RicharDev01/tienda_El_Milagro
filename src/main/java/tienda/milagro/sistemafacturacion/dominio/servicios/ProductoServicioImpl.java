package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProductoRepositorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoServicioImpl implements ProductoServicio {

    private final ProductoRepositorio productoRepositorio;

    public ProductoServicioImpl(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
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
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró ningún producto con el id: " + id));
    }

    @Override
    @Transactional
    public Producto registrar(Producto producto) {
        validarPrecio(producto.getPrecioProducto());

        producto.setEsActivo(true);
        producto.setFechaRegistro(LocalDateTime.now());
        producto.setFechaModificacion(null);

        return productoRepositorio.save(producto);
    }

    @Override
    @Transactional
    public Producto actualizar(Long id, Producto productoActualizado) {
        validarPrecio(productoActualizado.getPrecioProducto());

        Producto productoExistente = buscarPorId(id);

        productoExistente.setNombreProducto(productoActualizado.getNombreProducto());
        productoExistente.setPrecioProducto(productoActualizado.getPrecioProducto());
        productoExistente.setProveedor(productoActualizado.getProveedor());
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
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio del producto no puede ser nulo ni negativo.");
        }
    }
}