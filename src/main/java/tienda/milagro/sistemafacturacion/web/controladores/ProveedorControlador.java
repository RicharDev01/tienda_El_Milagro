package tienda.milagro.sistemafacturacion.web.controladores;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Proveedor;
import tienda.milagro.sistemafacturacion.dominio.servicios.ProveedorServicio;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el modulo de Gestion de Proveedores (CU02).
 * Expone los endpoints bajo la ruta base /proveedores.
 *
 * Perfil activo: dev-david
 *
 * Codigos HTTP utilizados:
 *   200 OK         - Operacion exitosa
 *   201 CREATED    - Recurso creado correctamente
 *   400 BAD REQUEST - Error de validacion de negocio (nombre duplicado, estado invalido)
 *   404 NOT FOUND  - Proveedor no encontrado
 */
@RestController
@RequestMapping("/proveedores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ProveedorControlador {

    // ------------------------------------------------------------------
    // Dependencias
    // ------------------------------------------------------------------

    private final ProveedorServicio proveedorServicio;

    /**
     * Inyeccion por constructor.
     *
     * @param proveedorServicio servicio de negocio de proveedores
     */
    public ProveedorControlador(ProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    // ------------------------------------------------------------------
    // Endpoints de consulta
    // ------------------------------------------------------------------

    /**
     * Lista todos los proveedores (activos e inactivos).
     *
     * GET /proveedores/listar
     *
     * @return 200 OK con la lista completa de proveedores
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Proveedor>> listar() {
        return ResponseEntity.ok(proveedorServicio.listarTodos());
    }

    /**
     * Lista unicamente los proveedores activos.
     *
     * GET /proveedores/listar/activos
     *
     * @return 200 OK con la lista de proveedores activos
     */
    @GetMapping("/listar/activos")
    public ResponseEntity<List<Proveedor>> listarActivos() {
        return ResponseEntity.ok(proveedorServicio.listarActivos());
    }

    /**
     * Busca un proveedor por su id.
     *
     * GET /proveedores/buscar/{id}
     *
     * @param id identificador del proveedor
     * @return 200 OK con el proveedor, o 404 si no existe
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(proveedorServicio.buscarPorId(id));
        } catch (EntityNotFoundException excepcion) {
            return respuestaError(HttpStatus.NOT_FOUND, excepcion.getMessage());
        }
    }

    /**
     * Busca proveedores cuyo nombre contenga el texto indicado.
     *
     * GET /proveedores/buscar/nombre?valor=texto
     *
     * @param valor fragmento del nombre a buscar
     * @return 200 OK con la lista de coincidencias
     */
    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<Proveedor>> buscarPorNombre(
            @RequestParam(name = "valor") String valor) {
        return ResponseEntity.ok(proveedorServicio.buscarPorNombre(valor));
    }

    // ------------------------------------------------------------------
    // Endpoints de escritura
    // ------------------------------------------------------------------

    /**
     * Registra un nuevo proveedor en el sistema.
     *
     * POST /proveedores/registrar
     * Body: JSON con los datos del proveedor
     *
     * @param proveedor datos del proveedor a registrar
     * @return 201 CREATED con el proveedor registrado, o 400 si el nombre ya existe
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Proveedor proveedor) {
        try {
            Proveedor proveedorRegistrado = proveedorServicio.registrar(proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorRegistrado);
        } catch (IllegalArgumentException excepcion) {
            return respuestaError(HttpStatus.BAD_REQUEST, excepcion.getMessage());
        }
    }

    /**
     * Actualiza los datos de un proveedor existente.
     *
     * PUT /proveedores/actualizar/{id}
     * Body: JSON con los nuevos datos del proveedor
     *
     * @param id               identificador del proveedor a actualizar
     * @param datosActualizados nuevos datos del proveedor
     * @return 200 OK con el proveedor actualizado, 404 si no existe, 400 si nombre duplicado
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                         @RequestBody Proveedor datosActualizados) {
        try {
            return ResponseEntity.ok(proveedorServicio.modificar(id, datosActualizados));
        } catch (EntityNotFoundException excepcion) {
            return respuestaError(HttpStatus.NOT_FOUND, excepcion.getMessage());
        } catch (IllegalArgumentException excepcion) {
            return respuestaError(HttpStatus.BAD_REQUEST, excepcion.getMessage());
        }
    }

    /**
     * Desactiva un proveedor mediante eliminacion logica (esActivo = false).
     *
     * DELETE /proveedores/eliminar/{id}
     *
     * @param id identificador del proveedor a desactivar
     * @return 200 OK con mensaje de confirmacion, 404 si no existe, 400 si ya estaba inactivo
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            proveedorServicio.desactivar(id);
            return ResponseEntity.ok(
                    Map.of("mensaje", "Proveedor con id " + id + " desactivado correctamente."));
        } catch (EntityNotFoundException excepcion) {
            return respuestaError(HttpStatus.NOT_FOUND, excepcion.getMessage());
        } catch (IllegalStateException excepcion) {
            return respuestaError(HttpStatus.BAD_REQUEST, excepcion.getMessage());
        }
    }

    /**
     * Reactiva un proveedor previamente desactivado.
     *
     * PATCH /proveedores/reactivar/{id}
     *
     * @param id identificador del proveedor a reactivar
     * @return 200 OK con el proveedor reactivado, 404 si no existe, 400 si ya estaba activo
     */
    @PatchMapping("/reactivar/{id}")
    public ResponseEntity<?> reactivar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(proveedorServicio.reactivar(id));
        } catch (EntityNotFoundException excepcion) {
            return respuestaError(HttpStatus.NOT_FOUND, excepcion.getMessage());
        } catch (IllegalStateException excepcion) {
            return respuestaError(HttpStatus.BAD_REQUEST, excepcion.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Metodos privados de apoyo
    // ------------------------------------------------------------------

    /**
     * Construye una respuesta de error estandarizada con cuerpo JSON.
     *
     * @param estado   codigo HTTP a retornar
     * @param mensaje  descripcion del error
     * @return ResponseEntity con mapa {error: mensaje}
     */
    private ResponseEntity<Map<String, String>> respuestaError(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("error", mensaje));
    }
}