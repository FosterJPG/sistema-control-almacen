package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPrincipalCentralController implements Initializable {

    @FXML
    private Label lb_nombreEmpleado;

    @FXML
    private Label lb_nombreSucursal;

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
        Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle("Menu principal");
        stage.show();
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    @FXML
    public void btnRegistrarFactura(ActionEvent actionEvent) {
        try {
            //Parent vista = FXMLLoader.load(getClass().getResource(rutaMenu));
            Parent vista = UtilidadesFX.cargarFXML("RegistroFactura");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Registro de facturas");
            stage.show();
            stage.setResizable(false);
            stage.centerOnScreen();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void btnConsultarFacturas(ActionEvent actionEvent) {
    }
}