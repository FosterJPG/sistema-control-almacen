package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class InicioSesionController {

    @FXML
    private Button btnIngresar;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MenuPrincipalCentral.fxml"));
            
            Scene nuevaEscena = new Scene(root);
            
            Stage ventanaActual = (Stage) btnIngresar.getScene().getWindow();
            
            ventanaActual.setScene(nuevaEscena);
            ventanaActual.show(); // Refrescar la ventana

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la pantalla: " + e.getMessage());
        }
    }
}