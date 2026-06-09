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
import uv.lis.controlalmacen.modelo.dto.DetallesFactura;
import uv.lis.controlalmacen.modelo.dto.Factura;

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

    private static TreeMap<Integer, Map<String, List<DetalleAgrupado>>> agruparFacturasPorPartida(List<Factura> facturas) {

        TreeMap<Integer, Map<String, List<DetalleAgrupado>>> partidas = new TreeMap<>();

        for (Factura factura : facturas) {
            for (DetallesFactura detalle : factura.getDetallesFactura()) {

                Integer codigoPartida = detalle.getCodigoPartida();
                partidas.putIfAbsent( codigoPartida, new LinkedHashMap<>());
                Map<String, List<DetalleAgrupado>> facturasPartida = partidas.get(codigoPartida);

                facturasPartida.putIfAbsent(factura.getFolio(), new ArrayList<>());
                facturasPartida.get(factura.getFolio()).add(new DetalleAgrupado(factura,detalle));
            }
        }
        return partidas;
    }

    private static void imprimirAgrupacion(TreeMap<Integer, Map<String, List<DetalleAgrupado>>> partidas) {

        for (Integer codigo : partidas.keySet()) {
            System.out.println("\nPARTIDA " + codigo);

            Map<String, List<DetalleAgrupado>> facturas = partidas.get(codigo);

            for (String folio : facturas.keySet()) {
                System.out.println("  Factura: " + folio);

                for (DetalleAgrupado detalle : facturas.get(folio)) {
                    System.out.println("      " + detalle.getDetalle().getDescripcion());
                }
            }
        }
    }

    public static void generarReporteIngresos(String rutaPdf, List<Factura> facturas, LocalDate fechaInicio, LocalDate fechaFinal)
            throws FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));

        Document documento =new Document(pdf, PageSize.A4.rotate());
        documento.setMargins(40,40,40,40);

        agregarLogoYTitulo(documento, "REPORTE DE INGRESOS");

        agregarPeriodo(documento, fechaInicio, fechaFinal);

        TreeMap<Integer, Map<String, List<DetalleAgrupado>>> partidas = agruparFacturasPorPartida(facturas);

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

    private static void agregarLogoYTitulo(Document documento, String titulo) {
        try {
            Image logo = new Image(ImageDataFactory.create(
                    "src/main/resources/imagenes/logo-financiera.png"));
            logo.setWidth(120);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            documento.add(logo);
        } catch (Exception ignored) {}

        documento.add(new Paragraph(titulo).setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER).setMarginTop(10)
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

        documento.add(new Paragraph("Periodo: " + periodo).setMarginBottom(20));
    }

    private static void agregarTituloPartida(Document documento,Integer codigo, String descripcion) {
        documento.add(new Paragraph("PARTIDA " + codigo + " - " + descripcion.toUpperCase()).setBold().setFontSize(14)
                .setFontColor(COLOR_ENCABEZADO).setMarginTop(15).setMarginBottom(10));
    }

    private static void agregarDatosFactura(Document documento,Factura factura) {
        documento.add(new Paragraph("Factura: " + factura.getFolio()).setBold());
        documento.add(new Paragraph("Proveedor: "+ factura.getRazonSocial()));

        documento.add(new Paragraph("Fecha: "+ FORMATO_FECHA.format(factura.getFecha())).setMarginBottom(10));
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
