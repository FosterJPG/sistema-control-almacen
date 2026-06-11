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

public class CatalogoEmpleadosController implements Initializable {

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
    private TableColumn<Empleado, String> col_depto;

    private ObservableList<Empleado> empleados;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarInformacionEmpleados();
    }

    private void configurarTabla() {
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_paterno.setCellValueFactory(new PropertyValueFactory<>("paterno"));
        col_materno.setCellValueFactory(new PropertyValueFactory<>("materno"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        col_depto.setCellValueFactory(cellData -> {
            Empleado empleado = cellData.getValue();

            if (empleado.getDepartamento() == null
                    || empleado.getDepartamento().getDescripcion() == null) {
                return new ReadOnlyStringWrapper("");
            }

            return new ReadOnlyStringWrapper(empleado.getDepartamento().getDescripcion());
        });
    }

    private void cargarInformacionEmpleados() {
        try {
            empleados = FXCollections.observableArrayList();

            Integer noSucursal = obtenerSucursalActual();
            List<Empleado> empleadosBD = empleadoDAO.buscarEmpleadosPorSucursal(noSucursal);

            empleados.addAll(empleadosBD);
            tv_listado.setItems(empleados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar empleados",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String textoBusqueda = obtenerTextoBusqueda();

        if (textoBusqueda.isEmpty()) {
            cargarInformacionEmpleados();
            return;
        }

        buscarEmpleadosPorNombre(textoBusqueda);
    }

    private void buscarEmpleadosPorNombre(String textoBusqueda) {
        try {
            empleados = FXCollections.observableArrayList();

            Integer noSucursal = obtenerSucursalActual();
            List<Empleado> empleadosBD =
                    empleadoDAO.buscarEmpleadosPorNombreYSucursal(textoBusqueda, noSucursal);

            empleados.addAll(empleadosBD);
            tv_listado.setItems(empleados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al buscar empleados",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioEmpleado(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Empleado empleadoSeleccionado = tv_listado.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un empleado para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioEmpleado(empleadoSeleccionado, true);
    }

    private void abrirFormularioEmpleado(Empleado empleado, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroEmpleado");
            Parent vista = loader.load();

            RegistroEmpleadoController controller = loader.getController();

            if (esEdicion) {
                controller.inicializarEdicionEmpleado(empleado);
            } else {
                controller.inicializarRegistroEmpleado();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Empleado" : "Registrar Empleado");
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
        Empleado empleadoSeleccionado = tv_listado.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un empleado para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar al empleado \"" +
                        empleadoSeleccionado.getNombre() + " " +
                        empleadoSeleccionado.getPaterno() + "\"?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (empleadoDAO.eliminar(empleadoSeleccionado)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "El empleado se eliminó correctamente.",
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
                    "Error al eliminar empleado",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        String textoBusqueda = obtenerTextoBusqueda();

        if (textoBusqueda.isEmpty()) {
            cargarInformacionEmpleados();
        } else {
            buscarEmpleadosPorNombre(textoBusqueda);
        }
    }

    private String obtenerTextoBusqueda() {
        if (txtBuscar.getText() == null) {
            return "";
        }

        return txtBuscar.getText().trim();
    }

    private Integer obtenerSucursalActual() {
        return Sesion.getUsuarioActual()
                .getEmpleado()
                .getDepartamento()
                .getSucursal()
                .getNoSucursal();
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

    @FXML
    private void clicRegistrarFactura(ActionEvent event) {
        cargarEscena("RegistroFactura", "Registro de facturas");
    }

    @FXML
    private void clicRegistrarItem(ActionEvent event) {
        cargarEscena("RegistroItemSucursal", "Registro de ítems para la sucursal");
    }

    @FXML
    private void clicConsultarItems(ActionEvent event) {
        cargarEscena("ListadoItems", "Listado de ítems almacenados");
    }

    @FXML
    private void clicConsultarBitacora(ActionEvent event) {
        cargarEscena("BitacoraPedidos", "Bitácora de pedidos");
    }

    @FXML
    private void clicConsultarFacturas(ActionEvent event) {
        cargarEscena("ListadoFacturas","Listado de facturas");
    }

    private void cargarEscena(String nombreFXML, String titulo) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML(nombreFXML);
            Parent vista = loader.load();

            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar pantalla",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }
}