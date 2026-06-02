package tienda.milagro.sistemafacturacion.web.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda.milagro.sistemafacturacion.dominio.servicios.ProductoServicio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    // -------------------------------------------------------------------------
    // GET /productos/listar
    // Retorna todos los productos sin filtrar por estado.
    // -------------------------------------------------------------------------
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> listarTodos() {
        try {
            List<Producto> productos = productoServicio.listarTodos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // GET /productos/listar/activos
    // Retorna únicamente los productos con esActivo = true.
    // -------------------------------------------------------------------------
    @GetMapping("/listar/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> listarActivos() {
        try {
            List<Producto> productos = productoServicio.listarActivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // GET /productos/buscar/{id}
    // Retorna el producto correspondiente al id indicado.
    // -------------------------------------------------------------------------
    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Producto producto = productoServicio.buscarPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // POST /productos/registrar
    // Crea un nuevo producto. Recibe el cuerpo en formato JSON.
    // -------------------------------------------------------------------------
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrar(@RequestBody Producto producto) {
        try {
            Producto guardado = productoServicio.registrar(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // PUT /productos/actualizar/{id}
    // Actualiza los campos modificables de un producto existente.
    // -------------------------------------------------------------------------
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody Producto producto) {
        try {
            Producto actualizado = productoServicio.actualizar(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            // Distingue entre "no encontrado" e "argumento inválido"
            if (e instanceof IllegalArgumentException) {
                return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
            }
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /productos/eliminar/{id}
    // Realiza la eliminación lógica (esActivo = false).
    // -------------------------------------------------------------------------
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            productoServicio.desactivar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Método utilitario: construye la respuesta de error estándar del equipo.
    // Estructura: { "error": "mensaje detallado" }
    // -------------------------------------------------------------------------
    private ResponseEntity<Map<String, String>> buildError(HttpStatus status, String mensaje) {
        return ResponseEntity
                .status(status)
                .body(Map.of("error", mensaje != null ? mensaje : "Error interno del servidor."));
    }
}