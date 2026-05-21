package uv.lis.controlalmacen.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuPrincipalSalidasController implements Initializable {

    @FXML
    private Button btnCrearSolicitud;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnCrearSolicitud.setOnAction(this::abrirRegistroSolicitud);
    }

    private void abrirRegistroSolicitud(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uv/lis/controlalmacen/vistas/RegistrarSolicitud.fxml"));
            Parent root = loader.load();

            Scene nuevaEscena = new Scene(root);

            Stage ventanaActual = (Stage) btnCrearSolicitud.getScene().getWindow();

            ventanaActual.setScene(nuevaEscena);
            ventanaActual.setTitle("Registrar Nueva Solicitud - Sistema de Almacén");
            ventanaActual.centerOnScreen();
            ventanaActual.show();

        } catch (IOException ex) {
            System.err.println("Error al cargar la pantalla RegistrarSolicitud.fxml: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}