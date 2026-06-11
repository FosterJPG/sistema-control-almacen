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
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoPartidasController implements Initializable {

    @FXML private TableView<PartidaPresupuestal> tv_listado;
    @FXML private TableColumn<PartidaPresupuestal, Integer> col_codigo;
    @FXML private TableColumn<PartidaPresupuestal, String>  col_descripcion;
    @FXML private TextField txtBuscar;

    private final ObservableList<PartidaPresupuestal> listaPartidas = FXCollections.observableArrayList();
    private FilteredList<PartidaPresupuestal> listaFiltrada;
    private final PartidaPresupuestalDAO dao = new PartidaPresupuestalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPartidas();
    }

    private void configurarTabla() {
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionPartida"));
        tv_listado.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        listaFiltrada = new FilteredList<>(listaPartidas, p -> true);
        tv_listado.setItems(listaFiltrada);

        txtBuscar.textProperty().addListener((obs, old, val) -> filtrar(val));
    }

    private void cargarPartidas() {
        try {
            List<PartidaPresupuestal> partidas = dao.buscarTodos();
            listaPartidas.setAll(partidas);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar las partidas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filtrar(String texto) {
        listaFiltrada.setPredicate(p -> {
            if (texto == null || texto.isBlank()) return true;
            String lower = texto.toLowerCase();
            return p.getDescripcionPartida().toLowerCase().contains(lower)
                    || String.valueOf(p.getCodigo()).contains(lower);
        });
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        filtrar(txtBuscar.getText());
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirModal(null);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        PartidaPresupuestal seleccionada = tv_listado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona una partida de la tabla para modificarla.", Alert.AlertType.WARNING);
            return;
        }
        abrirModal(seleccionada);
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        PartidaPresupuestal seleccionada = tv_listado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona una partida de la tabla para eliminarla.", Alert.AlertType.WARNING);
            return;
        }
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion("Eliminar partida",
                "¿Eliminar la partida \"" + seleccionada.getDescripcionPartida() + "\"?\nEsta acción no se puede deshacer.");
        if (!confirmar) return;

        try {
            dao.eliminar(seleccionada);
            listaPartidas.remove(seleccionada);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    "No se pudo eliminar la partida:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirModal(PartidaPresupuestal partida) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroPartida");
            Parent vista = loader.load();
            RegistroPartidaController controller = loader.getController();
            if (partida != null) controller.setPartida(partida);

            Stage modal = new Stage();
            modal.setTitle(partida == null ? "Nueva Partida" : "Modificar Partida");
            modal.setScene(new Scene(vista));
            modal.setResizable(false);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(tv_listado.getScene().getWindow());
            modal.showAndWait();

            cargarPartidas();
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
