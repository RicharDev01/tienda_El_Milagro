package tienda.milagro.sistemafacturacion.web.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tienda.milagro.sistemafacturacion.dominio.excepciones.CredencialesInvalidasExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioInactivoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioNoEncontradoExcepcion;

import java.util.Map;

@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    @ExceptionHandler(CredencialesInvalidasExcepcion.class)
    public ResponseEntity<Map<String, String>> manejarCredencialesInvalidas(CredencialesInvalidasExcepcion excepcion) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", excepcion.getMessage()));
    }

    @ExceptionHandler(UsuarioInactivoExcepcion.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioInactivo(UsuarioInactivoExcepcion excepcion) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", excepcion.getMessage()));
    }

    @ExceptionHandler(UsuarioNoEncontradoExcepcion.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoEncontrado(UsuarioNoEncontradoExcepcion excepcion) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", excepcion.getMessage()));
    }

}

