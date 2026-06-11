package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.SolicitudDAO;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.Solicitud;
import uv.lis.controlalmacen.utilidades.ExportadorPDF;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class AceptarSolicitudModalController implements Initializable {

    @FXML private Label lblFolioSolicitud;
    @FXML private Label lblNombreEmpleado;
    @FXML private Label lblFecha;
    @FXML private Label lblNoEmpleado;

    @FXML private TableView<DetallesSolicitud> tvDetallesSolicitud;
    @FXML private TableColumn<DetallesSolicitud, String> colIdItem;
    @FXML private TableColumn<DetallesSolicitud, String> colDescripcion;
    @FXML private TableColumn<DetallesSolicitud, Integer> colStockActual;
    @FXML private TableColumn<DetallesSolicitud, Integer> colCantSolicitada;
    @FXML private TableColumn<DetallesSolicitud, Integer> colCantEntregar;
    @FXML private TableColumn<DetallesSolicitud, String> colUso;

    private Solicitud solicitud;
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final ObservableList<DetallesSolicitud> listaDetalles = FXCollections.observableArrayList();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIdItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionItem"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        colCantSolicitada.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantEntregar.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colUso.setCellValueFactory(new PropertyValueFactory<>("uso"));
        tvDetallesSolicitud.setItems(listaDetalles);
    }

    public void cargarSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;

        lblFolioSolicitud.setText("#" + solicitud.getNoSolicitud());
        lblNombreEmpleado.setText(solicitud.getNombreCompleto());
        lblNoEmpleado.setText(String.valueOf(solicitud.getNoEmpleado()));
        lblFecha.setText(solicitud.getFechaSolicitud() != null
                ? SDF.format(solicitud.getFechaSolicitud()) : "");

        try {
            List<DetallesSolicitud> detalles = solicitudDAO.buscarDetalle(
                    solicitud.getNoSolicitud(), solicitud.getNoSucursal());
            listaDetalles.setAll(detalles);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar los detalles: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicAceptar(ActionEvent event) {
        if (solicitud == null) return;

        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar entrega",
                "¿Confirmas la entrega de los materiales de la solicitud #"
                        + solicitud.getNoSolicitud() + "?\nEsta accion no se puede deshacer.");
        if (!confirmar) return;

        try {
            solicitudDAO.aprobar(solicitud.getNoSolicitud());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar formato de entrega-recepción");
            fileChooser.setInitialFileName("entrega-recepcion-" + solicitud.getNoSolicitud() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            File archivo = fileChooser.showSaveDialog(tvDetallesSolicitud.getScene().getWindow());

            if (archivo != null) {
                ExportadorPDF.generarFormatoEntregaRecepcion(
                        archivo.getAbsolutePath(), solicitud, listaDetalles);
                UtilidadesFX.mostrarAlertaSimple("Entrega registrada",
                        "Solicitud #" + solicitud.getNoSolicitud()
                                + " aprobada. Formato guardado en:\n" + archivo.getAbsolutePath(),
                        Alert.AlertType.INFORMATION);
            } else {
                UtilidadesFX.mostrarAlertaSimple("Entrega registrada",
                        "La solicitud #" + solicitud.getNoSolicitud()
                                + " fue aprobada y las existencias fueron actualizadas.",
                        Alert.AlertType.INFORMATION);
            }

            cerrarModal();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al aprobar",
                    "No se pudo registrar la entrega:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicRechazar(ActionEvent event) {
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion(
                "Rechazar solicitud",
                "¿Confirmas que NO entregaras los materiales de la solicitud #"
                        + solicitud.getNoSolicitud() + "?\nLa solicitud quedara pendiente.");
        if (confirmar) cerrarModal();
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarModal();
    }

    private void cerrarModal() {
        ((Stage) tvDetallesSolicitud.getScene().getWindow()).close();
    }
}
