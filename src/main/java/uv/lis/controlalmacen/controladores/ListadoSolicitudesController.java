package uv.lis.controlalmacen.controladores;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class ListadoSolicitudesController implements Initializable {

    @FXML
    private TableView<?> tvSolicitudes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tvSolicitudes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }
}