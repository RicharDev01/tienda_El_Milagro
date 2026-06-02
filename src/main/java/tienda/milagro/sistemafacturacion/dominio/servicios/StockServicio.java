package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;

import java.util.List;

public interface StockServicio {

    List<Stock> listarTodos();

    Stock buscarPorProductoId(Long productoId);

    Stock actualizarCantidad(Long productoId, Integer nuevaCantidad);
}

