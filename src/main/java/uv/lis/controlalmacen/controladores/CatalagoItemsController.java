package uv.lis.controlalmacen.controladores;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

public class CatalagoItemsController implements Initializable {
    @FXML
    private TableView<?> tv_listado;
    @FXML
    private ComboBox<?> cbPartidaPresupuestal;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableColumn<?, ?> col_idItem;
    @FXML
    private TableColumn<?, ?> col_descripcion;
    @FXML
    private TableColumn<?, ?> col_partida;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    private void clicBuscar(ActionEvent event) {

    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItem");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Registro Item");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicModificar(ActionEvent event) {
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
    }
}
