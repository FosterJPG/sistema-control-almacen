package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPrincipalEncargadoController implements Initializable {
    @FXML
    private Label lb_nombreEmpleado;

    @FXML
    private Label lb_nombreSucursal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // TODO implementacion para mostrar
    public void cargarDatos(String nombreEmpleado){
        lb_nombreEmpleado.setText(nombreEmpleado);
    }

    @FXML
    public void clicCerrarSesion(ActionEvent actionEvent) {
        try {
            Parent vista = UtilidadesFX.cargarFXML("InicioSesion");
            Scene escena = new Scene(vista);

            Stage stage = (Stage) lb_nombreEmpleado.getScene().getWindow();
            stage.setTitle("Control de Almacén - GLOBAL FINANCE");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicRegistrarFactura(ActionEvent actionEvent) {
        try {
            Parent vista = UtilidadesFX.cargarFXML("RegistroFactura");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoFacturas");
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
            Parent vista = UtilidadesFX.cargarFXML("RegistroItem");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoItems");
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
            Parent vista = UtilidadesFX.cargarFXML("BitacoraPedidos");
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
