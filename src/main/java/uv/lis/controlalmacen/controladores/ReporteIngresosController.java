package uv.lis.controlalmacen.controladores;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

public class ReporteIngresosController implements Initializable {
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Button btnGenerarReporte;
    @FXML private Button btnRegresar;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
