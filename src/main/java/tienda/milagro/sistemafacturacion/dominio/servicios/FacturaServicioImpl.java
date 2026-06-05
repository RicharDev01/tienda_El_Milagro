package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ClienteNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.FacturaNoEncontradaExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ProductoNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.ReporteInvalidoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.StockInsuficienteExcepcion;
import tienda.milagro.sistemafacturacion.dominio.excepciones.UsuarioNoEncontradoExcepcion;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ClienteRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.FacturaRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProductoRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.StockRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.UsuarioRepositorio;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Cliente;
import tienda.milagro.sistemafacturacion.persistencia.modelos.DetalleFactura;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Factura;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Usuario;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class FacturaServicioImpl implements FacturaServicio {

    private static final BigDecimal PORCENTAJE_IVA = new BigDecimal("0.13");
    private static final String RUTA_LOGO_REPORTE_PDF = "reportes/logo-reporte.png";
    private static final DateTimeFormatter FORMATO_FECHA_REPORTE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat FORMATO_MONEDA_REPORTE = new DecimalFormat("$#,##0.00");

    private final FacturaRepositorio facturaRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final StockRepositorio stockRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public FacturaServicioImpl(FacturaRepositorio facturaRepositorio,
                               ClienteRepositorio clienteRepositorio,
                               ProductoRepositorio productoRepositorio,
                               StockRepositorio stockRepositorio,
                               UsuarioRepositorio usuarioRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.stockRepositorio = stockRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional
    public Factura generarFactura(Factura facturaSolicitud) {
        validarSolicitudFactura(facturaSolicitud);

        Usuario usuario = obtenerUsuarioValido(facturaSolicitud);
        Cliente cliente = obtenerClienteValido(facturaSolicitud.getCliente().getDui());

        Set<Long> productosUnicos = new HashSet<>();
        List<DetalleFactura> detallesProcesados = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (DetalleFactura detalleSolicitud : facturaSolicitud.getDetalles()) {
            validarDetalleSolicitud(detalleSolicitud);

            Long idProducto = detalleSolicitud.getProducto().getId();
            if (!productosUnicos.add(idProducto)) {
                throw new IllegalArgumentException("No se permite repetir el mismo producto en una factura.");
            }

            Producto producto = productoRepositorio.findById(idProducto)
                    .orElseThrow(() -> new ProductoNoEncontradoExcepcion(idProducto));

            if (!Boolean.TRUE.equals(producto.getEsActivo())) {
                throw new IllegalArgumentException("El producto con id " + idProducto + " esta inactivo.");
            }

            Stock stock = stockRepositorio.findByProductoId(idProducto)
                    .orElseThrow(() -> new StockInsuficienteExcepcion(
                            "No existe registro de stock para el producto con id: " + idProducto));

            validarStockDisponible(producto, stock, detalleSolicitud.getCantidad());

            BigDecimal precioHistorico = producto.getPrecioProducto().setScale(2, RoundingMode.HALF_UP);
            BigDecimal costo = precioHistorico
                    .multiply(BigDecimal.valueOf(detalleSolicitud.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleFactura detalle = new DetalleFactura();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleSolicitud.getCantidad());

            /*
             * Se almacena el precio historico del producto para preservar
             * la integridad de facturas antiguas aunque el producto cambie su precio.
             */
            detalle.setPrecioUnitario(precioHistorico);
            detalle.setCosto(costo);
            detallesProcesados.add(detalle);

            subtotal = subtotal.add(costo);
        }

        Factura factura = new Factura();
        factura.setId(generarIdFactura());
        factura.setUsuario(usuario);
        factura.setCliente(cliente);
        factura.setFecha(facturaSolicitud.getFecha() != null ? facturaSolicitud.getFecha() : LocalDate.now());
        factura.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));

        BigDecimal montoIva = calcularIva(factura.getSubtotal());
        factura.setIva(PORCENTAJE_IVA);
        factura.setTotal(factura.getSubtotal().add(montoIva).setScale(2, RoundingMode.HALF_UP));
        factura.setFechaRegistro(LocalDateTime.now());
        factura.setFechaModificacion(null);

        for (DetalleFactura detalle : detallesProcesados) {
            detalle.setFactura(factura);
            factura.getDetalles().add(detalle);
        }

        for (DetalleFactura detalle : detallesProcesados) {
            Stock stock = stockRepositorio.findByProductoId(detalle.getProducto().getId())
                    .orElseThrow(() -> new StockInsuficienteExcepcion(
                            "No existe registro de stock para el producto con id: " + detalle.getProducto().getId()));
            actualizarStock(stock, detalle.getCantidad());
            stockRepositorio.save(stock);
        }

        return facturaRepositorio.save(factura);
    }

    @Override
    public List<Factura> consultarFacturas(LocalDate fechaInicio, LocalDate fechaFin, String duiCliente) {
        if (duiCliente != null && !duiCliente.isBlank() && fechaInicio != null && fechaFin != null) {
            return facturaRepositorio.findByFechaBetweenAndClienteDui(fechaInicio, fechaFin, duiCliente);
        }

        if (duiCliente != null && !duiCliente.isBlank()) {
            return facturaRepositorio.findByClienteDui(duiCliente);
        }

        if (fechaInicio != null && fechaFin != null) {
            return facturaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
        }

        return facturaRepositorio.findAll().stream()
                .sorted(Comparator.comparing(Factura::getFecha).reversed())
                .toList();
    }

    @Override
    public Factura consultarFacturaPorId(String idFactura) {
        return facturaRepositorio.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaExcepcion(idFactura));
    }

    @Override
    public BigDecimal calcularIva(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser nulo ni negativo.");
        }

        return subtotal.multiply(PORCENTAJE_IVA).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, Object> generarReporteMensual(Integer mes, Integer anio) {
        if (mes == null || anio == null || mes < 1 || mes > 12 || anio < 2000) {
            throw new ReporteInvalidoExcepcion("Los parametros de reporte son invalidos.");
        }

        YearMonth periodo = YearMonth.of(anio, mes);
        LocalDate fechaInicio = periodo.atDay(1);
        LocalDate fechaFin = periodo.atEndOfMonth();

        List<Factura> facturas = facturaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
        BigDecimal totalMensual = facturaRepositorio.sumarTotalPorRango(fechaInicio, fechaFin)
                .setScale(2, RoundingMode.HALF_UP);

        return Map.of(
                "mes", mes,
                "anio", anio,
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin,
                "cantidadFacturas", facturas.size(),
                "totalMensual", totalMensual,
                "facturas", facturas
        );
    }

    @Override
    public byte[] generarReporteMensualPdf(Integer mes, Integer anio) {
        /*
         * Se reutiliza el reporte mensual existente
         * para garantizar consistencia entre la API,
         * el PDF y el Excel.
         */
        Map<String, Object> reporte = generarReporteMensual(mes, anio);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document documento = new Document();
            PdfWriter.getInstance(documento, baos);
            documento.open();

            agregarLogoReporteSiExiste(documento);

            com.lowagie.text.Font fonteTitulo = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fonteEncabezado = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fonteEncabezadoTabla = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD, Color.WHITE);
            com.lowagie.text.Font fonteNormal = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10);

            documento.add(new Paragraph("Reporte Mensual de Facturacion", fonteTitulo));
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("Mes: " + mes, fonteEncabezado));
            documento.add(new Paragraph("Anio: " + anio, fonteEncabezado));
            documento.add(new Paragraph("Fecha Inicio: " + formatearFechaFrances((LocalDate) reporte.get("fechaInicio")), fonteEncabezado));
            documento.add(new Paragraph("Fecha Fin: " + formatearFechaFrances((LocalDate) reporte.get("fechaFin")), fonteEncabezado));
            documento.add(new Paragraph("Cantidad de Facturas: " + reporte.get("cantidadFacturas"), fonteEncabezado));
            documento.add(new Paragraph("Total Mensual: " + formatearMoneda((BigDecimal) reporte.get("totalMensual")), fonteEncabezado));
            documento.add(new Paragraph(" "));

            Table tabla = new Table(6);
            tabla.setWidth(100);
            tabla.setPadding(5);

            Cell celdaFactura = new Cell(new Paragraph("Factura", fonteEncabezadoTabla));
            Cell celdaFecha = new Cell(new Paragraph("Fecha", fonteEncabezadoTabla));
            Cell celdaCliente = new Cell(new Paragraph("Cliente", fonteEncabezadoTabla));
            Cell celdaSubtotal = new Cell(new Paragraph("Subtotal", fonteEncabezadoTabla));
            Cell celdaIva = new Cell(new Paragraph("IVA", fonteEncabezadoTabla));
            Cell celdaTotal = new Cell(new Paragraph("Total", fonteEncabezadoTabla));

            aplicarEstiloEncabezado(celdaFactura);
            aplicarEstiloEncabezado(celdaFecha);
            aplicarEstiloEncabezado(celdaCliente);
            aplicarEstiloEncabezado(celdaSubtotal);
            aplicarEstiloEncabezado(celdaIva);
            aplicarEstiloEncabezado(celdaTotal);

            tabla.addCell(celdaFactura);
            tabla.addCell(celdaFecha);
            tabla.addCell(celdaCliente);
            tabla.addCell(celdaSubtotal);
            tabla.addCell(celdaIva);
            tabla.addCell(celdaTotal);

            @SuppressWarnings("unchecked")
            List<Factura> facturas = (List<Factura>) reporte.get("facturas");

            for (Factura factura : facturas) {
                BigDecimal montoIva = factura.getSubtotal().multiply(PORCENTAJE_IVA).setScale(2, RoundingMode.HALF_UP);

                tabla.addCell(new Cell(new Paragraph(factura.getId(), fonteNormal)));
                tabla.addCell(new Cell(new Paragraph(formatearFechaFrances(factura.getFecha()), fonteNormal)));
                tabla.addCell(new Cell(new Paragraph(obtenerNombreClienteParaReporte(factura), fonteNormal)));
                tabla.addCell(new Cell(new Paragraph(formatearMoneda(factura.getSubtotal()), fonteNormal)));
                tabla.addCell(new Cell(new Paragraph(formatearMoneda(montoIva), fonteNormal)));
                tabla.addCell(new Cell(new Paragraph(formatearMoneda(factura.getTotal()), fonteNormal)));
            }

            documento.add(tabla);
            documento.close();

            return baos.toByteArray();
        } catch (DocumentException | IOException ex) {
            throw new RuntimeException("Error al generar reporte PDF: " + ex.getMessage(), ex);
        }
    }

    private void agregarLogoReporteSiExiste(Document documento) throws IOException, DocumentException {
        ClassPathResource recursoLogo = new ClassPathResource(RUTA_LOGO_REPORTE_PDF);
        if (!recursoLogo.exists()) {
            return;
        }

        try (InputStream inputStream = recursoLogo.getInputStream()) {
            Image logo = Image.getInstance(inputStream.readAllBytes());
            logo.scaleToFit(130, 60);
            logo.setAlignment(Image.ALIGN_RIGHT);
            documento.add(logo);
            documento.add(new Paragraph(" "));
        }
    }

    private void aplicarEstiloEncabezado(Cell celda) {
        celda.setBackgroundColor(Color.DARK_GRAY);
    }

    private String formatearFechaFrances(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        return fecha.format(FORMATO_FECHA_REPORTE);
    }

    private String formatearMoneda(BigDecimal monto) {
        if (monto == null) {
            return "$0.00";
        }
        return FORMATO_MONEDA_REPORTE.format(monto.setScale(2, RoundingMode.HALF_UP));
    }

    private String obtenerNombreClienteParaReporte(Factura factura) {
        if (factura == null || factura.getCliente() == null) {
            return "N/A";
        }

        String nombre = factura.getCliente().getPrimerNombre() == null
                ? ""
                : factura.getCliente().getPrimerNombre().trim();
        String apellido = factura.getCliente().getPrimerApellido() == null
                ? ""
                : factura.getCliente().getPrimerApellido().trim();

        String nombreCompleto = (nombre + " " + apellido).trim();
        return nombreCompleto.isEmpty() ? "N/A" : nombreCompleto;
    }

    @Override
    public byte[] generarReporteMensualExcel(Integer mes, Integer anio) {
        /*
         * Se reutiliza el reporte mensual existente
         * para garantizar consistencia entre la API,
         * el PDF y el Excel.
         */
        Map<String, Object> reporte = generarReporteMensual(mes, anio);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XSSFWorkbook libroTrabajo = new XSSFWorkbook()) {

            XSSFSheet hoja = libroTrabajo.createSheet("Reporte Facturacion");
            CellStyle estiloEncabezadoExcel = crearEstiloEncabezadoExcel(libroTrabajo);
            CellStyle estiloMonedaExcel = crearEstiloMonedaExcel(libroTrabajo, false);
            CellStyle estiloMonedaTotalExcel = crearEstiloMonedaExcel(libroTrabajo, true);
            CellStyle estiloTextoTotalExcel = crearEstiloTextoTotalExcel(libroTrabajo);

            int numeroFila = 0;
            XSSFRow filaEncabezado = hoja.createRow(numeroFila++);

            String[] encabezados = {"Factura", "Fecha", "Cliente", "Subtotal", "IVA", "Total"};
            for (int i = 0; i < encabezados.length; i++) {
                XSSFCell celda = filaEncabezado.createCell(i);
                celda.setCellValue(encabezados[i]);
                celda.setCellStyle(estiloEncabezadoExcel);
            }

            @SuppressWarnings("unchecked")
            List<Factura> facturas = (List<Factura>) reporte.get("facturas");

            for (Factura factura : facturas) {
                BigDecimal montoIva = factura.getSubtotal().multiply(PORCENTAJE_IVA).setScale(2, RoundingMode.HALF_UP);

                XSSFRow fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(factura.getId());
                fila.createCell(1).setCellValue(formatearFechaFrances(factura.getFecha()));
                fila.createCell(2).setCellValue(obtenerNombreClienteParaReporte(factura));

                XSSFCell celdaSubtotal = fila.createCell(3);
                celdaSubtotal.setCellValue(factura.getSubtotal().doubleValue());
                celdaSubtotal.setCellStyle(estiloMonedaExcel);

                XSSFCell celdaIva = fila.createCell(4);
                celdaIva.setCellValue(montoIva.doubleValue());
                celdaIva.setCellStyle(estiloMonedaExcel);

                XSSFCell celdaTotal = fila.createCell(5);
                celdaTotal.setCellValue(factura.getTotal().doubleValue());
                celdaTotal.setCellStyle(estiloMonedaExcel);
            }

            XSSFRow filaTotal = hoja.createRow(numeroFila);
            XSSFCell celdaEtiquetaTotal = filaTotal.createCell(4);
            celdaEtiquetaTotal.setCellValue("TOTAL MENSUAL");
            celdaEtiquetaTotal.setCellStyle(estiloTextoTotalExcel);

            XSSFCell celdaValorTotal = filaTotal.createCell(5);
            celdaValorTotal.setCellValue(((BigDecimal) reporte.get("totalMensual")).doubleValue());
            celdaValorTotal.setCellStyle(estiloMonedaTotalExcel);

            for (int i = 0; i < encabezados.length; i++) {
                hoja.autoSizeColumn(i);
            }

            libroTrabajo.write(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error al generar reporte Excel: " + ex.getMessage(), ex);
        }
    }

    private CellStyle crearEstiloEncabezadoExcel(XSSFWorkbook libroTrabajo) {
        CellStyle estilo = libroTrabajo.createCellStyle();
        estilo.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.CENTER);

        org.apache.poi.ss.usermodel.Font fuente = libroTrabajo.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        return estilo;
    }

    private CellStyle crearEstiloMonedaExcel(XSSFWorkbook libroTrabajo, boolean negrita) {
        CellStyle estilo = libroTrabajo.createCellStyle();
        estilo.setDataFormat(libroTrabajo.createDataFormat().getFormat("$#,##0.00"));
        estilo.setAlignment(HorizontalAlignment.RIGHT);

        org.apache.poi.ss.usermodel.Font fuente = libroTrabajo.createFont();
        fuente.setBold(negrita);
        estilo.setFont(fuente);
        return estilo;
    }

    private CellStyle crearEstiloTextoTotalExcel(XSSFWorkbook libroTrabajo) {
        CellStyle estilo = libroTrabajo.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.RIGHT);

        org.apache.poi.ss.usermodel.Font fuente = libroTrabajo.createFont();
        fuente.setBold(true);
        estilo.setFont(fuente);
        return estilo;
    }

    private void validarSolicitudFactura(Factura facturaSolicitud) {
        if (facturaSolicitud == null) {
            throw new IllegalArgumentException("La solicitud de factura es obligatoria.");
        }

        if (facturaSolicitud.getCliente() == null || facturaSolicitud.getCliente().getDui() == null
                || facturaSolicitud.getCliente().getDui().isBlank()) {
            throw new IllegalArgumentException("Debe especificar el DUI del cliente.");
        }

        if (facturaSolicitud.getUsuario() == null || facturaSolicitud.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Debe especificar el usuario que genera la factura.");
        }

        if (facturaSolicitud.getDetalles() == null || facturaSolicitud.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La factura debe incluir al menos un detalle.");
        }
    }

    private void validarDetalleSolicitud(DetalleFactura detalleSolicitud) {
        if (detalleSolicitud == null || detalleSolicitud.getProducto() == null || detalleSolicitud.getProducto().getId() == null) {
            throw new IllegalArgumentException("Cada detalle debe incluir un producto valido.");
        }

        if (detalleSolicitud.getCantidad() == null || detalleSolicitud.getCantidad() < 1) {
            throw new IllegalArgumentException("La cantidad de cada detalle debe ser mayor o igual a 1.");
        }
    }

    private Usuario obtenerUsuarioValido(Factura facturaSolicitud) {
        Long idUsuario = facturaSolicitud.getUsuario().getId();
        return usuarioRepositorio.findById(idUsuario)
                .orElseThrow(UsuarioNoEncontradoExcepcion::new);
    }

    private Cliente obtenerClienteValido(String dui) {
        Cliente cliente = clienteRepositorio.findByDui(dui)
                .orElseThrow(() -> new ClienteNoEncontradoExcepcion(dui));

        if (!Boolean.TRUE.equals(cliente.getEsActivo())) {
            throw new IllegalArgumentException("El cliente con DUI " + dui + " se encuentra inactivo.");
        }

        return cliente;
    }

    private void validarStockDisponible(Producto producto, Stock stock, Integer cantidadSolicitada) {
        if (stock.getCantidad() == null || stock.getCantidad() < cantidadSolicitada) {
            throw new StockInsuficienteExcepcion(
                    "Stock insuficiente para el producto " + producto.getNombreProducto() + ". Disponible: " +
                            (stock.getCantidad() == null ? 0 : stock.getCantidad()) +
                            ", solicitado: " + cantidadSolicitada);
        }
    }

    private void actualizarStock(Stock stock, Integer cantidadSolicitada) {
        stock.setCantidad(stock.getCantidad() - cantidadSolicitada);
    }

    private String generarIdFactura() {
        List<String> ids = facturaRepositorio.buscarIdsFacturaDesc();
        if (ids.isEmpty()) {
            return "FAC00001";
        }

        String ultimoId = ids.getFirst();

        try {
            int correlativo = Integer.parseInt(
                    ultimoId.replace("FAC", "")
            );

            return String.format("FAC%07d", correlativo + 1);

        } catch (RuntimeException ex) {
            throw new IllegalStateException("No fue posible generar el correlativo de factura.");
        }
    }
}

