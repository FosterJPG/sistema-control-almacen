package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RegistroFacturaController implements Initializable {

    @FXML
    private TextField txt_folio;
    @FXML
    private DatePicker dp_fecha;
    @FXML
    private TextField txt_razonSocial;
    @FXML
    private TextField txt_domicilio;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_rfc;
    @FXML
    private TextField txt_cantidad;
    @FXML
    private TextField txt_costoUnitario;
    @FXML
    private TextField txt_idItem;
    @FXML
    private TableView<?> tv_detalleFactura;
    @FXML
    private TableColumn<?, ?> col_idItem;
    @FXML
    private TableColumn<?, ?> col_descripcion;
    @FXML
    private TableColumn<?, ?> col_cantidad;
    @FXML
    private TableColumn<?, ?> col_costoUnitario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void clicCancelar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista =  loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
            stage.setTitle("Menu principal");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarFacturas(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoFacturas");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
            stage.setTitle("Listado de facturas");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicRegistrarItem(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItemSucursal");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
            stage.setTitle("Registro de items para la sucursal");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicConsultarItems(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoItems");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
            stage.setTitle("Listado de items almacenados");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarBitacora(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("BitacoraPedidos");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
            stage.setTitle("Bitacora de pedidos");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void clicEliminarItem(ActionEvent event) {
    }

    @FXML
    private void clicAgregarItem(ActionEvent event) {
    }

    @FXML
    private void clicGuardarFactura(ActionEvent event) {
    }
}
