package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ProductoNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProductoRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.StockRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockServicioImpl implements StockServicio {

    private final StockRepositorio stockRepositorio;
    private final ProductoRepositorio productoRepositorio;

    public StockServicioImpl(StockRepositorio stockRepositorio,
                             ProductoRepositorio productoRepositorio) {
        this.stockRepositorio = stockRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    @Override
    public List<Stock> listarTodos() {
        return stockRepositorio.findAll();
    }

    @Override
    public Stock buscarPorProductoId(Long productoId) {
        validarProductoExiste(productoId);
        return stockRepositorio.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException(
                        "No existe registro de stock para el producto con id: " + productoId));
    }

    @Override
    @Transactional
    public Stock actualizarCantidad(Long productoId, Integer nuevaCantidad) {
        if (nuevaCantidad == null || nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad de stock debe ser mayor o igual a 0.");
        }

        Stock stock = buscarPorProductoId(productoId);
        stock.setCantidad(nuevaCantidad);
        return stockRepositorio.save(stock);
    }

    private void validarProductoExiste(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("El id del producto es obligatorio.");
        }

        if (!productoRepositorio.existsById(productoId)) {
            throw new ProductoNoEncontradoExcepcion(productoId);
        }
    }
}

