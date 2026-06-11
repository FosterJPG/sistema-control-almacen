package uv.lis.controlalmacen.controladores;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dto.ItemBaja;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class BitacoraBajasController implements Initializable {

    @FXML private TableView<ItemBaja> tv_bajas;
    @FXML private TableColumn<ItemBaja, String> col_idItem;
    @FXML private TableColumn<ItemBaja, String> col_descripcion;
    @FXML private TableColumn<ItemBaja, String> col_fechaBaja;
    @FXML private TableColumn<ItemBaja, String> col_razon;
    @FXML private TableColumn<ItemBaja, Integer> col_existencias;

    private final ObservableList<ItemBaja> listaBajas = FXCollections.observableArrayList();
    private final ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        col_idItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionItem"));
        col_fechaBaja.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaBaja();
            return new SimpleStringProperty(fecha != null ? SDF.format(fecha) : "");
        });
        col_razon.setCellValueFactory(new PropertyValueFactory<>("razon"));
        col_existencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        tv_bajas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tv_bajas.setItems(listaBajas);
    }

    private void cargarDatos() {
        try {
            int noSucursal = Sesion.getUsuarioActual().getEmpleado()
                    .getDepartamento().getSucursal().getNoSucursal();
            List<ItemBaja> bajas = itemAlmacenadoDAO.buscarBitacoraBajas(noSucursal);
            listaBajas.setAll(bajas);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudo cargar la bitácora de bajas: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clicRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_bajas.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
