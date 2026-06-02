package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class StockInsuficienteExcepcion extends RuntimeException {

    public StockInsuficienteExcepcion(String mensaje) {
        super(mensaje);
    }

}

