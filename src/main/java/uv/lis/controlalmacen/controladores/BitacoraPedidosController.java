package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dto.ItemPedido;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.ExportadorExcel;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.File;
import java.sql.SQLException;

public class BitacoraPedidosController implements Initializable {

    @FXML private TableView<ItemPedido> tv_bitacora;
    @FXML private TableColumn col_idItem;
    @FXML private TableColumn col_descripcion;
    @FXML private TableColumn col_existencias;
    @FXML private TableColumn col_stockMin;
    @FXML private TableColumn col_fecha;

    private final ObservableList<ItemPedido> listaPedidos = FXCollections.observableArrayList();
    private final ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        col_idItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        col_existencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        col_stockMin.setCellValueFactory(new PropertyValueFactory<>("stockMin"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tv_bitacora.setItems(listaPedidos);
    }

    private void cargarDatos() {
        try {
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            List<ItemPedido> pedidos = itemAlmacenadoDAO.buscarBitacoraPedidos(noSucursal);
            listaPedidos.setAll(pedidos);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudo cargar la bitácora de pedidos: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicExportarExcel(ActionEvent event) {
        if (listaPedidos.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin pedidos pendientes",
                    "No hay ítems bajo stock mínimo registrados en la bitácora.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar bitácora de pedidos");
        fileChooser.setInitialFileName("bitacora-pedidos.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));

        Stage stageActual = (Stage) tv_bitacora.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stageActual);

        if (archivo == null) return;

        try {
            ExportadorExcel.generarBitacoraPedidos(archivo.getAbsolutePath(), listaPedidos);

            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            itemAlmacenadoDAO.eliminarBitacoraPedidos(noSucursal);

            cargarDatos();

            UtilidadesFX.mostrarAlertaSimple("Exportación exitosa",
                    "Bitácora guardada en:\n" + archivo.getAbsolutePath() +
                    "\n\nLa lista ha sido reiniciada.",
                    Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al exportar",
                    "No se pudo generar el archivo Excel: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al limpiar bitácora",
                    "El archivo fue generado, pero no se pudo reiniciar la lista: " + e.getMessage(),
                    Alert.AlertType.WARNING);
        }
    }



    // NAVEGABILIDAD //

    @FXML
    public void clicRegresar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_bitacora.getScene().getWindow();
            stage.setTitle("Menu principal");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
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

            Stage stage = (Stage) tv_bitacora.getScene().getWindow();
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
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_bitacora.getScene().getWindow();
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
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItemSucursal");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_bitacora.getScene().getWindow();
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
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoItems");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_bitacora.getScene().getWindow();
            stage.setTitle("Consultar Items");
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
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("BitacoraPedidos");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_bitacora.getScene().getWindow();
            stage.setTitle("Consultar Bitacora");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
