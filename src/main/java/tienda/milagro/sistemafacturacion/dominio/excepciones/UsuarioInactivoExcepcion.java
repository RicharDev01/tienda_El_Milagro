package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class UsuarioInactivoExcepcion extends RuntimeException {

    public UsuarioInactivoExcepcion() {
        super("El usuario esta inactivo.");
    }

}

