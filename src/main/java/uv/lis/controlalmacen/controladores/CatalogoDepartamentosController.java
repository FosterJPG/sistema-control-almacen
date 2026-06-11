package uv.lis.controlalmacen.controladores;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.DepartamentoDAO;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoDepartamentosController implements Initializable {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Departamento> tvDepartamentos;

    @FXML
    private TableColumn<Departamento, String> colDescripcion;

    @FXML
    private TableColumn<Departamento, String> colSucursal;

    private ObservableList<Departamento> departamentos;

    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarInformacionDepartamentos();
    }

    private void configurarTabla() {
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colSucursal.setCellValueFactory(cellData -> {
            Departamento departamento = cellData.getValue();

            if (departamento.getSucursal() == null || departamento.getSucursal().getNombre() == null) {
                return new ReadOnlyStringWrapper("");
            }

            return new ReadOnlyStringWrapper(departamento.getSucursal().getNombre());
        });
    }

    private void cargarInformacionDepartamentos() {
        try {
            departamentos = FXCollections.observableArrayList();

            List<Departamento> departamentosBD = departamentoDAO.buscarTodos();
            departamentos.addAll(departamentosBD);

            tvDepartamentos.setItems(departamentos);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar departamentos",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String textoBusqueda = obtenerTextoBusqueda();

        if (textoBusqueda.isEmpty()) {
            cargarInformacionDepartamentos();
            return;
        }

        buscarDepartamentosPorDescripcion(textoBusqueda);
    }

    @FXML
    private void clicVerTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarInformacionDepartamentos();
    }

    private void buscarDepartamentosPorDescripcion(String textoBusqueda) {
        try {
            departamentos = FXCollections.observableArrayList();

            List<Departamento> departamentosBD = departamentoDAO.buscarPorDescripcion(textoBusqueda);
            departamentos.addAll(departamentosBD);

            tvDepartamentos.setItems(departamentos);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al buscar departamentos",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioDepartamento(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Departamento departamentoSeleccionado = tvDepartamentos.getSelectionModel().getSelectedItem();

        if (departamentoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un departamento para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioDepartamento(departamentoSeleccionado, true);
    }

    private void abrirFormularioDepartamento(Departamento departamento, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroDepartamento");
            Parent vista = loader.load();

            RegistroDepartamentoController controller = loader.getController();

            if (esEdicion) {
                controller.inicializarEdicion(departamento);
            } else {
                controller.inicializarRegistro();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Departamento" : "Registrar Departamento");
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            actualizarInformacion();

        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al abrir formulario",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Departamento departamentoSeleccionado = tvDepartamentos.getSelectionModel().getSelectedItem();

        if (departamentoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un departamento para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar el departamento \"" +
                        departamentoSeleccionado.getDescripcion() + "\"?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (departamentoDAO.eliminar(departamentoSeleccionado)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "El departamento se eliminó correctamente.",
                        Alert.AlertType.INFORMATION
                );

                actualizarInformacion();
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al eliminar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al eliminar departamento",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        String textoBusqueda = obtenerTextoBusqueda();

        if (textoBusqueda.isEmpty()) {
            cargarInformacionDepartamentos();
        } else {
            buscarDepartamentosPorDescripcion(textoBusqueda);
        }
    }

    private String obtenerTextoBusqueda() {
        if (txtBuscar.getText() == null) {
            return "";
        }

        return txtBuscar.getText().trim();
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
            stage.centerOnScreen();
            stage.setScene(new Scene(vista));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}