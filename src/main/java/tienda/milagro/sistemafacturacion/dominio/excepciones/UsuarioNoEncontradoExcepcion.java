package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class UsuarioNoEncontradoExcepcion extends RuntimeException {

    public UsuarioNoEncontradoExcepcion() {
        super("Usuario no encontrado.");
    }

}

