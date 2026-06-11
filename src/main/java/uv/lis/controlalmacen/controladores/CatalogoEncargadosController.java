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
import uv.lis.controlalmacen.modelo.dao.EmpleadoDAO;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoEncargadosController implements Initializable {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Empleado> tv_listado;

    @FXML
    private TableColumn<Empleado, String> col_nombre;

    @FXML
    private TableColumn<Empleado, String> col_paterno;

    @FXML
    private TableColumn<Empleado, String> col_materno;

    @FXML
    private TableColumn<Empleado, String> col_direccion;

    @FXML
    private TableColumn<Empleado, String> col_correo;

    @FXML
    private TableColumn<Empleado, String> col_telefono;

    @FXML
    private TableColumn<Empleado, String> col_sucursal;

    private ObservableList<Empleado> encargados;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarInformacionEncargados();
    }

    private void configurarTabla() {
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_paterno.setCellValueFactory(new PropertyValueFactory<>("paterno"));
        col_materno.setCellValueFactory(new PropertyValueFactory<>("materno"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        col_sucursal.setCellValueFactory(cellData -> {
            Empleado empleado = cellData.getValue();

            if (empleado.getDepartamento() == null
                    || empleado.getDepartamento().getSucursal() == null
                    || empleado.getDepartamento().getSucursal().getNombre() == null) {
                return new ReadOnlyStringWrapper("");
            }

            return new ReadOnlyStringWrapper(
                    empleado.getDepartamento().getSucursal().getNombre()
            );
        });
    }

    private void cargarInformacionEncargados() {
        try {
            encargados = FXCollections.observableArrayList();

            List<Empleado> encargadosBD = empleadoDAO.buscarEncargados();
            encargados.addAll(encargadosBD);

            tv_listado.setItems(encargados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar encargados",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String textoBusqueda = obtenerTextoBusqueda();

        if (textoBusqueda.isEmpty()) {
            cargarInformacionEncargados();
            return;
        }

        buscarEncargadosPorNombre(textoBusqueda);
    }

    private void buscarEncargadosPorNombre(String textoBusqueda) {
        try {
            encargados = FXCollections.observableArrayList();

            List<Empleado> encargadosBD = empleadoDAO.buscarEncargadosPorNombre(textoBusqueda);
            encargados.addAll(encargadosBD);

            tv_listado.setItems(encargados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al buscar encargados",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioEncargado(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Empleado encargadoSeleccionado = tv_listado.getSelectionModel().getSelectedItem();

        if (encargadoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un encargado para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioEncargado(encargadoSeleccionado, true);
    }

    private void abrirFormularioEncargado(Empleado empleado, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroEmpleado");
            Parent vista = loader.load();

            RegistroEmpleadoController controller = loader.getController();

            if (esEdicion) {
                controller.inicializarEdicionEncargado(empleado);
            } else {
                controller.inicializarRegistroEncargado();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Encargado" : "Registrar Encargado");
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
        Empleado encargadoSeleccionado = tv_listado.getSelectionModel().getSelectedItem();

        if (encargadoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un encargado para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar al encargado \"" +
                        encargadoSeleccionado.getNombre() + " " +
                        encargadoSeleccionado.getPaterno() + "\"?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (empleadoDAO.eliminar(encargadoSeleccionado)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "El encargado se eliminó correctamente.",
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
                    "Error al eliminar encargado",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        String textoBusqueda = obtenerTextoBusqueda();

        if (textoBusqueda.isEmpty()) {
            cargarInformacionEncargados();
        } else {
            buscarEncargadosPorNombre(textoBusqueda);
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

            Stage stage = (Stage) tv_listado.getScene().getWindow();
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