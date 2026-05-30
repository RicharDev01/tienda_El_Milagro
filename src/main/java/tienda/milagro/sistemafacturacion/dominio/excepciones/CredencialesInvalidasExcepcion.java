package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class CredencialesInvalidasExcepcion extends RuntimeException {

    public CredencialesInvalidasExcepcion() {
        super("Credenciales invalidas.");
    }

}

