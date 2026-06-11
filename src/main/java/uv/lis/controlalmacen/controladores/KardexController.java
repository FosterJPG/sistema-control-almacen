package uv.lis.controlalmacen.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.KardexDAO;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.Kardex;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

public class KardexController implements Initializable {

    @FXML
    private TableView<Kardex> tv_kardex;
    @FXML
    private TableColumn col_folio;
    @FXML
    private TableColumn col_fechaFactura;
    @FXML
    private TableColumn col_costoUnitario;
    @FXML
    private TableColumn col_costoPromedio;

    private ObservableList<Kardex> kardexItem;
    @FXML
    private Label lb_descripcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaKardex();
    }

    private void configurarTablaKardex(){
        col_folio.setCellValueFactory(new PropertyValueFactory("folioFactura"));
        col_fechaFactura.setCellValueFactory(new PropertyValueFactory("fechaFactura"));
        col_costoUnitario.setCellValueFactory(new PropertyValueFactory("costoUnitario"));
        col_costoPromedio.setCellValueFactory(new PropertyValueFactory("costoPromedio"));
    }

    public void cargarKardexItem(ItemAlmacenado item){
        try {
            kardexItem = FXCollections.observableArrayList();
            int noSucursal = Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal();

            List<Kardex> kardexItemsBD = KardexDAO.buscarKardexItem(item.getIdItem(), noSucursal);
            kardexItem.addAll(kardexItemsBD);
            tv_kardex.setItems(kardexItem);
            lb_descripcion.setText(item.getDescripcionItem());
        }catch(SQLException ex){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "Lo sentimos, los kardex items de la sucursal "
                            + "no pueden ser cargados en este momento,"
                            + " por favor inténtelo más tarde",
                    Alert.AlertType.WARNING);
        }
    }

    //NAVEGABILIDAD
    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) tv_kardex.getScene().getWindow()).close();
    }
}