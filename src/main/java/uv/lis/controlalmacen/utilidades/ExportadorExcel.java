package uv.lis.controlalmacen.utilidades;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uv.lis.controlalmacen.modelo.dto.ItemPedido;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExportadorExcel {

    private static final String[] ENCABEZADOS_BITACORA = {
        "Código", "Descripción", "Existencias", "Stock Mínimo", "Fecha de registro"
    };

    public static void generarBitacoraPedidos(String rutaArchivo, List<ItemPedido> items)
            throws IOException {

        try (Workbook libro = new XSSFWorkbook();
             FileOutputStream salida = new FileOutputStream(rutaArchivo)) {

            Sheet hoja = libro.createSheet("Bitácora de Pedidos");

            CellStyle estiloEncabezado = crearEstiloEncabezado(libro);
            CellStyle estiloFecha = crearEstiloFecha(libro);

            Row filaEncabezado = hoja.createRow(0);
            for (int i = 0; i < ENCABEZADOS_BITACORA.length; i++) {
                Cell celda = filaEncabezado.createCell(i);
                celda.setCellValue(ENCABEZADOS_BITACORA[i]);
                celda.setCellStyle(estiloEncabezado);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int numeroFila = 1;
            for (ItemPedido item : items) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(item.getIdItem());
                fila.createCell(1).setCellValue(item.getDescripcion());
                fila.createCell(2).setCellValue(item.getExistencias());
                fila.createCell(3).setCellValue(item.getStockMin());

                Cell celdaFecha = fila.createCell(4);
                celdaFecha.setCellValue(item.getFecha() != null ? sdf.format(item.getFecha()) : "");
            }

            for (int i = 0; i < ENCABEZADOS_BITACORA.length; i++) {
                hoja.autoSizeColumn(i);
            }

            libro.write(salida);
        }
    }

    private static CellStyle crearEstiloEncabezado(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        return estilo;
    }

    private static CellStyle crearEstiloFecha(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        CreationHelper helper = libro.getCreationHelper();
        estilo.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy"));
        return estilo;
    }
}
