package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CatalogoPartidasController implements Initializable {

    @FXML private TableView<PartidaPresupuestal> tv_listado;
    @FXML private TableColumn<PartidaPresupuestal, Integer> col_codigo;
    @FXML private TableColumn<PartidaPresupuestal, String>  col_descripcion;
    @FXML private TextField txtBuscar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    private void configurarTabla() {
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionPartida"));
    }

    @FXML private void clicBuscar(ActionEvent event)    { /* TODO */ }
    @FXML private void clicAgregar(ActionEvent event)   { /* TODO */ }
    @FXML private void clicModificar(ActionEvent event) { /* TODO */ }
    @FXML private void clicEliminar(ActionEvent event)  { /* TODO */ }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(Sesion.getUsuarioActual().getRol());
            FXMLLoader loader = UtilidadesFX.cargarFXML(rutaMenu);
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(new Scene(vista));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
