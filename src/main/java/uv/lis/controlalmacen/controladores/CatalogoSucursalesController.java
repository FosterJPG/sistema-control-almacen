package uv.lis.controlalmacen.controladores;

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
import uv.lis.controlalmacen.modelo.dao.SucursalDAO;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoSucursalesController implements Initializable {

    @FXML
    private TextField txt_nombreSucursal;
    @FXML
    private TableView<Sucursal> tv_listado;
    @FXML
    private TableColumn<Sucursal, String> col_nombre;
    @FXML
    private TableColumn<Sucursal, String> col_direccion;
    @FXML
    private TableColumn<Sucursal, String> col_telefono;

    private ObservableList<Sucursal> sucursales;
    private final SucursalDAO sucursalDAO = new SucursalDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarInformacionSucursales();
    }

    private void configurarTabla() {
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    private void cargarInformacionSucursales() {
        try {
            sucursales = FXCollections.observableArrayList();

            List<Sucursal> sucursalesBD = sucursalDAO.buscarTodos();
            sucursales.addAll(sucursalesBD);

            tv_listado.setItems(sucursales);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar sucursales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String nombreSucursal = obtenerTextoBusqueda();

        if (nombreSucursal.isEmpty()) {
            cargarInformacionSucursales();
            return;
        }

        buscarSucursalesPorNombre(nombreSucursal);
    }

    private void buscarSucursalesPorNombre(String nombreSucursal) {
        try {
            sucursales = FXCollections.observableArrayList();

            List<Sucursal> sucursalesBD = sucursalDAO.buscarPorNombre(nombreSucursal);
            sucursales.addAll(sucursalesBD);

            tv_listado.setItems(sucursales);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al buscar sucursales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicVerTodos(ActionEvent event) {
        cargarInformacionSucursales();
        txt_nombreSucursal.clear();
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioSucursal(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Sucursal sucursalSeleccionada = tv_listado.getSelectionModel().getSelectedItem();

        if (sucursalSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione una sucursal para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioSucursal(sucursalSeleccionada, true);
    }

    private void abrirFormularioSucursal(Sucursal sucursal, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroSucursal");
            Parent vista = loader.load();

            RegistroSucursalController controller = loader.getController();

            if (esEdicion) {
                controller.inicializarEdicion(sucursal);
            } else {
                controller.inicializarRegistro();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Sucursal" : "Registrar Sucursal");
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
        Sucursal sucursalSeleccionada = tv_listado.getSelectionModel().getSelectedItem();

        if (sucursalSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione una sucursal para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar la sucursal \"" +
                        sucursalSeleccionada.getNombre() + "\"?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (sucursalDAO.eliminar(sucursalSeleccionada)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "La sucursal se eliminó correctamente.",
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
                    "Error al eliminar sucursal",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        String nombreSucursal = obtenerTextoBusqueda();

        if (nombreSucursal.isEmpty()) {
            cargarInformacionSucursales();
        } else {
            buscarSucursalesPorNombre(nombreSucursal);
        }
    }

    private String obtenerTextoBusqueda() {
        if (txt_nombreSucursal.getText() == null) {
            return "";
        }

        return txt_nombreSucursal.getText().trim();
    }

    @FXML
    private void clicFacturas(ActionEvent event) {
        Sucursal sucursalSeleccionada = tv_listado.getSelectionModel().getSelectedItem();
        if (sucursalSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección", "Seleccione una sucursal para ver sus facturas.", Alert.AlertType.WARNING);
            return;
        }
        try {
            Departamento departamento = new Departamento();
            departamento.setSucursal(sucursalSeleccionada);
            Sesion.getUsuarioActual().getEmpleado().setDepartamento(departamento);
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoFacturas");
            Parent vista = loader.load();
            ListadoFacturasController controller = loader.getController();
            controller.setOrigenCatalogoSucursales(true);
            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Listado de Facturas");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error de navegación", "No se pudo abrir el listado de facturas.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicSolicitudes(ActionEvent event) {
        Sucursal sucursalSeleccionada = tv_listado.getSelectionModel().getSelectedItem();
        if (sucursalSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección", "Seleccione una sucursal para ver sus solicitudes.", Alert.AlertType.WARNING);
            return;
        }
        try {
            Departamento departamento = new Departamento();
            departamento.setSucursal(sucursalSeleccionada);
            Sesion.getUsuarioActual().getEmpleado().setDepartamento(departamento);
            FXMLLoader loader = UtilidadesFX.cargarFXML("ConsultarSolicitudes");
            Parent vista = loader.load();
            ConsultarSolicitudesController controller = loader.getController();
            controller.setOrigenCatalogoSucursales(true);
            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Consultar Solicitudes");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error de navegación", "No se pudo abrir las solicitudes.", Alert.AlertType.ERROR);
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

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}