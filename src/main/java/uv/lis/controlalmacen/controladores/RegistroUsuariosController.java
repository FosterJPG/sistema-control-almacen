package uv.lis.controlalmacen.controladores;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistroUsuariosController implements Initializable {

    @FXML
    public void pruebaDeConexion() {
        System.out.println("Si ves esto en Scene Builder, están conectados");
    }
    
    @FXML
private void metodoDePrueba() {
    System.out.println("¡Funciona!");
}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}