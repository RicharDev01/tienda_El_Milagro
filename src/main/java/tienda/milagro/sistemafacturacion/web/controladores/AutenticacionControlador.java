package tienda.milagro.sistemafacturacion.web.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tienda.milagro.sistemafacturacion.dominio.servicios.AutenticacionServicio;

import java.util.Map;

@RestController
@RequestMapping("/api/autenticacion")
public class AutenticacionControlador {

    private final AutenticacionServicio autenticacionServicio;

    public AutenticacionControlador(AutenticacionServicio autenticacionServicio) {
        this.autenticacionServicio = autenticacionServicio;
    }

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<Map<String, Object>> iniciarSesion(@RequestBody Map<String, String> solicitud) {
        String nombreUsuario = solicitud.getOrDefault("nombreUsuario", "");
        String contrasena = solicitud.getOrDefault("contrasena", "");

        Map<String, Object> respuesta = autenticacionServicio.autenticar(nombreUsuario, contrasena);
        return ResponseEntity.ok(respuesta);
    }

}

