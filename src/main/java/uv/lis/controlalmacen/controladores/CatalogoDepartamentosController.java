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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.DepartamentoDAO;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoDepartamentosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Departamento> tvDepartamentos;
    @FXML private TableColumn<Departamento, Integer> colId;
    @FXML private TableColumn<Departamento, String> colDescripcion;
    @FXML private TableColumn<Departamento, String> colSucursal;

    private final ObservableList<Departamento> listaDepartamentos = FXCollections.observableArrayList();
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDepto"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colSucursal.setCellValueFactory(cellData -> {
            Departamento d = cellData.getValue();
            return new SimpleStringProperty(
                    d.getSucursal() != null ? d.getSucursal().getNombre() : "");
        });
        tvDepartamentos.setItems(listaDepartamentos);
        cargarDepartamentos();
    }

    private void cargarDepartamentos() {
        try {
            List<Departamento> lista = departamentoDAO.buscarTodos();
            listaDepartamentos.setAll(lista);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar los departamentos:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarDepartamentos();
            return;
        }
        try {
            List<Departamento> lista = departamentoDAO.buscarPorDescripcion(texto);
            listaDepartamentos.setAll(lista);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al buscar",
                    e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicVerTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarDepartamentos();
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Departamento seleccionado = tvDepartamentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona un departamento para modificar.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Departamento seleccionado = tvDepartamentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona un departamento para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion("Confirmar eliminación",
                "¿Eliminar el departamento \"" + seleccionado.getDescripcion() + "\"?\n" +
                "Esta acción no se puede deshacer.");
        if (!confirmar) return;
        try {
            departamentoDAO.eliminar(seleccionado.getIdDepto());
            UtilidadesFX.mostrarAlertaSimple("Eliminado",
                    "Departamento eliminado correctamente.", Alert.AlertType.INFORMATION);
            cargarDepartamentos();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    "No se pudo eliminar el departamento:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirFormulario(Departamento departamento) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroDepartamento");
            Parent vista = loader.load();
            RegistroDepartamentoController controller = loader.getController();
            if (departamento == null) {
                controller.inicializarRegistro();
            } else {
                controller.inicializarEdicion(departamento);
            }
            Stage modal = new Stage();
            modal.setTitle(departamento == null ? "Registrar Departamento" : "Modificar Departamento");
            modal.setResizable(false);
            modal.setScene(new Scene(vista));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(tvDepartamentos.getScene().getWindow());
            modal.centerOnScreen();
            modal.showAndWait();
            cargarDepartamentos();
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
            Stage stage = (Stage) tvDepartamentos.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
