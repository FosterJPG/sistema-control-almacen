package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPrincipalEncargadoController implements Initializable, MenuController {

    @FXML
    private Label lb_nombreEmpleado;

    @FXML
    private Label lb_nombreSucursal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void cargarDatos(){
        lb_nombreEmpleado.setText("Bienvenido: " + Sesion.getUsuarioActual().getEmpleado().getNombre());
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
            stage.centerOnScreen();

            Sesion.cerrarSesion();
            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicRegistrarFactura(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroFactura");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
            stage.setTitle("Registro de facturas");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicConsultarFacturas(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoFacturas");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
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
            FXMLLoader loader = UtilidadesFX.cargarFXML("ConfigurarStock");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
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
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
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
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
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
