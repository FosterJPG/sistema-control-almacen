/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package uv.lis.controlalmacen.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class MenuPrincipalSalidasController implements Initializable, MenuController {

    @FXML
    private Label lb_nombreEmpleado;
    @FXML
    private Label lb_nombreSucursal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lb_nombreEmpleado.setText("Bienvenido: " + Sesion.getUsuarioActual().getEmpleado().getNombre());
        lb_nombreSucursal.setText("Sucursal: " + Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNombre());
    }

    @Override
    public void cargarDatos(){

    }
    
    @FXML
    private void clicAtenderSolicitud(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoSolicitudes");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
            stage.setTitle("Solicitudes por atender");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    public void clicCerrarSesion(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("InicioSesion");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
            stage.setTitle("Control de Almacén - GLOBAL FINANCE");
            stage.setResizable(false);

            Sesion.cerrarSesion();
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}