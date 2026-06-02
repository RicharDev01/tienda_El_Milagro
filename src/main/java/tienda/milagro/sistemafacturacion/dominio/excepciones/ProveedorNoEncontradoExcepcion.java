package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class ProveedorNoEncontradoExcepcion extends RuntimeException {

    public ProveedorNoEncontradoExcepcion(Long idProveedor) {
        super("No se encontro ningun proveedor con el id: " + idProveedor);
    }
}

