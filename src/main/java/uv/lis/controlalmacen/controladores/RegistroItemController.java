package uv.lis.controlalmacen.controladores;

import java.io.IOException;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemDAO;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

public class RegistroItemController implements Initializable {

    @FXML
    private TextField txt_descripcion;
    @FXML
    private ComboBox<PartidaPresupuestal> cb_partidaPresupuestal;
    
    private ObservableList<PartidaPresupuestal> partidasPresupuestales;
    
    ItemDAO itemDAO = new ItemDAO();
    PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarInformacionPartidasPresupuestales();
    }


        
    private void cargarInformacionPartidasPresupuestales(){
        try{
            partidasPresupuestales = FXCollections.observableArrayList();
            List<PartidaPresupuestal> partidasPresupuestalesDB = partidaPresupuestalDAO.buscarTodos();
            partidasPresupuestales.addAll(partidasPresupuestalesDB);
            cb_partidaPresupuestal.setItems(partidasPresupuestales);
        }catch(SQLException ex){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", 
                                            ex.getMessage(), 
                                            Alert.AlertType.ERROR);
        }catch(NullPointerException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar", 
                            "Lo sentimos, las partidas presupuestales "
                            + "no pueden ser cargada en este momento,"
                            + "porfavor inténtelo más tade", 
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        Item item = new Item();
        item.setDescripcionItem(txt_descripcion.getText());
        configurarSeleccionPartidaPresupuestal(item);
        
        try{
            if(itemDAO.registrar(item)){
            UtilidadesFX.mostrarAlertaSimple("Registro existoso", 
                                            "El item se ha registrado en"
                                            + " el catálogo correctamente", 
                                            Alert.AlertType.INFORMATION);
            }
        }catch(SQLException ex){
            UtilidadesFX.mostrarAlertaSimple("Error al registrar", 
                                            ex.getMessage(), 
                                            Alert.AlertType.ERROR);
        }catch(NullPointerException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar", 
                            "Lo sentimos, las partidas presupuestales "
                            + "no pueden ser cargada en este momento,"
                            + "porfavor inténtelo más tade", 
                    Alert.AlertType.WARNING);
        }
        
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
            Parent vista = UtilidadesFX.cargarFXML("MenuPrincipalCentral");
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
            Parent vista = UtilidadesFX.cargarFXML("RegistroFactura");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoFacturas");
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
            Parent vista = UtilidadesFX.cargarFXML("RegistroItem");
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
            Parent vista = UtilidadesFX.cargarFXML("BitacoraPedidos");
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
