package uv.lis.controlalmacen.controladores;

import com.mysql.cj.conf.PropertyDefinition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

public class ListadoItemsController implements Initializable {

    @FXML
    private ComboBox<String> cb_filtroStock;

    @FXML
    private ComboBox<String> cb_filtroPartida;

    private ObservableList<String> listaOpcionesStock = FXCollections.observableArrayList(
                                            "Sobre el máximo", "Menor que el mínimo");
    @FXML
    private TextField txt_buscarIdProducto;
    @FXML
    private TableView<ItemAlmacenado> tv_inventario;
    @FXML
    private TableColumn col_idItem;
    @FXML
    private TableColumn col_descripcion;
    @FXML
    private TableColumn col_existencias;
    @FXML
    private TableColumn col_stockMin;
    @FXML
    private TableColumn col_stockMax;

    private ObservableList<ItemAlmacenado> itemsAlmacenados;

    ItemAlmacenadoDAO itemAlmacenadoDAO 

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cb_filtroStock.setItems(listaOpcionesStock);
    }
    
    private void configurarTabla(){
        col_idItem.setCellValueFactory(new PropertyValueFactory("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory("descripcionItem"));
        col_existencias.setCellValueFactory(new PropertyValueFactory("existencias"));
        col_stockMin.setCellValueFactory(new PropertyValueFactory("stockMin"));
        col_stockMax.setCellValueFactory(new PropertyValueFactory("stockMax"));
    }

    private void cargarInformacionItems(){
        itemsAlmacenados = FXCollections.observableArrayList();
        List<ItemAlmacenado> itemsAlmacenadosBD =
    }

    @FXML
    public void clicRegresar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
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

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
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

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
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
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
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

            Stage stage = (Stage) cb_filtroPartida.getScene().getWindow();
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

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
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
