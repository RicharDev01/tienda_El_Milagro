package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class ClienteNoEncontradoExcepcion extends RuntimeException {

    public ClienteNoEncontradoExcepcion(String dui) {
        super("No se encontro ningun cliente con el DUI: " + dui);
    }

}

