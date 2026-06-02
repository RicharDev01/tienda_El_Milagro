package tienda.milagro.sistemafacturacion.web.controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Sistema", description = "Endpoints basicos del sistema")
public class HolaControllador {

    @GetMapping
    @Operation(summary = "Saludo", description = "Endpoint de prueba para validar disponibilidad del API")
    public String saludar(){
        return "Hola UES!";
    }

}
