package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CatalogoSucursalesController implements Initializable {

    @FXML private TableView<Sucursal> tv_listado;
    @FXML private TableColumn<Sucursal, Integer> col_noSucursal;
    @FXML private TableColumn<Sucursal, String>  col_nombre;
    @FXML private TableColumn<Sucursal, String>  col_direccion;
    @FXML private TableColumn<Sucursal, String>  col_telefono;
    @FXML private TextField txt_nombreSucursal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    private void configurarTabla() {
        col_noSucursal.setCellValueFactory(new PropertyValueFactory<>("noSucursal"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
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
