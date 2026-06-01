package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import java.util.Map;

public interface AutenticacionServicio {

    Map<String, Object> autenticar(String nombreUsuario, String contrasena);

    Usuario validarCredenciales(String nombreUsuario, String contrasena);

    String generarToken(Usuario usuario);

}

