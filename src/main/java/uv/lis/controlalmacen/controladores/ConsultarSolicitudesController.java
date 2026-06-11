package uv.lis.controlalmacen.controladores;

import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dao.SolicitudDAO;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Solicitud;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.ExportadorPDF;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultarSolicitudesController implements Initializable {

    @FXML private ComboBox<String> cb_partida;
    @FXML private DatePicker dp_fechaInicial;
    @FXML private DatePicker dp_fechaFinal;
    @FXML private TableView<Solicitud> tv_solicitudes;
    @FXML private TextField txt_buscar;
    @FXML private TableColumn<Solicitud, Integer> col_noSolicitud;
    @FXML private TableColumn<Solicitud, String> col_fecha;
    @FXML private TableColumn<Solicitud, String> col_empleado;
    @FXML private TableColumn<Solicitud, String> col_departamento;

    private ObservableList<Solicitud> solicitudes;
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final PartidaPresupuestalDAO partidaDAO = new PartidaPresupuestalDAO();
    private final ObservableList<String> partidas = FXCollections.observableArrayList();
    private FilteredList<String> partidasFiltradas;
    private boolean seleccionandoPartida = false;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarComboPartidas();
        cargarPartidas();
        cargarSolicitudes();
        dp_fechaFinal.setDisable(true);
        configurarSeleccionFecha();
    }

    private void configurarTabla() {
        col_noSolicitud.setCellValueFactory(new PropertyValueFactory<>("noSolicitud"));
        col_fecha.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaSolicitud();
            return new SimpleStringProperty(fecha != null ? SDF.format(fecha) : "");
        });
        col_empleado.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        col_departamento.setCellValueFactory(new PropertyValueFactory<>("descripcionDepto"));
        tv_solicitudes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void configurarComboPartidas() {
        cb_partida.setEditable(true);
        partidasFiltradas = new FilteredList<>(partidas, p -> true);
        cb_partida.setItems(partidasFiltradas);

        cb_partida.valueProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo == null) return;
            seleccionandoPartida = true;
            Platform.runLater(() -> {
                cb_partida.getEditor().setText(nuevo);
                cb_partida.getEditor().positionCaret(nuevo.length());
                partidasFiltradas.setPredicate(p -> true);
                aplicarFiltros();
                seleccionandoPartida = false;
            });
        });

        cb_partida.getEditor().textProperty().addListener((obs, anterior, nuevo) -> {
            if (seleccionandoPartida) return;
            String texto = nuevo == null ? "" : nuevo.trim().toLowerCase();
            partidasFiltradas.setPredicate(p -> texto.isEmpty() || p.toLowerCase().contains(texto));
            if (cb_partida.isFocused() && !partidasFiltradas.isEmpty()) {
                Platform.runLater(() -> { if (!cb_partida.isShowing()) cb_partida.show(); });
            }
        });
    }

    private void configurarSeleccionFecha() {
        dp_fechaInicial.valueProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo == null) {
                dp_fechaFinal.setDisable(true);
                dp_fechaFinal.setValue(null);
                return;
            }
            dp_fechaFinal.setDisable(false);
            if (dp_fechaFinal.getValue() != null && dp_fechaFinal.getValue().isBefore(nuevo)) {
                dp_fechaFinal.setValue(null);
            }
            dp_fechaFinal.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(empty || item.isBefore(nuevo));
                }
            });
            if (dp_fechaFinal.getValue() != null) aplicarFiltros();
        });
        dp_fechaFinal.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
    }

    private void cargarPartidas() {
        try {
            List<PartidaPresupuestal> lista = partidaDAO.buscarTodos();
            partidas.clear();
            for (PartidaPresupuestal p : lista) {
                partidas.add(p.getDescripcionPartida());
            }
        } catch (Exception ex) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar las partidas presupuestales.", Alert.AlertType.WARNING);
        }
    }

    private void cargarSolicitudes() {
        try {
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            List<Solicitud> lista = solicitudDAO.buscarTodas(noSucursal);
            solicitudes = FXCollections.observableArrayList(lista);
            tv_solicitudes.setItems(solicitudes);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar", Constantes.MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    private void aplicarFiltros() {
        try {
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            String partida = cb_partida.getEditor().getText();
            boolean hayPartida = partida != null && !partida.isBlank();
            boolean hayFecha = dp_fechaInicial.getValue() != null && dp_fechaFinal.getValue() != null;

            List<Solicitud> lista;
            if (hayPartida && hayFecha) {
                lista = solicitudDAO.buscarPorPartidaYFecha(partida,
                        dp_fechaInicial.getValue(), dp_fechaFinal.getValue(), noSucursal);
            } else if (hayPartida) {
                lista = solicitudDAO.buscarPorPartida(partida, noSucursal);
            } else if (hayFecha) {
                lista = solicitudDAO.buscarPorFecha(
                        dp_fechaInicial.getValue(), dp_fechaFinal.getValue(), noSucursal);
            } else {
                lista = solicitudDAO.buscarTodas(noSucursal);
            }

            solicitudes = FXCollections.observableArrayList(lista);
            tv_solicitudes.setItems(solicitudes);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar", Constantes.MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clicVerTodos(ActionEvent event) {
        dp_fechaInicial.setValue(null);
        dp_fechaFinal.setValue(null);
        cb_partida.setValue(null);
        cb_partida.getEditor().clear();
        txt_buscar.clear();
        cargarSolicitudes();
    }

    @FXML
    public void clicBuscarPorNumero(ActionEvent event) {
        String texto = txt_buscar.getText().trim();
        if (texto.isEmpty()) return;
        try {
            int numero = Integer.parseInt(texto);
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            List<Solicitud> todas = solicitudDAO.buscarTodas(noSucursal);
            List<Solicitud> resultado = new ArrayList<>();
            for (Solicitud s : todas) {
                if (s.getNoSolicitud() == numero) resultado.add(s);
            }
            solicitudes = FXCollections.observableArrayList(resultado);
            tv_solicitudes.setItems(solicitudes);
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Número inválido",
                    "Ingresa un número de solicitud válido.", Alert.AlertType.WARNING);
        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar", Constantes.MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clicGenerarReporte(ActionEvent event) {
        List<Solicitud> enTabla = new ArrayList<>(tv_solicitudes.getItems());
        if (enTabla.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin datos",
                    "No hay solicitudes que exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de egresos");
        fileChooser.setInitialFileName("reporte-egresos.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        Stage stage = (Stage) txt_buscar.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            List<Solicitud> conDetalles = new ArrayList<>();
            for (Solicitud s : enTabla) {
                List<DetallesSolicitud> detalles = solicitudDAO.buscarDetalleConPartida(s.getNoSolicitud());
                s.setDetallesSolicitud(detalles);
                conDetalles.add(s);
            }

            String partida = null;
            String textoPartida = cb_partida.getEditor().getText();
            if (textoPartida != null && !textoPartida.isBlank()) {
                partida = textoPartida;
            }

            ExportadorPDF.generarReporteEgresos(
                    archivo.getAbsolutePath(), conDetalles,
                    dp_fechaInicial.getValue(), dp_fechaFinal.getValue(), partida);

            UtilidadesFX.mostrarAlertaSimple("Exportación exitosa",
                    "Reporte guardado en:\n" + archivo.getAbsolutePath(), Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (FileNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al generar",
                    "No se pudo crear el archivo PDF.", Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error de conexión", Constantes.MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clicRegresar(ActionEvent event) {
        try {
            String fxmlMenu = CargadorEscenas.cargarEscenarSegunRol(Sesion.getUsuarioActual().getRol());
            FXMLLoader loader = UtilidadesFX.cargarFXML(fxmlMenu);
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
