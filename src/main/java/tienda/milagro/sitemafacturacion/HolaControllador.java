package tienda.milagro.sitemafacturacion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaControllador {

    @GetMapping
    public String saludar(){
        return "Hola UES!";
    }

}
