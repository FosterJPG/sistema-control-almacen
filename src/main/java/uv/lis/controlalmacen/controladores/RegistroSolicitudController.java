package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dao.SolicitudDAO;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroSolicitudController implements Initializable {

    @FXML private DatePicker dp_fecha;
    @FXML private TextField txt_empleado;
    @FXML private TextField txt_sucursal;
    @FXML private TextField txt_idItem;
    @FXML private TextField txt_descripcionItem;
    @FXML private TextField txt_cantidad;
    @FXML private TextArea txt_uso;

    @FXML private TableView<DetallesSolicitud> tv_items;
    @FXML private TableColumn<DetallesSolicitud, String> col_idItem;
    @FXML private TableColumn<DetallesSolicitud, String> col_descripcion;
    @FXML private TableColumn<DetallesSolicitud, Integer> col_cantidad;
    @FXML private TableColumn<DetallesSolicitud, String> col_uso;

    private final ObservableList<DetallesSolicitud> listaItems = FXCollections.observableArrayList();
    private final ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        precargarDatosSesion();
        tv_items.setItems(listaItems);
    }

    private void configurarTabla() {
        col_idItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionItem"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_uso.setCellValueFactory(new PropertyValueFactory<>("uso"));
    }

    private void precargarDatosSesion() {
        Empleado empleado = Sesion.getUsuarioActual().getEmpleado();
        txt_empleado.setText(empleado.getNombre() + " " + empleado.getPaterno());

        if (empleado.getDepartamento() != null && empleado.getDepartamento().getSucursal() != null) {
            txt_sucursal.setText(empleado.getDepartamento().getSucursal().getNombre());
        }

        dp_fecha.setValue(LocalDate.now());
        dp_fecha.setDisable(true);
    }

    @FXML
    private void clicBuscarItem(ActionEvent event) {
        String id = txt_idItem.getText().trim();
        if (id.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campo vacío", "Ingresa el código del ítem.", Alert.AlertType.WARNING);
            return;
        }
        try {
            ItemAlmacenado item = itemAlmacenadoDAO.buscarUno(id);
            if (item.getIdItem() == null) {
                UtilidadesFX.mostrarAlertaSimple("No encontrado",
                        "No existe un ítem con el código \"" + id + "\" en esta sucursal.", Alert.AlertType.WARNING);
                txt_descripcionItem.clear();
            } else {
                txt_descripcionItem.setText(item.getDescripcionItem());
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error de consulta",
                    "No se pudo buscar el ítem: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicAgregarItem(ActionEvent event) {
        String id = txt_idItem.getText().trim();
        String descripcion = txt_descripcionItem.getText().trim();
        String cantidadStr = txt_cantidad.getText().trim();
        String uso = txt_uso.getText().trim();

        if (id.isEmpty() || descripcion.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Datos incompletos",
                    "Busca un ítem válido antes de agregarlo.", Alert.AlertType.WARNING);
            return;
        }
        if (cantidadStr.isEmpty() || uso.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Datos incompletos",
                    "La cantidad y el uso son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Cantidad inválida",
                    "La cantidad debe ser un número entero positivo.", Alert.AlertType.WARNING);
            return;
        }

        // Verificar que el ítem no esté ya en la lista
        boolean duplicado = listaItems.stream().anyMatch(d -> d.getIdItem().equals(id));
        if (duplicado) {
            UtilidadesFX.mostrarAlertaSimple("Ítem duplicado",
                    "El ítem \"" + id + "\" ya está en la lista.", Alert.AlertType.WARNING);
            return;
        }

        DetallesSolicitud detalle = new DetallesSolicitud();
        detalle.setIdItem(id);
        detalle.setDescripcionItem(descripcion);
        detalle.setCantidad(cantidad);
        detalle.setUso(uso);
        listaItems.add(detalle);

        limpiarCamposItem();
    }

    @FXML
    private void clicQuitarItem(ActionEvent event) {
        DetallesSolicitud seleccionado = tv_items.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona un ítem de la tabla para quitarlo.", Alert.AlertType.WARNING);
            return;
        }
        listaItems.remove(seleccionado);
    }

    @FXML
    private void clicGuardarSolicitud(ActionEvent event) {
        if (dp_fecha.getValue() == null) {
            UtilidadesFX.mostrarAlertaSimple("Fecha requerida",
                    "Selecciona la fecha de la solicitud.", Alert.AlertType.WARNING);
            return;
        }
        if (listaItems.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin ítems",
                    "Agrega al menos un ítem antes de enviar la solicitud.", Alert.AlertType.WARNING);
            return;
        }

        Empleado empleado = Sesion.getUsuarioActual().getEmpleado();
        Date fecha = Date.valueOf(dp_fecha.getValue());
        int noEmpleado = empleado.getNoEmpleado();
        int noSucursal = empleado.getDepartamento().getSucursal().getNoSucursal();

        try {
            solicitudDAO.registrar(listaItems, fecha, noEmpleado, noSucursal);
            UtilidadesFX.mostrarAlertaSimple("Solicitud enviada",
                    "La solicitud fue registrada correctamente.", Alert.AlertType.INFORMATION);
            verificarStockMinimo();
            regresarAlMenu();
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al guardar",
                    "No se pudo registrar la solicitud:\n" + e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error de conexión",
                    "No se pudo conectar a la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        regresarAlMenu();
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        regresarAlMenu();
    }

    private void regresarAlMenu() {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalDepartamento");
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_empleado.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verificarStockMinimo() {
        List<String> alertas = new ArrayList<>();
        for (DetallesSolicitud detalle : listaItems) {
            try {
                ItemAlmacenado item = itemAlmacenadoDAO.buscarUno(detalle.getIdItem());
                if (item.getIdItem() != null && detalle.getCantidad() >= item.getExistencias()) {
                    alertas.add("• " + item.getDescripcionItem() + " — existencias actuales: " + item.getExistencias() + " unidades");
                }
            } catch (Exception ignored) {}
        }
        if (!alertas.isEmpty()) {
            UtilidadesFX.mostrarAlertaStockMinimo(alertas);
        }
    }

    private void limpiarCamposItem() {
        txt_idItem.clear();
        txt_descripcionItem.clear();
        txt_cantidad.clear();
        txt_uso.clear();
        txt_idItem.requestFocus();
    }
}
