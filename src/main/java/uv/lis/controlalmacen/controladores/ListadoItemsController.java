package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListadoItemsController implements Initializable {

    @FXML
    private ComboBox<String> cb_filtroStock;

    @FXML
    private ComboBox<String> cb_filtroPartida;

    private ObservableList<String> listaOpcionesStock = FXCollections.observableArrayList(
                                            "Sobre el máximo", "Menor que el mínimo");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cb_filtroStock.setItems(listaOpcionesStock);
    }

    public void clicRegresar(ActionEvent actionEvent) {
        try {
            Parent vista = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Menu principal");
            stage.show();
            stage.setResizable(false);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clicRegistrarFactura(ActionEvent actionEvent) {
        try{
            Parent vista = UtilidadesFX.cargarFXML("RegistroFactura");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Registrar Factura");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void clicConsultarFacturas(ActionEvent actionEvent) {
        try{
            Parent vista = UtilidadesFX.cargarFXML("ListadoFacturas");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Consultar Factura");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void clicRegistrarItem(ActionEvent actionEvent) {
        try{
            Parent vista = UtilidadesFX.cargarFXML("RegistroItem");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Registrar Item");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void clicConsultarItems(ActionEvent actionEvent) {
        //
    }

    public void clicConsultarBitacora(ActionEvent actionEvent) {
        try{
            Parent vista = UtilidadesFX.cargarFXML("BitacoraPedidos");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Consultar Bitacora");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
