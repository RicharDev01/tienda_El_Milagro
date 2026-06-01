package tienda.milagro.sistemafacturacion.web.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Cliente;
import tienda.milagro.sistemafacturacion.dominio.servicios.ClienteServicio;

import java.util.List;

/**
 * Controlador REST para el modulo de Gestion de Clientes (CU03).
 * Expone endpoints bajo la ruta base /clientes.
 */
@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteControlador {

    // ------------------------------------------------------------------
    // Dependencias
    // ------------------------------------------------------------------

    private final ClienteServicio clienteServicio;

    /**
     * Inyeccion por constructor para facilitar pruebas unitarias.
     *
     * @param clienteServicio servicio de negocio de clientes
     */
    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    // ------------------------------------------------------------------
    // Endpoints
    // ------------------------------------------------------------------

    /**
     * Lista todos los clientes registrados (activos e inactivos).
     *
     * GET /clientes/listar
     *
     * @return 200 OK con la lista de clientes
     */
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<List<Cliente>> listar() {
        List<Cliente> clientes = clienteServicio.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Lista unicamente los clientes activos.
     *
     * GET /clientes/listar/activos
     *
     * @return 200 OK con la lista de clientes activos
     */
    @GetMapping("/listar/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<List<Cliente>> listarActivos() {
        List<Cliente> clientesActivos = clienteServicio.listarActivos();
        return ResponseEntity.ok(clientesActivos);
    }

    /**
     * Busca un cliente por su DUI.
     *
     * GET /clientes/buscar/{dui}
     *
     * @param dui documento unico de identidad del cliente
     * @return 200 OK con el cliente encontrado, o 404 si no existe
     */
    @GetMapping("/buscar/{dui}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> buscarPorDui(@PathVariable String dui) {
        try {
            Cliente cliente = clienteServicio.buscarPorDui(dui);
            return ResponseEntity.ok(cliente);
        } catch (jakarta.persistence.EntityNotFoundException excepcion) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(excepcion.getMessage());
        }
    }

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * POST /clientes/registrar
     *
     * @param cliente datos del cliente a registrar (body JSON)
     * @return 201 CREATED con el cliente registrado, o 409 si el DUI ya existe
     */
    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> registrar(@RequestBody Cliente cliente) {
        try {
            Cliente clienteRegistrado = clienteServicio.registrar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteRegistrado);
        } catch (IllegalArgumentException excepcion) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(excepcion.getMessage());
        }
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * PUT /clientes/actualizar/{dui}
     *
     * @param dui     documento unico de identidad del cliente a actualizar
     * @param cliente datos nuevos del cliente (body JSON)
     * @return 200 OK con el cliente actualizado, o 404 si no existe
     */
    @PutMapping("/actualizar/{dui}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> actualizar(@PathVariable String dui,
                                         @RequestBody Cliente cliente) {
        try {
            Cliente clienteActualizado = clienteServicio.modificar(dui, cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (jakarta.persistence.EntityNotFoundException excepcion) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(excepcion.getMessage());
        }
    }

    /**
     * Elimina un cliente de forma logica o fisica segun corresponda.
     * - Con facturas asociadas: deshabilita el cliente (esActivo = false).
     * - Sin facturas: elimina el registro fisicamente.
     *
     * DELETE /clientes/eliminar/{dui}
     *
     * @param dui documento unico de identidad del cliente a eliminar
     * @return 200 OK con mensaje de confirmacion, o 404 si no existe
     */
    @DeleteMapping("/eliminar/{dui}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<String> eliminar(@PathVariable String dui) {
        try {
            clienteServicio.eliminar(dui);
            return ResponseEntity.ok("Cliente con DUI " + dui + " eliminado correctamente.");
        } catch (jakarta.persistence.EntityNotFoundException excepcion) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(excepcion.getMessage());
        }
    }
}