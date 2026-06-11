package uv.lis.controlalmacen.controladores;

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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPrincipalCentralController implements Initializable, MenuController {

    @FXML private Label lblBienvenida;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @Override
    public void cargarDatos() {
        lblBienvenida.setText("Bienvenido, " + Sesion.getUsuarioActual().getEmpleado().getNombre());
    }

    private void navegarA(String fxml, String titulo) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML(fxml);
            Parent vista = loader.load();
            Scene escena = new Scene(vista);
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicItems(ActionEvent e)      { navegarA("CatalogoItems",   "Catálogo de Ítems"); }
    @FXML private void clicEncargados(ActionEvent e) { navegarA("GestorEmpleados", "Gestión de Empleados"); }
    @FXML private void clicSucursales(ActionEvent e) { navegarA("CatalogoSucursales",      "Sucursales"); }
    @FXML private void clicPartidas(ActionEvent e)   { navegarA("CatalogoPartidas","Partidas Presupuestales"); }
    @FXML private void clicFacturas(ActionEvent e)   { navegarA("ListadoFacturas", "Listado de Facturas"); }
    @FXML private void clicSolicitudes(ActionEvent e){ navegarA("ListadoSolicitudes", "Listado de Solicitudes"); }

    @FXML
    public void clicCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("InicioSesion");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setTitle("Control de Almacén - GLOBAL FINANCE");
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            Sesion.cerrarSesion();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
