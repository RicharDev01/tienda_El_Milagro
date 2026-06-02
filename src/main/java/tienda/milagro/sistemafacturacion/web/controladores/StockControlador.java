package tienda.milagro.sistemafacturacion.web.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda.milagro.sistemafacturacion.dominio.servicios.StockServicio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
public class StockControlador {

    private final StockServicio stockServicio;

    public StockControlador(StockServicio stockServicio) {
        this.stockServicio = stockServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> listarTodos() {
        try {
            List<Stock> stocks = stockServicio.listarTodos();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{productoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> buscarPorProductoId(@PathVariable Long productoId) {
        try {
            Stock stock = stockServicio.buscarPorProductoId(productoId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/{productoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Long productoId,
                                                @RequestBody Stock stockSolicitud) {
        try {
            Integer nuevaCantidad = stockSolicitud != null ? stockSolicitud.getCantidad() : null;
            Stock stockActualizado = stockServicio.actualizarCantidad(productoId, nuevaCantidad);
            return ResponseEntity.ok(stockActualizado);
        } catch (IllegalArgumentException e) {
            return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> buildError(HttpStatus status, String mensaje) {
        return ResponseEntity
                .status(status)
                .body(Map.of("error", mensaje != null ? mensaje : "Error interno del servidor."));
    }
}

