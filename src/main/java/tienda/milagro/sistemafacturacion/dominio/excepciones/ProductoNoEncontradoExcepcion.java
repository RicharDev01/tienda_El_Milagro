package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class ProductoNoEncontradoExcepcion extends RuntimeException {

    public ProductoNoEncontradoExcepcion(Long idProducto) {
        super("No se encontro ningun producto con el id: " + idProducto);
    }

}

