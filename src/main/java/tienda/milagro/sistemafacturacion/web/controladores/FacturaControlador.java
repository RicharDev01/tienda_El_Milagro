package tienda.milagro.sistemafacturacion.web.controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@Tag(name = "Facturas", description = "Gestion y consulta de facturas")
@SecurityRequirement(name = "bearerAuth")
public class FacturaControlador {

    private final FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio) {
        this.facturaServicio = facturaServicio;
    }

    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @Operation(summary = "Generar factura", description = "Registra una factura con sus detalles y aplica reglas de stock")
    public ResponseEntity<Factura> generarFactura(@RequestBody Factura facturaSolicitud) {
        Factura factura = facturaServicio.generarFactura(facturaSolicitud);
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/consultar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @Operation(summary = "Consultar facturas", description = "Consulta facturas por rango de fechas y/o DUI de cliente")
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
    @Operation(summary = "Consultar factura por ID", description = "Obtiene una factura especifica por su identificador")
    public ResponseEntity<Factura> consultarFacturaPorId(@PathVariable String idFactura) {
        Factura factura = facturaServicio.consultarFacturaPorId(idFactura);
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/consultar/{idFactura}/detalles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @Operation(summary = "Consultar detalles de factura", description = "Retorna los detalles de productos de una factura")
    public ResponseEntity<List<DetalleFactura>> consultarDetalleFactura(@PathVariable String idFactura) {
        Factura factura = facturaServicio.consultarFacturaPorId(idFactura);
        return ResponseEntity.ok(factura.getDetalles());
    }

    @GetMapping("/reporte-mensual")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Generar reporte mensual", description = "Genera reporte de ventas del mes y anio solicitados")
    public ResponseEntity<Map<String, Object>> generarReporteMensual(@RequestParam Integer mes,
                                                                     @RequestParam Integer anio) {
        Map<String, Object> reporte = facturaServicio.generarReporteMensual(mes, anio);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/reporte-mensual/pdf")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Exportar reporte mensual PDF", description = "Genera y descarga el reporte mensual de facturacion en formato PDF")
    public ResponseEntity<byte[]> generarReporteMensualPdf(@RequestParam Integer mes,
                                                           @RequestParam Integer anio) {
        byte[] contenidoPdf = facturaServicio.generarReporteMensualPdf(mes, anio);

        HttpHeaders encabezados = new HttpHeaders();
        encabezados.setContentType(MediaType.APPLICATION_PDF);
        encabezados.setContentDispositionFormData("attachment", "reporte_" + mes + "_" + anio + ".pdf");

        return ResponseEntity.ok()
                .headers(encabezados)
                .body(contenidoPdf);
    }

    @GetMapping("/reporte-mensual/excel")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Exportar reporte mensual Excel", description = "Genera y descarga el reporte mensual de facturacion en formato Excel")
    public ResponseEntity<byte[]> generarReporteMensualExcel(@RequestParam Integer mes,
                                                             @RequestParam Integer anio) {
        byte[] contenidoExcel = facturaServicio.generarReporteMensualExcel(mes, anio);

        HttpHeaders encabezados = new HttpHeaders();
        encabezados.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        encabezados.setContentDispositionFormData("attachment", "reporte_" + mes + "_" + anio + ".xlsx");

        return ResponseEntity.ok()
                .headers(encabezados)
                .body(contenidoExcel);
    }
}

