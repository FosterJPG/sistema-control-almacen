package uv.lis.controlalmacen.controladores;

import com.mysql.cj.conf.PropertyDefinition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListadoItemsController implements Initializable {

    @FXML
    private ComboBox<String> cb_filtroStock;
    private ComboBox<PartidaPresupuestal> cb_filtroPartida;
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

    private ObservableList<String> listaOpcionesStock = FXCollections.observableArrayList(
            "Sobre el máximo", "Menor que el mínimo", "Mostrar Todos");
    private ObservableList<ItemAlmacenado> itemsAlmacenados;

    ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cb_filtroStock.setItems(listaOpcionesStock);
        configurarTabla();
        cargarInformacionItems();
        configurarSeleccionStock();
    }

    private void configurarTabla(){
        col_idItem.setCellValueFactory(new PropertyValueFactory("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory("descripcionItem"));
        col_existencias.setCellValueFactory(new PropertyValueFactory("existencias"));
        col_stockMin.setCellValueFactory(new PropertyValueFactory("stockMin"));
        col_stockMax.setCellValueFactory(new PropertyValueFactory("stockMax"));
    }

    private void cargarInformacionItems(){
        try {
            itemsAlmacenados = FXCollections.observableArrayList();
            List<ItemAlmacenado> itemsAlmacenadosBD = itemAlmacenadoDAO.buscarTodos();
            itemsAlmacenados.addAll(itemsAlmacenadosBD);
            tv_inventario.setItems(itemsAlmacenados);
        }catch(SQLException ex){
        UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                                        ex.getMessage(),
                                        Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
        UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                "Lo sentimos, los items de la sucursal "
                        + "no pueden ser cargados en este momento,"
                        + "porfavor inténtelo más tade",
                        Alert.AlertType.WARNING);
        }
    }

    private void configurarSeleccionStock(){
        cb_filtroStock.valueProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null){
                    try {
                        itemsAlmacenados = FXCollections.observableArrayList();
                        List<ItemAlmacenado> itemsAlmacenadosBD;
                        if(newValue.equals("Mostrar Todos")){
                            itemsAlmacenadosBD = itemAlmacenadoDAO.buscarTodos();
                        }else{
                            itemsAlmacenadosBD = itemAlmacenadoDAO.buscarPorStock(newValue);
                        }

                        itemsAlmacenados.addAll(itemsAlmacenadosBD);
                        tv_inventario.setItems(itemsAlmacenados);
                    }catch(SQLException ex){
                        UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                                ex.getMessage(),
                                Alert.AlertType.ERROR);
                    }catch(NullPointerException | ClassNotFoundException | IOException n) {
                        UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                                "Lo sentimos, los items de la sucursal "
                                        + "no pueden ser cargados en este momento,"
                                        + "porfavor inténtelo más tade",
                                Alert.AlertType.WARNING);
                    }
                }
            }
        });
    }

    @FXML
    private void clicBuscarItemAlmacenado(ActionEvent event) {
        try {
            String idItem = txt_buscarIdProducto.getText();
            itemsAlmacenados = FXCollections.observableArrayList();
            ItemAlmacenado itemAlmacenado = itemAlmacenadoDAO.buscarUno(idItem);
            itemsAlmacenados.add(itemAlmacenado);
            tv_inventario.setItems(itemsAlmacenados);
        }catch(SQLException ex){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "Lo sentimos, el item buscado "
                            + "no puede ser cargado en este momento,"
                            + "porfavor inténtelo más tade",
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicVerKardex(ActionEvent event) {
        ItemAlmacenado itemElegido = tv_inventario.getSelectionModel().getSelectedItem();
        if(itemElegido != null){
            try {
                FXMLLoader loader = UtilidadesFX.cargarFXML("Kardex");
                Parent vista = loader.load();
                KardexController controller = loader.getController();
                controller.cargarKardexItem(itemElegido);
                Scene escena = new Scene(vista);

                Stage stage = new Stage();
                stage.setTitle("Kárdex");
                stage.setResizable(false);
                stage.setScene(escena);

                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "No hay un item seleccionado para mostrar un kárdex",
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

            Stage stage = (Stage) cb_filtroStock.getScene().getWindow();
            stage.setTitle("Menú principal");
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
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItemSucursal");
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
