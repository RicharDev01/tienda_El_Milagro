package tienda.milagro.sistemafacturacion.dominio.excepciones;

public class UsuarioDuplicadoExcepcion extends RuntimeException {

    public UsuarioDuplicadoExcepcion(String nombreUsuario) {
        super("Ya existe un usuario con el nombre de usuario: " + nombreUsuario);
    }

}