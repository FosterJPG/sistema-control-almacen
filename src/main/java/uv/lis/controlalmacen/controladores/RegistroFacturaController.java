package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
//import uv.lis.controlalmacen.logica.FacturaService;
import uv.lis.controlalmacen.modelo.dto.DetallesFactura;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableColumn col_idItem;
    @FXML
    private TableColumn col_descripcion;
    @FXML
    private TableColumn col_cantidad;
    @FXML
    private TableColumn col_costoUnitario;
    @FXML
    private TableColumn col_partidaPresupuestal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaDetallesFactura();
    }

    private void configurarTablaDetallesFactura(){
        col_idItem.setCellValueFactory(new PropertyValueFactory("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory("descripcion"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory("cantidad"));
        col_costoUnitario.setCellValueFactory(new PropertyValueFactory("costoUnitario"));
        col_partidaPresupuestal.setCellValueFactory(new PropertyValueFactory("descripcionPartida"));

    }


    @FXML
    private void clicEliminarItem(ActionEvent event) {
    }

    @FXML
    private void clicAgregarItem(ActionEvent event) {
    }

    @FXML
    private void clicGuardarFactura(ActionEvent actionEvent) {
    }

    /*
    @FXML
    private void clicGuardarFactura(ActionEvent event) {
        //VALIDAR DATOS
        if(datosValidosFactura() && datosValidosDetalles()){
            guardarFactura();
            guardarDetalles();

        }else{
            UtilidadesFX.mostrarAlertaSimple("Datos inválidos",
                    "Datos inválidos para proceder con el guardado",
                            Alert.AlertType.WARNING);
        }
    }

    //1. VALIDAR DATOS

    private boolean datosValidosFactura(){

        return false;
    }

    private boolean datosValidosDetalles(){

        return false;
    }

    /*
    //2. OBTENER
    //TODO HABER VALIDADO DATOS
    private Factura obtenerFactura(){
        Factura factura = new Factura();
        factura.setFolio(txt_folio.getText());
        //factura.setFecha(dp_fecha.getValue());
        factura.setRfc(txt_rfc.getText());
        factura.setTelefono(txt_telefono.getText());
        factura.setNoSucursal(Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
        factura.setRazonSocial(txt_rfc.getText());
        return factura;
    }

    private List<DetallesFactura> obtenerDetalles(){

        return null;
    }


    //3.GUARDAR
    private void guardarFactura(){
        try{
            Factura factura = obtenerFactura();

            if(FacturaService.guardarFactura(factura)){
                UtilidadesFX.mostrarAlertaSimple("Factura guardada correctamente",
                        "La información de la factura fue registrada correctamente",
                        Alert.AlertType.WARNING);
            }
            //AQUI VAN LAS EXCEPCIONES QUE VENGAN DE OBTENER Y GUARDAR
        }catch(Exception e) { // TODO cambiar a las excepciones especificas
            // Mensaje
        }
    }

    private void guardarDetalles(){
        try{
            List<DetallesFactura> detallesFactura = obtenerDetalles();

            if(FacturaService.guardarDetalles(detallesFactura)){
                UtilidadesFX.mostrarAlertaSimple("Factura guardada correctamente",
                        "La información de la factura fue registrada correctamente",
                        Alert.AlertType.WARNING);
            }
            //AQUI VAN LAS EXCEPCIONES QUE VENGAN DE OBTENER Y GUARDAR
        }catch(Exception e) { // TODO cambiar a las excepciones especificas
            // Mensaje
        }
    }
*/
    //NAVEGACION
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

            stage.setScene(escena);
            stage.centerOnScreen();
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

            stage.setScene(escena);
            stage.centerOnScreen();
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

            stage.setScene(escena);
            stage.centerOnScreen();
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

            stage.setScene(escena);
            stage.centerOnScreen();
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

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
