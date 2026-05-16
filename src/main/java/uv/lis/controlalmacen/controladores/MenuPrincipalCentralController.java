package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPrincipalCentralController implements Initializable {
    @FXML
    public Label lbNombreEmpleado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void btnCerrarSesion(ActionEvent actionEvent) {
        Parent vista = null;
        try {
            vista = FXMLLoader.load(getClass().getResource("/fxml/InicioSesion.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene escena = new Scene(vista);

        // Stage escenario = (Stage) tf_personal.getScene().getWindow();
        Stage stage = (Stage) lbNombreEmpleado.getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle("Menu principal");
        stage.show();
        stage.setResizable(false);
        stage.centerOnScreen();
    }
}