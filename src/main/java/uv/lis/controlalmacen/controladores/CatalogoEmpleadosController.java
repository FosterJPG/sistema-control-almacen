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
import uv.lis.controlalmacen.modelo.dao.EmpleadoDAO;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoEmpleadosController implements Initializable {

    @FXML private TableView<Empleado> tv_listado;
    @FXML private TableColumn<Empleado, Integer> col_noEmpleado;
    @FXML private TableColumn<Empleado, String> col_nombre;
    @FXML private TableColumn<Empleado, String> col_sucursal;
    @FXML private TableColumn<Empleado, String> col_puesto;
    @FXML private TableColumn<Empleado, String> col_correo;
    @FXML private TextField txtBuscar;

    private final ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();
    private FilteredList<Empleado> listaFiltrada;
    private final EmpleadoDAO dao = new EmpleadoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarEmpleados();
    }

    private void configurarTabla() {
        col_noEmpleado.setCellValueFactory(new PropertyValueFactory<>("noEmpleado"));
        col_nombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        col_sucursal.setCellValueFactory(cellData -> {
            Empleado e = cellData.getValue();
            if (e.getDepartamento() != null && e.getDepartamento().getSucursal() != null) {
                return new SimpleStringProperty(e.getDepartamento().getSucursal().getNombre());
            }
            return new SimpleStringProperty("Sin asignar");
        });
        col_puesto.setCellValueFactory(cellData -> {
            Empleado e = cellData.getValue();
            return new SimpleStringProperty(e.getPuesto() != null ? e.getPuesto().getPuesto() : "Sin asignar");
        });
        tv_listado.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        listaFiltrada = new FilteredList<>(listaEmpleados, e -> true);
        tv_listado.setItems(listaFiltrada);
        txtBuscar.textProperty().addListener((obs, old, val) -> filtrar(val));
    }

    private void cargarEmpleados() {
        try {
            List<Empleado> empleados = dao.buscarTodos();
            listaEmpleados.setAll(empleados);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar los empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filtrar(String texto) {
        listaFiltrada.setPredicate(e -> {
            if (texto == null || texto.isBlank()) return true;
            String lower = texto.toLowerCase();
            return e.getNombreCompleto().toLowerCase().contains(lower)
                    || String.valueOf(e.getNoEmpleado()).contains(lower);
        });
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        filtrar(txtBuscar.getText());
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroEmpleado");
            Parent vista = loader.load();
            Stage modal = new Stage();
            modal.setTitle("Registrar Empleado");
            modal.setResizable(false);
            modal.setScene(new Scene(vista));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(tv_listado.getScene().getWindow());
            modal.centerOnScreen();
            modal.showAndWait();
            cargarEmpleados();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        UtilidadesFX.mostrarAlertaSimple("No disponible",
                "La modificación de empleados no está habilitada en esta versión.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Empleado seleccionado = tv_listado.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona un empleado de la tabla para eliminarlo.", Alert.AlertType.WARNING);
            return;
        }
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion("Eliminar empleado",
                "¿Eliminar a " + seleccionado.getNombreCompleto() + "?\nEsta acción no se puede deshacer.");
        if (!confirmar) return;
        try {
            dao.eliminar(seleccionado);
            listaEmpleados.remove(seleccionado);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    "No se pudo eliminar al empleado:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalCentral");
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(new Scene(vista));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
