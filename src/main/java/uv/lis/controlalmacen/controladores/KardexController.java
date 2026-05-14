package uv.lis.controlalmacen.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

public class KardexController implements Initializable {

    @FXML
    private TableView<?> tvKardex; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tvKardex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
    }
}