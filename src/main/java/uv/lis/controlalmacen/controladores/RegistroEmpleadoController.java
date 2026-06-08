package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistroEmpleadoController implements Initializable {

    @FXML private TextField txt_noEmpleado;
    @FXML private TextField txt_nombre;
    @FXML private TextField txt_paterno;
    @FXML private TextField txt_materno;
    @FXML private TextField txt_correo;
    @FXML private ComboBox<?> cb_puesto;
    @FXML private ComboBox<?> cb_sucursal;
    @FXML private ComboBox<?> cb_departamento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO: cargar cb_puesto, cb_sucursal, cb_departamento desde BD
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage) txt_nombre.getScene().getWindow()).close();
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        // TODO: validar y guardar empleado
    }
}
