package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import uv.lis.controlalmacen.modelo.dao.SolicitudDAO;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.Solicitud;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
        colCantEntregar.setCellValueFactory(new PropertyValueFactory<>("cantidadEntregar"));
        colCantEntregar.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCantEntregar.setOnEditCommit(event -> {
            DetallesSolicitud detalle = event.getRowValue();
            Integer nuevaCantidad = event.getNewValue();

            if (nuevaCantidad == null || nuevaCantidad < 0) {
                detalle.setCantidadEntregar(event.getOldValue());
                tvDetallesSolicitud.refresh();
                return;
            }

            if (nuevaCantidad > detalle.getCantidad()) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Cantidad inválida",
                        "La cantidad a entregar no puede ser mayor a la cantidad solicitada.",
                        Alert.AlertType.WARNING
                );
                detalle.setCantidadEntregar(event.getOldValue());
                tvDetallesSolicitud.refresh();
                return;
            }

            if (nuevaCantidad > detalle.getExistencias()) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Stock insuficiente",
                        "La cantidad a entregar no puede ser mayor al stock actual.",
                        Alert.AlertType.WARNING
                );
                detalle.setCantidadEntregar(event.getOldValue());
                tvDetallesSolicitud.refresh();
                return;
            }

            detalle.setCantidadEntregar(nuevaCantidad);
        });

        colUso.setCellValueFactory(new PropertyValueFactory<>("uso"));

        tvDetallesSolicitud.setEditable(true);
        colCantEntregar.setEditable(true);
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

            for (DetallesSolicitud detalle : detalles) {
                int cantidadSolicitada = detalle.getCantidad() != null ? detalle.getCantidad() : 0;
                int existencias = detalle.getExistencias() != null ? detalle.getExistencias() : 0;

                detalle.setCantidadEntregar(Math.min(cantidadSolicitada, existencias));
            }

            listaDetalles.setAll(detalles);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar los detalles: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicAceptar(ActionEvent event) {
        tvDetallesSolicitud.edit(-1, null);
        tvDetallesSolicitud.refresh();

        if (solicitud == null) {
            return;
        }

        if (!validarCantidadesEntrega()) {
            return;
        }

        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar entrega",
                "¿Confirmas la entrega de los materiales de la solicitud #"
                        + solicitud.getNoSolicitud() + "?\nEsta acción no se puede deshacer."
        );

        if (!confirmar) {
            return;
        }

        try {
            solicitudDAO.aprobar(solicitud.getNoSolicitud(), listaDetalles);

            UtilidadesFX.mostrarAlertaSimple(
                    "Entrega registrada",
                    "La solicitud #" + solicitud.getNoSolicitud()
                            + " fue aprobada y las existencias fueron actualizadas.",
                    Alert.AlertType.INFORMATION
            );

            cerrarVentana();

        } catch (SQLException e) {
        UtilidadesFX.mostrarAlertaSimple(
                "No se pudo aprobar",
                e.getMessage(),
                Alert.AlertType.WARNING
        );

        if (e.getMessage().contains("ya fue aprobada")) {
            cerrarVentana();
        }

        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error de conexión",
                    "No se pudo conectar a la base de datos.",
                    Alert.AlertType.ERROR
            );
        }
    }

    private boolean validarCantidadesEntrega() {
        int totalEntregado = 0;

        for (DetallesSolicitud detalle : listaDetalles) {
            Integer cantidadEntregar = detalle.getCantidadEntregar();
            System.out.println("VALIDANDO -> Item: " + detalle.getIdItem()
                    + " | solicitada: " + detalle.getCantidad()
                    + " | entregar: " + detalle.getCantidadEntregar());

            if (cantidadEntregar == null) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Cantidad requerida",
                        "Todos los artículos deben tener una cantidad a entregar.",
                        Alert.AlertType.WARNING
                );
                return false;
            }

            if (cantidadEntregar < 0) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Cantidad inválida",
                        "La cantidad a entregar no puede ser negativa.",
                        Alert.AlertType.WARNING
                );
                return false;
            }

            if (cantidadEntregar > detalle.getCantidad()) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Cantidad inválida",
                        "La cantidad a entregar no puede ser mayor a la cantidad solicitada.",
                        Alert.AlertType.WARNING
                );
                return false;
            }

            if (cantidadEntregar > detalle.getExistencias()) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Stock insuficiente",
                        "La cantidad a entregar no puede ser mayor al stock actual.",
                        Alert.AlertType.WARNING
                );
                return false;
            }

            totalEntregado += cantidadEntregar;
        }

        if (totalEntregado == 0) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Entrega vacía",
                    "Debe entregar al menos una unidad para aceptar la solicitud.",
                    Alert.AlertType.WARNING
            );
            return false;
        }

        return true;
    }


    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) tvDetallesSolicitud.getScene().getWindow()).close();
    }
}
