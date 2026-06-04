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
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GestorEmpleadosController implements Initializable {

    @FXML private TableView<Empleado> tv_listado;
    @FXML private TableColumn<Empleado, Integer> col_noEmpleado;
    @FXML private TableColumn<Empleado, String>  col_nombre;
    @FXML private TableColumn<Empleado, String>  col_sucursal;
    @FXML private TableColumn<Empleado, String>  col_puesto;
    @FXML private TableColumn<Empleado, String>  col_correo;
    @FXML private ComboBox<?> cbSucursal;
    @FXML private TextField txtBuscar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    private void configurarTabla() {
        col_noEmpleado.setCellValueFactory(new PropertyValueFactory<>("noEmpleado"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        // col_sucursal y col_puesto requieren cellFactory personalizado (son objetos anidados)
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        // TODO: filtrar tv_listado por txtBuscar y cbSucursal
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroEmpleado");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registrar Empleado");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        // TODO
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        // TODO
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(Sesion.getUsuarioActual().getRol());
            FXMLLoader loader = UtilidadesFX.cargarFXML(rutaMenu);
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Scene escena = new Scene(vista);
            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
