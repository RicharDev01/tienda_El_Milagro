package tienda.milagro.sistemafacturacion.web.controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ProveedorNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.servicios.ProductoServicio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Gestion de productos")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Listar productos", description = "Retorna todos los productos")
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
    @Operation(summary = "Listar productos activos", description = "Retorna solo productos activos")
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
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto especifico por su id")
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
    // POST /productos
    // Crea un nuevo producto. Recibe el cuerpo en formato JSON.
    // -------------------------------------------------------------------------
    @PostMapping({"", "/registrar"})
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar producto", description = "Crea un producto y su stock inicial en una sola transaccion")
    public ResponseEntity<?> registrar(@RequestBody Producto producto) {
        try {
            Producto guardado = productoServicio.registrarProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ProveedorNoEncontradoExcepcion e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
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
    @Operation(summary = "Actualizar producto", description = "Actualiza los campos editables de un producto existente")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody Producto producto) {
        try {
            Producto actualizado = productoServicio.actualizar(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (ProveedorNoEncontradoExcepcion e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
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
    @Operation(summary = "Desactivar producto", description = "Realiza eliminacion logica del producto")
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