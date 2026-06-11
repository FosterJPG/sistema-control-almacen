package uv.lis.controlalmacen.controladores;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.SolicitudDAO;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Solicitud;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ListadoSolicitudesController implements Initializable {

    @FXML private TableView<Solicitud> tvSolicitudes;
    @FXML private TableColumn<Solicitud, Integer> colNoSolicitud;
    @FXML private TableColumn<Solicitud, String> colFecha;
    @FXML private TableColumn<Solicitud, String> colEmpleado;
    @FXML private TableColumn<Solicitud, Integer> colNoEmpleado;

    @FXML private DatePicker dpFechaFiltro;
    @FXML private TextField txtBuscar;

    private final ObservableList<Solicitud> listaSolicitudes = FXCollections.observableArrayList();
    private FilteredList<Solicitud> listaFiltrada;
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarFiltros();
        cargarSolicitudes();
    }

    private void configurarTabla() {
        colNoSolicitud.setCellValueFactory(new PropertyValueFactory<>("noSolicitud"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colNoEmpleado.setCellValueFactory(new PropertyValueFactory<>("noEmpleado"));

        colFecha.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaSolicitud();
            return new SimpleStringProperty(fecha != null ? SDF.format(fecha) : "");
        });

        tvSolicitudes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void configurarFiltros() {
        listaFiltrada = new FilteredList<>(listaSolicitudes, s -> true);
        tvSolicitudes.setItems(listaFiltrada);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        dpFechaFiltro.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        listaFiltrada.setPredicate(s -> {
            String texto = txtBuscar.getText().trim().toLowerCase();
            java.time.LocalDate fechaFiltro = dpFechaFiltro.getValue();

            boolean coincideTexto = texto.isEmpty()
                    || s.getNombreCompleto().toLowerCase().contains(texto)
                    || String.valueOf(s.getNoSolicitud()).contains(texto);

            boolean coincideFecha = true;
            if (fechaFiltro != null && s.getFechaSolicitud() != null) {
                java.sql.Date sqlFecha = java.sql.Date.valueOf(fechaFiltro);
                coincideFecha = s.getFechaSolicitud().equals(sqlFecha);
            }

            return coincideTexto && coincideFecha;
        });
    }

    private void cargarSolicitudes() {
        try {
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            List<Solicitud> pendientes = solicitudDAO.buscarPendientes(noSucursal);
            listaSolicitudes.setAll(pendientes);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar las solicitudes: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicAtender(ActionEvent event) {
        Solicitud seleccionada = tvSolicitudes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin seleccion",
                    "Selecciona una solicitud de la lista para atenderla.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("AceptarSolicitudModal");
            Parent vista = loader.load();
            AceptarSolicitudModalController controller = loader.getController();
            controller.cargarSolicitud(seleccionada);

            Stage ventana = new Stage();
            ventana.setTitle("Atender Solicitud #" + seleccionada.getNoSolicitud());
            ventana.setScene(new Scene(vista));
            ventana.setResizable(false);

            ventana.setOnHidden(e -> {
                cargarSolicitudes();
            });

            ventana.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            String fxmlMenu = CargadorEscenas.cargarEscenarSegunRol(Sesion.getUsuarioActual().getRol());
            FXMLLoader loader = UtilidadesFX.cargarFXML(fxmlMenu);
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Stage stage = (Stage) tvSolicitudes.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicLimpiarFiltros(ActionEvent event) {
        txtBuscar.clear();
        dpFechaFiltro.setValue(null);
    }
}
