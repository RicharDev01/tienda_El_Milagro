package tienda.milagro.sistemafacturacion.web.controladores;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tienda.milagro.sistemafacturacion.dominio.servicios.FacturaServicio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.DetalleFactura;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Factura;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/facturas")
public class FacturaControlador {

    private final FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio) {
        this.facturaServicio = facturaServicio;
    }

    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<Factura> generarFactura(@RequestBody Factura facturaSolicitud) {
        Factura factura = facturaServicio.generarFactura(facturaSolicitud);
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/consultar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<List<Factura>> consultarFacturas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String cliente) {

        if ((fechaInicio == null) != (fechaFin == null)) {
            throw new IllegalArgumentException("Debe enviar fechaInicio y fechaFin juntas.");
        }

        List<Factura> facturas = facturaServicio.consultarFacturas(fechaInicio, fechaFin, cliente);
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/consultar/{idFactura}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<Factura> consultarFacturaPorId(@PathVariable String idFactura) {
        Factura factura = facturaServicio.consultarFacturaPorId(idFactura);
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/consultar/{idFactura}/detalles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<List<DetalleFactura>> consultarDetalleFactura(@PathVariable String idFactura) {
        Factura factura = facturaServicio.consultarFacturaPorId(idFactura);
        return ResponseEntity.ok(factura.getDetalles());
    }

    @GetMapping("/reporte-mensual")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> generarReporteMensual(@RequestParam Integer mes,
                                                                     @RequestParam Integer anio) {
        Map<String, Object> reporte = facturaServicio.generarReporteMensual(mes, anio);
        return ResponseEntity.ok(reporte);
    }
}

