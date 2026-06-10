/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package uv.lis.controlalmacen.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemDAO;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class RegistroItemSucursalController implements Initializable {

    @FXML
    private TextField txt_descripcion;
    @FXML
    private TextField txt_idItem;
    @FXML
    private TextField txt_stockMinimo;
    @FXML
    private TextField txt_stockMaximo;

    ItemDAO itemDAO = new ItemDAO();
    @FXML
    private Label txt_noEncontrado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt_descripcion.setEditable(false);
    }    


    @FXML
    private void clicBuscar(ActionEvent event) {
        try {
            Item item = itemDAO.buscarUno(txt_idItem.getText());
            String descripcion = item.getDescripcionItem();
            if(descripcion == null){
                txt_descripcion.setText("");
                txt_noEncontrado.setText("No existe este código en el catálogo de items");
                return;
            }
            txt_descripcion.setText(descripcion);
        } catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al buscar el item",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar la descripción del item",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(!validarDatosCompletos()){
            return;
        }
    }

    private boolean validarDatosCompletos(){
        if(txt_idItem.getText() == null){
            return false;
        }
        if(txt_descripcion.getText() == null){
            return false;
        }
        if(txt_stockMinimo.getText() == null){
            return false;
        }
        if(txt_stockMaximo.getText() == null){
            return false;
        }
        return true;
    }

    // NAVEGABILIDAD //

    @FXML
    public void clicCancelar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle("Menu principal");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clicRegistrarFactura(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroFactura");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle("Registrar Factura");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarFacturas(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoFacturas");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle("Consultar Factura");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicRegistrarItem(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItemSucursal");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle("Registrar Item");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarItems(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoItems");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle("Consultar Items");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarBitacora(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("BitacoraPedidos");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle("Consultar Bitacora");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
