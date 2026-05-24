package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistroFacturaController implements Initializable {

    @FXML
    private TextField txt_folio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void clicCancelar(ActionEvent actionEvent) {
        try {
            Parent vista = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoFacturas");
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
            Parent vista = UtilidadesFX.cargarFXML("RegistroItem");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoItems");
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
            Parent vista = UtilidadesFX.cargarFXML("BitacoraPedidos");
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
}
