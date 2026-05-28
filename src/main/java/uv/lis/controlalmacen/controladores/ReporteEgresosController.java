package uv.lis.controlalmacen.controladores;

import java.io.IOException;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

public class ReporteEgresosController implements Initializable {

    @FXML
    private DatePicker dp_inicio;
    @FXML
    private DatePicker dp_fin;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) dp_inicio.getScene().getWindow()).close();
    }

    @FXML
    private void clicGenerarReporte(ActionEvent event) {
        
    }
}
