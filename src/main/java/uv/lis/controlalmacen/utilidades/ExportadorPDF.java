package uv.lis.controlalmacen.utilidades;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import uv.lis.controlalmacen.modelo.dto.*;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExportadorPDF {

    private static final DeviceRgb COLOR_ENCABEZADO = new DeviceRgb(4, 76, 135);
        private static final DateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    private static class DetalleAgrupado {

        private final Factura factura;
        private final DetallesFactura detalle;

        public DetalleAgrupado(Factura factura, DetallesFactura detalle) {
            this.factura = factura;
            this.detalle = detalle;
        }

        public Factura getFactura() {
            return factura;
        }

        public DetallesFactura getDetalle() {
            return detalle;
        }
    }

    private static TreeMap<Integer, Map<String, List<DetalleAgrupado>>> agruparFacturasPorPartida(List<Factura> facturas,
                                                                                                  String partidaFiltrada) {

        TreeMap<Integer, Map<String, List<DetalleAgrupado>>> partidas = new TreeMap<>();

        for (Factura factura : facturas) {
            for (DetallesFactura detalle : factura.getDetallesFactura()) {
                if (partidaFiltrada != null && !partidaFiltrada.isBlank() && !detalle.getDescripcionPartida()
                        .equalsIgnoreCase(partidaFiltrada)) {
                    continue;
                }

                Integer codigoPartida = detalle.getCodigoPartida();
                partidas.putIfAbsent( codigoPartida, new LinkedHashMap<>());
                Map<String, List<DetalleAgrupado>> facturasPartida = partidas.get(codigoPartida);

                facturasPartida.putIfAbsent(factura.getFolio(), new ArrayList<>());
                facturasPartida.get(factura.getFolio()).add(new DetalleAgrupado(factura,detalle));
            }
        }
        return partidas;
    }

    public static void generarReporteIngresos(String rutaPdf, List<Factura> facturas,
                                              LocalDate fechaInicio, LocalDate fechaFinal, String partidaFiltrada)
            throws FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));

        Document documento =new Document(pdf, PageSize.A4.rotate());
        documento.setMargins(40,40,40,40);

        agregarLogoYTitulo(documento, "REPORTE DE INGRESOS");

        agregarPeriodo(documento, fechaInicio, fechaFinal);

        TreeMap<Integer, Map<String, List<DetalleAgrupado>>> partidas = agruparFacturasPorPartida(facturas, partidaFiltrada);

        double totalGeneral = 0;

        for (Integer codigoPartida : partidas.keySet()) {

            Map<String, List<DetalleAgrupado>>facturasPartida = partidas.get(codigoPartida);

            String descripcionPartida = facturasPartida.values().iterator().next().get(0).getDetalle().getDescripcionPartida();

            agregarTituloPartida(documento, codigoPartida, descripcionPartida);

            double totalPartida = 0;

            for (String folio : facturasPartida.keySet()) {

                List<DetalleAgrupado> detalles =facturasPartida.get(folio);

                Factura factura = detalles.get(0).getFactura();

                agregarDatosFactura(documento, factura);

                Table tabla = crearTablaDetalles();

                double subtotalFactura = 0;

                for (DetalleAgrupado detalle : detalles) {

                    DetallesFactura d = detalle.getDetalle();
                    double importe = d.getCantidad() * d.getCostoUnitario();
                    subtotalFactura += importe;

                    tabla.addCell(crearCelda(d.getDescripcion()));
                    tabla.addCell(crearCelda(String.valueOf(d.getCantidad())));
                    tabla.addCell(crearCelda(String.format("$%,.2f", d.getCostoUnitario())));

                    tabla.addCell(crearCelda(String.format("$%,.2f", importe)));
                }

                documento.add(tabla);
                documento.add(new Paragraph("Subtotal factura-partida: " + String.format("$%,.2f", subtotalFactura))
                                .setBold().setTextAlignment(TextAlignment.RIGHT).setMarginBottom(15));
                totalPartida += subtotalFactura;
            }

            documento.add(new Paragraph("TOTAL PARTIDA " + descripcionPartida.toUpperCase() + ": " + String.format(
                    "$%,.2f", totalPartida)).setBold().setFontColor(COLOR_ENCABEZADO).setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20));

            totalGeneral += totalPartida;
        }

        documento.add(new Paragraph("TOTAL GENERAL DEL REPORTE: "+ String.format("$%,.2f", totalGeneral)).setBold()
                .setFontSize(16).setTextAlignment(TextAlignment.RIGHT));
        documento.close();
    }

    public static void generarReporteEgresos(String rutaPdf, List<Solicitud> solicitudes,
                                             LocalDate fechaInicio, LocalDate fechaFinal, String partidaFiltrada)
            throws FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));
        Document documento = new Document(pdf, PageSize.A4.rotate());
        documento.setMargins(40, 40, 40, 40);

        agregarLogoYTitulo(documento, "REPORTE DE EGRESOS");
        agregarPeriodo(documento, fechaInicio, fechaFinal);

        TreeMap<Integer, Map<Integer, List<DetallesSolicitud>>> partidas = new TreeMap<>();
        Map<Integer, Solicitud> porNumero = new LinkedHashMap<>();

        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getDetallesSolicitud() == null) continue;
            porNumero.put(solicitud.getNoSolicitud(), solicitud);
            for (DetallesSolicitud d : solicitud.getDetallesSolicitud()) {
                if (partidaFiltrada != null && !partidaFiltrada.isBlank()
                        && !d.getDescripcionPartida().equalsIgnoreCase(partidaFiltrada)) continue;
                Integer codigo = d.getCodigoPartida();
                partidas.putIfAbsent(codigo, new LinkedHashMap<>());
                partidas.get(codigo).putIfAbsent(solicitud.getNoSolicitud(), new ArrayList<>());
                partidas.get(codigo).get(solicitud.getNoSolicitud()).add(d);
            }
        }

        int totalGeneral = 0;

        for (Integer codigoPartida : partidas.keySet()) {
            Map<Integer, List<DetallesSolicitud>> solicitudesPartida = partidas.get(codigoPartida);
            String descripcionPartida = solicitudesPartida.values().iterator().next().get(0).getDescripcionPartida();

            agregarTituloPartida(documento, codigoPartida, descripcionPartida);

            int totalPartida = 0;

            for (Integer noSolicitud : solicitudesPartida.keySet()) {
                List<DetallesSolicitud> detalles = solicitudesPartida.get(noSolicitud);
                Solicitud solicitud = porNumero.get(noSolicitud);

                agregarDatosSolicitud(documento, solicitud);

                Table tabla = crearTablaEgresos();
                int subtotal = 0;
                for (DetallesSolicitud d : detalles) {
                    tabla.addCell(crearCelda(d.getDescripcionItem() != null ? d.getDescripcionItem() : ""));
                    tabla.addCell(crearCelda(String.valueOf(d.getCantidad())));
                    tabla.addCell(crearCelda(d.getUso() != null ? d.getUso() : ""));
                    subtotal += d.getCantidad();
                }
                documento.add(tabla);
                documento.add(new Paragraph("Subtotal solicitud-partida: " + subtotal + " unidades")
                        .setBold().setTextAlignment(TextAlignment.RIGHT).setMarginBottom(15));
                totalPartida += subtotal;
            }

            documento.add(new Paragraph("TOTAL PARTIDA " + descripcionPartida.toUpperCase() + ": "
                    + totalPartida + " unidades")
                    .setBold().setFontColor(COLOR_ENCABEZADO).setTextAlignment(TextAlignment.RIGHT).setMarginBottom(20));
            totalGeneral += totalPartida;
        }

        documento.add(new Paragraph("TOTAL GENERAL DEL REPORTE: " + totalGeneral + " unidades")
                .setBold().setFontSize(16).setTextAlignment(TextAlignment.RIGHT));
        documento.close();
    }

    public static void generarReporteInventario(String rutaPdf, List<ItemAlmacenado> items, String filtroStock)
            throws FileNotFoundException {

        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));
        Document documento = new Document(pdf, PageSize.A4.rotate());
        documento.setMargins(40, 40, 40, 40);

        agregarLogoYTitulo(documento, "REPORTE DE INVENTARIO EN ALMACÉN");

        documento.add(new Paragraph("Fecha de generación: " + FORMATO_FECHA.format(new Date()))
                .setMarginBottom(5));

        documento.add(new Paragraph("Nivel de stock: " + obtenerTextoFiltroStock(filtroStock))
                .setMarginBottom(10));

        Table tabla = crearTablaInventario();

        for (ItemAlmacenado item : items) {
            tabla.addCell(crearCelda(obtenerTextoSeguro(item.getIdItem())));
            tabla.addCell(crearCelda(obtenerTextoSeguro(item.getDescripcionItem())));
            tabla.addCell(crearCeldaCentrada(String.valueOf(item.getExistencias())));
            tabla.addCell(crearCeldaCentrada(String.valueOf(item.getStockMin())));
            tabla.addCell(crearCeldaCentrada(String.valueOf(item.getStockMax())));
        }

        documento.add(tabla);

        documento.close();
    }

    public static void generarFormatoEntregaRecepcion(String rutaPdf, Solicitud solicitud,
                                                       List<DetallesSolicitud> detalles)
            throws FileNotFoundException {

        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));
        Document documento = new Document(pdf, PageSize.A4);
        documento.setMargins(50, 50, 50, 50);

        agregarLogoYTitulo(documento, "FORMATO DE ENTREGA-RECEPCIÓN");

        documento.add(new Paragraph("Folio de solicitud: #" + solicitud.getNoSolicitud())
                .setBold().setMarginBottom(3));
        documento.add(new Paragraph("Fecha: " + FORMATO_FECHA.format(new Date()))
                .setMarginBottom(3));
        documento.add(new Paragraph("Solicitante: " + solicitud.getNombreCompleto())
                .setMarginBottom(3));
        documento.add(new Paragraph("No. Empleado: " + solicitud.getNoEmpleado())
                .setMarginBottom(15));

        Table tabla = new Table(new float[]{2, 5, 2, 4});
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell(crearHeaderTabla("Código"));
        tabla.addHeaderCell(crearHeaderTabla("Descripción del artículo"));
        tabla.addHeaderCell(crearHeaderTabla("Cantidad"));
        tabla.addHeaderCell(crearHeaderTabla("Uso / Destino"));

        for (DetallesSolicitud d : detalles) {
            tabla.addCell(crearCelda(d.getIdItem() != null ? d.getIdItem() : ""));
            tabla.addCell(crearCelda(d.getDescripcionItem() != null ? d.getDescripcionItem() : ""));
            tabla.addCell(crearCeldaCentrada(String.valueOf(d.getCantidad())));
            tabla.addCell(crearCelda(d.getUso() != null ? d.getUso() : ""));
        }

        documento.add(tabla);

        documento.add(new Paragraph("\n\n\n"));

        Table firmas = new Table(new float[]{1, 1});
        firmas.setWidth(UnitValue.createPercentValue(100));
        firmas.addCell(new Cell().add(new Paragraph("___________________________\nEntrega\n(Encargado de almacén)")
                .setTextAlignment(TextAlignment.CENTER)).setBorder(null));
        firmas.addCell(new Cell().add(new Paragraph("___________________________\nRecibe\n(Empleado solicitante)")
                .setTextAlignment(TextAlignment.CENTER)).setBorder(null));

        documento.add(firmas);
        documento.close();
    }

    private static Table crearTablaInventario() {
        Table tabla = new Table(new float[]{2, 7, 2, 2, 2});
        tabla.setWidth(UnitValue.createPercentValue(100));

        tabla.addHeaderCell(crearHeaderTabla("Código"));
        tabla.addHeaderCell(crearHeaderTabla("Descripción"));
        tabla.addHeaderCell(crearHeaderTabla("Existencias"));
        tabla.addHeaderCell(crearHeaderTabla("Stock mínimo"));
        tabla.addHeaderCell(crearHeaderTabla("Stock máximo"));

        return tabla;
    }

    private static Cell crearCeldaCentrada(String texto) {
        return new Cell()
                .add(new Paragraph(texto == null ? "" : texto))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static String obtenerTextoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private static String obtenerTextoFiltroStock(String filtroStock) {
        if (filtroStock == null || filtroStock.isBlank()) {
            return "Mostrar Todos";
        }

        return filtroStock;
    }

    private static void agregarDatosSolicitud(Document documento, Solicitud solicitud) {
        documento.add(new Paragraph("Solicitud #" + solicitud.getNoSolicitud()).setBold().setMarginBottom(2));
        documento.add(new Paragraph("Solicitante: " + solicitud.getNombreCompleto()).setMarginBottom(2));
        documento.add(new Paragraph("Fecha: " + FORMATO_FECHA.format(solicitud.getFechaSolicitud())).setMarginBottom(8));
    }

    private static Table crearTablaEgresos() {
        Table tabla = new Table(new float[]{6, 2, 4});
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell(crearHeaderTabla("Descripción"));
        tabla.addHeaderCell(crearHeaderTabla("Cantidad"));
        tabla.addHeaderCell(crearHeaderTabla("Uso / Destino"));
        return tabla;
    }

    private static void agregarLogoYTitulo(Document documento, String titulo) {
        try {
            Image logo = new Image(ImageDataFactory.create(
                    "src/main/resources/imagenes/logo-financiera.png"));
            logo.setWidth(120);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            documento.add(logo);
        } catch (Exception ignored) {}

        documento.add(new Paragraph(titulo).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginTop(10)
                .setMarginBottom(20));
    }

    private static Cell crearHeaderTabla(String texto) {
        return new Cell().add(new Paragraph(texto).setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(COLOR_ENCABEZADO)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell crearCelda(String texto) {
        return new Cell().add(new Paragraph(texto)).setTextAlignment(TextAlignment.LEFT);
    }

    private static void agregarPeriodo(Document documento, LocalDate fechaInicio,LocalDate fechaFinal) {
        documento.add(new Paragraph("Fecha de generación: " + FORMATO_FECHA.format(new Date())).setMarginBottom(5));

        String periodo;
        if (fechaInicio == null || fechaFinal == null) {
            periodo = "Todas las fechas";
        } else {
            periodo = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))+ " al "+ fechaFinal.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        documento.add(new Paragraph("Periodo: " + periodo).setMarginBottom(10));
    }

    private static void agregarTituloPartida(Document documento,Integer codigo, String descripcion) {
        documento.add(new Paragraph("PARTIDA " + " - " + descripcion.toUpperCase()).setBold().setFontSize(14)
                .setFontColor(COLOR_ENCABEZADO).setMarginTop(8).setMarginBottom(5));
    }

    private static void agregarDatosFactura(Document documento,Factura factura) {
        documento.add(new Paragraph("Factura: " + factura.getFolio()).setBold().setMarginBottom(2));
        documento.add(new Paragraph("Proveedor: "+ factura.getRazonSocial()).setMarginBottom(2));
        documento.add(new Paragraph("Fecha: "+ FORMATO_FECHA.format(factura.getFecha())).setMarginBottom(8));
    }

    private static Table crearTablaDetalles() {

        Table tabla =new Table(new float[]{6, 2, 2, 2});
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell(crearHeaderTabla("Descripción"));
        tabla.addHeaderCell(crearHeaderTabla("Cantidad"));
        tabla.addHeaderCell(crearHeaderTabla("Costo Unitario"));
        tabla.addHeaderCell(crearHeaderTabla("Importe"));
        return tabla;
    }
}
