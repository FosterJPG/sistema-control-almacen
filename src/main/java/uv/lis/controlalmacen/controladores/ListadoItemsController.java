package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
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
    private TableView<?> tv_inventario;
    @FXML
    private TableColumn<?, ?> col_idItem;
    @FXML
    private TableColumn<?, ?> col_descripcion;
    @FXML
    private TableColumn<?, ?> col_existencias;
    @FXML
    private TableColumn<?, ?> col_stockMin;
    @FXML
    private TableColumn<?, ?> col_stockMax;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cb_filtroStock.setItems(listaOpcionesStock);
    }

    @FXML
    public void clicRegresar(ActionEvent actionEvent) {
        try {
            Parent vista = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
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
            Parent vista = UtilidadesFX.cargarFXML("RegistroFactura");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoFacturas");
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
            Parent vista = UtilidadesFX.cargarFXML("RegistroItem");
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
            Parent vista = UtilidadesFX.cargarFXML("ListadoItems");
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
            Parent vista = UtilidadesFX.cargarFXML("BitacoraPedidos");
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
