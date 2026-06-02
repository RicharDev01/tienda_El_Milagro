package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class FacturaNoEncontradaExcepcion extends RuntimeException {

    public FacturaNoEncontradaExcepcion(String idFactura) {
        super("No se encontro ninguna factura con el id: " + idFactura);
    }

}

