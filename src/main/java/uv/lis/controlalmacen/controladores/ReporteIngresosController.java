package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.FacturaDAO;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.utilidades.ExportadorPDF;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ReporteIngresosController implements Initializable {

    @FXML private DatePicker dp_inicio;
    @FXML private DatePicker dp_fin;

    private final FacturaDAO facturaDAO = new FacturaDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) dp_inicio.getScene().getWindow()).close();
    }

    @FXML
    private void clicGenerarReporte(ActionEvent event) {
        if (dp_inicio.getValue() == null || dp_fin.getValue() == null) {
            UtilidadesFX.mostrarAlertaSimple("Fechas requeridas",
                    "Selecciona el periodo de inicio y fin.", Alert.AlertType.WARNING);
            return;
        }
        if (dp_fin.getValue().isBefore(dp_inicio.getValue())) {
            UtilidadesFX.mostrarAlertaSimple("Rango inválido",
                    "La fecha final no puede ser anterior a la fecha inicial.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de ingresos");
        fileChooser.setInitialFileName("reporte-ingresos.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        Stage stage = (Stage) dp_inicio.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            List<Factura> facturas = facturaDAO.buscarPorFecha(dp_inicio.getValue(), dp_fin.getValue());

            if (facturas.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Sin datos",
                        "No hay facturas en el periodo seleccionado.", Alert.AlertType.WARNING);
                return;
            }

            ExportadorPDF.generarReporteIngresos(archivo.getAbsolutePath(), facturas,
                    dp_inicio.getValue(), dp_fin.getValue(), null);

            UtilidadesFX.mostrarAlertaSimple("Exportación exitosa",
                    "Reporte guardado en:\n" + archivo.getAbsolutePath(), Alert.AlertType.INFORMATION);
            stage.close();

        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (FileNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al generar",
                    "No se pudo crear el archivo PDF.", Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error de conexión",
                    "No se pudo conectar a la base de datos.", Alert.AlertType.ERROR);
        }
    }
}
