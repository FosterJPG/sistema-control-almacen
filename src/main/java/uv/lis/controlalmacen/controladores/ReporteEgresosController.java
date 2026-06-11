package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.SolicitudDAO;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Solicitud;
import uv.lis.controlalmacen.utilidades.ExportadorPDF;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReporteEgresosController implements Initializable {

    @FXML private DatePicker dp_inicio;
    @FXML private DatePicker dp_fin;

    private final SolicitudDAO solicitudDAO = new SolicitudDAO();

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
        fileChooser.setTitle("Guardar reporte de egresos");
        fileChooser.setInitialFileName("reporte-egresos.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        Stage stage = (Stage) dp_inicio.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();

            List<Solicitud> solicitudes = solicitudDAO.buscarPorFecha(
                    dp_inicio.getValue(), dp_fin.getValue(), noSucursal);

            if (solicitudes.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Sin datos",
                        "No hay solicitudes en el periodo seleccionado.", Alert.AlertType.WARNING);
                return;
            }

            List<Solicitud> conDetalles = new ArrayList<>();
            for (Solicitud s : solicitudes) {
                List<DetallesSolicitud> detalles = solicitudDAO.buscarDetalleConPartida(s.getNoSolicitud());
                s.setDetallesSolicitud(detalles);
                conDetalles.add(s);
            }

            ExportadorPDF.generarReporteEgresos(archivo.getAbsolutePath(), conDetalles,
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
