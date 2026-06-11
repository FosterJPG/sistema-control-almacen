package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.SucursalDAO;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoSucursalesController implements Initializable {

    @FXML private TableView<Sucursal> tv_listado;
    @FXML private TableColumn<Sucursal, Integer> col_noSucursal;
    @FXML private TableColumn<Sucursal, String>  col_nombre;
    @FXML private TableColumn<Sucursal, String>  col_direccion;
    @FXML private TableColumn<Sucursal, String>  col_telefono;
    @FXML private TextField txt_nombreSucursal;

    private final ObservableList<Sucursal> listaSucursales = FXCollections.observableArrayList();
    private FilteredList<Sucursal> listaFiltrada;
    private final SucursalDAO dao = new SucursalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarSucursales();
    }

    private void configurarTabla() {
        col_noSucursal.setCellValueFactory(new PropertyValueFactory<>("noSucursal"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        tv_listado.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        listaFiltrada = new FilteredList<>(listaSucursales, s -> true);
        tv_listado.setItems(listaFiltrada);

        txt_nombreSucursal.textProperty().addListener((obs, old, val) -> filtrar(val));
    }

    private void cargarSucursales() {
        try {
            List<Sucursal> sucursales = dao.buscarTodos();
            listaSucursales.setAll(sucursales);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar las sucursales: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filtrar(String texto) {
        listaFiltrada.setPredicate(s -> {
            if (texto == null || texto.isBlank()) return true;
            return s.getNombre().toLowerCase().contains(texto.toLowerCase());
        });
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        filtrar(txt_nombreSucursal.getText());
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirModal(null);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Sucursal seleccionada = tv_listado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona una sucursal de la tabla para modificarla.", Alert.AlertType.WARNING);
            return;
        }
        abrirModal(seleccionada);
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Sucursal seleccionada = tv_listado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona una sucursal de la tabla para eliminarla.", Alert.AlertType.WARNING);
            return;
        }
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion("Eliminar sucursal",
                "¿Eliminar la sucursal \"" + seleccionada.getNombre() + "\"?\nEsta acción no se puede deshacer.");
        if (!confirmar) return;

        try {
            dao.eliminar(seleccionada);
            listaSucursales.remove(seleccionada);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    "No se pudo eliminar la sucursal:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirModal(Sucursal sucursal) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroSucursal");
            Parent vista = loader.load();
            RegistroSucursalController controller = loader.getController();
            if (sucursal != null) controller.setSucursal(sucursal);

            Stage modal = new Stage();
            modal.setTitle(sucursal == null ? "Nueva Sucursal" : "Modificar Sucursal");
            modal.setScene(new Scene(vista));
            modal.setResizable(false);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(tv_listado.getScene().getWindow());
            modal.showAndWait();

            cargarSucursales();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(Sesion.getUsuarioActual().getRol());
            FXMLLoader loader = UtilidadesFX.cargarFXML(rutaMenu);
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(new Scene(vista));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
