package uv.lis.controlalmacen.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KardexController implements Initializable {

    @FXML
    private TextField txt_idItem;
    @FXML
    private TableView<?> tv_kardex;
    @FXML
    private TableColumn<?, ?> col_folio;
    @FXML
    private TableColumn<?, ?> col_fechaFactura;
    @FXML
    private TableColumn<?, ?> col_costoUnitario;
    @FXML
    private TableColumn<?, ?> col_costoPromedio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
    }

    @FXML
    private void clicBuscarKardex(ActionEvent event) {
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) tv_kardex.getScene().getWindow()).close();
    }
}