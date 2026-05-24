package uv.lis.controlalmacen.controladores;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

public class RegistroItemController implements Initializable {

    @FXML
    private TextField txt_descripcion;
    @FXML
    private ComboBox<PartidaPresupuestal> cb_partidaPresupuestal;
    
    private ObservableList<PartidaPresupuestal> partidasPresupuestales;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    private void clicRegistrar(ActionEvent event) {
        Item item = new Item();
        item.setDescripcionItem(txt_descripcion.getText());

        //obtener datos de la vista: Descripcion y Partida Presupuestal
        // enviar al ItemDAO
        // devuelve true/false
        //if(ItemDao.registrar(item)){
            
        //}
        
    }
    
    
    private void configurarSeleccionPartidaPresupuestal(Item item){
        cb_partidaPresupuestal.valueProperty().addListener(new ChangeListener<PartidaPresupuestal>(){
            @Override
            public void changed(ObservableValue<? extends PartidaPresupuestal> observable, PartidaPresupuestal oldValue, PartidaPresupuestal newValue) {
               if(newValue != null){
                   item.setCodigoPartidaPresupuestal(newValue.getCodigo());
               } 
            } 
        });
    }
    
    
        
    //MÉTODOS DE NAVEGABILIDAD
        
    @FXML
    private void clicCancelar(ActionEvent event) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);
            
            Stage stage = (Stage) txt_descripcion.getScene().getWindow();
            stage.setTitle("Menu Principal");
            stage.centerOnScreen();
            
            stage.setScene(escena);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }    
    
    @FXML
    public void clicRegistrarFactura(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroFactura");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_descripcion.getScene().getWindow();
            stage.setTitle("Registrar Factura");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            stage.setScene(escena);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarFacturas(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoFacturas");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_descripcion.getScene().getWindow();
            stage.setTitle("Consultar Factura");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            stage.setScene(escena);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicRegistrarItem(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItem");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_descripcion.getScene().getWindow();
            stage.setTitle("Registrar Item");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            stage.setScene(escena);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarItems(ActionEvent actionEvent) {
        //
    }

    @FXML
    public void clicConsultarBitacora(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("BitacoraPedidos");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_descripcion.getScene().getWindow();
            stage.setTitle("Consultar Bitacora");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            stage.setScene(escena);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
