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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.UsuarioDAO;
import uv.lis.controlalmacen.modelo.dto.RolUsuario;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Usuario;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;

public class ConsultarUsuariosController implements Initializable {

    @FXML
    private ComboBox<RolUsuario> cbRol;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Usuario> tvUsuarios;

    @FXML
    private TableColumn<Usuario, String> colIdUsuario;

    @FXML
    private TableColumn<Usuario, String> colNombre;

    @FXML
    private TableColumn<Usuario, String> colCorreo;

    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TableColumn<Usuario, String> colFechaRegistro;

    private ObservableList<Usuario> usuarios;
    private ObservableList<RolUsuario> roles;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnRegresar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarRoles();
        cargarInformacionUsuarios();

        cbRol.setOnAction(event -> actualizarInformacion());
    }

    private void configurarTabla() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompletoEmpleado"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoEmpleado"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("descripcionRol"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistroTexto"));
    }

    private void cargarRoles() {
        try {
            roles = FXCollections.observableArrayList();

            RolUsuario todos = new RolUsuario();
            todos.setIdRol(null);
            todos.setDescripcion("Todos los roles");
            roles.add(todos);

            List<RolUsuario> rolesBD = usuarioDAO.buscarRoles();
            roles.addAll(rolesBD);

            cbRol.setItems(roles);
            cbRol.getSelectionModel().select(todos);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar roles",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar roles",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void cargarInformacionUsuarios() {
        try {
            usuarios = FXCollections.observableArrayList();

            List<Usuario> usuariosBD = usuarioDAO.buscarTodos();
            usuarios.addAll(usuariosBD);

            tvUsuarios.setItems(usuarios);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar usuarios",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        actualizarInformacion();
    }

    private void actualizarInformacion() {
        String nombreEmpleado = obtenerTextoBusqueda();
        RolUsuario rolSeleccionado = cbRol.getValue();

        if (rolSeleccionado == null || rolSeleccionado.getIdRol() == null) {
            if (nombreEmpleado.isEmpty()) {
                cargarInformacionUsuarios();
            } else {
                buscarUsuariosPorNombreEmpleado(nombreEmpleado);
            }
            return;
        }

        if (nombreEmpleado.isEmpty()) {
            buscarUsuariosPorRol(rolSeleccionado.getDescripcion());
        } else {
            buscarUsuariosPorNombreEmpleadoYRol(nombreEmpleado, rolSeleccionado.getDescripcion());
        }
    }

    private void buscarUsuariosPorNombreEmpleado(String nombreEmpleado) {
        try {
            usuarios = FXCollections.observableArrayList();

            List<Usuario> usuariosBD = usuarioDAO.buscarPorNombreEmpleado(nombreEmpleado);
            usuarios.addAll(usuariosBD);

            tvUsuarios.setItems(usuarios);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al buscar usuarios",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void buscarUsuariosPorRol(String descripcionRol) {
        try {
            usuarios = FXCollections.observableArrayList();

            List<Usuario> usuariosBD = usuarioDAO.buscarPorRol(descripcionRol);
            usuarios.addAll(usuariosBD);

            tvUsuarios.setItems(usuarios);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al filtrar usuarios",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void buscarUsuariosPorNombreEmpleadoYRol(String nombreEmpleado, String descripcionRol) {
        try {
            usuarios = FXCollections.observableArrayList();

            List<Usuario> usuariosBD = usuarioDAO.buscarPorNombreEmpleadoYRol(nombreEmpleado, descripcionRol);
            usuarios.addAll(usuariosBD);

            tvUsuarios.setItems(usuarios);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al filtrar usuarios",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioUsuario(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Usuario usuarioSeleccionado = tvUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un usuario para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioUsuario(usuarioSeleccionado, true);
    }

    private void abrirFormularioUsuario(Usuario usuario, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroUsuarios");
            Parent vista = loader.load();

            RegistroUsuariosController controller = loader.getController();

            if (esEdicion) {
                controller.inicializarEdicion(usuario);
            } else {
                controller.inicializarRegistro();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Usuario" : "Registrar Usuario");
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
        Usuario usuarioSeleccionado = tvUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un usuario para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar el usuario \"" + usuarioSeleccionado.getIdUsuario() + "\"?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (usuarioDAO.eliminar(usuarioSeleccionado)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "El usuario se eliminó correctamente.",
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
                    "Error al eliminar usuario",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
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

            Stage stage = (Stage) tvUsuarios.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(new Scene(vista));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String obtenerTextoBusqueda() {
        if (txtBuscar.getText() == null) {
            return "";
        }

        return txtBuscar.getText().trim();
    }
}