package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.modelos.Factura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FacturaServicio {

    Factura generarFactura(Factura facturaSolicitud);

    List<Factura> consultarFacturas(LocalDate fechaInicio, LocalDate fechaFin, String duiCliente);

    Factura consultarFacturaPorId(String idFactura);

    BigDecimal calcularIva(BigDecimal subtotal);

    Map<String, Object> generarReporteMensual(Integer mes, Integer anio);

}

