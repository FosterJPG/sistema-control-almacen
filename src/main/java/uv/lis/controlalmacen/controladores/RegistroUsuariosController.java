package uv.lis.controlalmacen.controladores;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import uv.lis.controlalmacen.modelo.dao.UsuarioDAO;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.RolUsuario;
import uv.lis.controlalmacen.modelo.dto.Usuario;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroUsuariosController implements Initializable {

    @FXML
    private ComboBox<Empleado> cb_empleado;

    @FXML
    private ComboBox<RolUsuario> cb_rol;

    @FXML
    private TextField txt_correo;

    @FXML
    private TextField txt_usuario;

    @FXML
    private PasswordField pf_password;

    @FXML
    private PasswordField pf_confirmar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private final ObservableList<Empleado> empleadosBase = FXCollections.observableArrayList();
    private final ObservableList<RolUsuario> rolesBase = FXCollections.observableArrayList();

    private FilteredList<Empleado> empleadosFiltrados;
    private FilteredList<RolUsuario> rolesFiltrados;

    private boolean esEdicion = false;
    private boolean cargandoDatos = false;
    private Usuario usuarioEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txt_correo.setEditable(false);

        empleadosFiltrados = new FilteredList<>(empleadosBase, empleado -> true);
        rolesFiltrados = new FilteredList<>(rolesBase, rol -> true);

        cb_empleado.setItems(empleadosFiltrados);
        cb_rol.setItems(rolesFiltrados);

        configurarComboEmpleados();
        configurarComboRoles();

        configurarBusquedaEmpleados();
        configurarBusquedaRoles();

        configurarSeleccionEmpleado();

        cargarEmpleados();
        cargarRoles();
    }

    public void inicializarRegistro() {
        esEdicion = false;
        usuarioEdicion = null;

        txt_usuario.setEditable(true);
        limpiarCampos();
    }

    public void inicializarEdicion(Usuario usuario) {
        if (usuario == null) {
            return;
        }

        esEdicion = true;
        usuarioEdicion = usuario;

        limpiarCampos();

        txt_usuario.setText(usuario.getIdUsuario());
        txt_usuario.setEditable(false);

        seleccionarEmpleado(usuario);
        seleccionarRol(usuario);
    }

    private void configurarComboEmpleados() {
        cb_empleado.setConverter(new StringConverter<Empleado>() {
            @Override
            public String toString(Empleado empleado) {
                if (empleado == null) {
                    return "";
                }

                return obtenerNombreCompleto(empleado);
            }

            @Override
            public Empleado fromString(String texto) {
                return buscarEmpleadoPorTexto(texto);
            }
        });

        cb_empleado.setCellFactory(listView -> new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);

                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(obtenerNombreCompleto(empleado));
                }
            }
        });

        cb_empleado.setButtonCell(new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);

                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(obtenerNombreCompleto(empleado));
                }
            }
        });
    }

    private void configurarComboRoles() {
        cb_rol.setConverter(new StringConverter<RolUsuario>() {
            @Override
            public String toString(RolUsuario rolUsuario) {
                if (rolUsuario == null) {
                    return "";
                }

                return rolUsuario.getDescripcion();
            }

            @Override
            public RolUsuario fromString(String texto) {
                return buscarRolPorTexto(texto);
            }
        });

        cb_rol.setCellFactory(listView -> new ListCell<RolUsuario>() {
            @Override
            protected void updateItem(RolUsuario rolUsuario, boolean empty) {
                super.updateItem(rolUsuario, empty);

                if (empty || rolUsuario == null) {
                    setText(null);
                } else {
                    setText(rolUsuario.getDescripcion());
                }
            }
        });

        cb_rol.setButtonCell(new ListCell<RolUsuario>() {
            @Override
            protected void updateItem(RolUsuario rolUsuario, boolean empty) {
                super.updateItem(rolUsuario, empty);

                if (empty || rolUsuario == null) {
                    setText(null);
                } else {
                    setText(rolUsuario.getDescripcion());
                }
            }
        });
    }

    private void configurarBusquedaEmpleados() {
        cb_empleado.getEditor().textProperty().addListener((observable, textoAnterior, textoNuevo) -> {
            if (cargandoDatos) {
                return;
            }

            if (!cb_empleado.getEditor().isFocused()) {
                return;
            }

            Empleado empleadoSeleccionado = cb_empleado.getSelectionModel().getSelectedItem();

            if (empleadoSeleccionado != null
                    && obtenerNombreCompleto(empleadoSeleccionado).equals(textoNuevo)) {
                return;
            }

            filtrarEmpleados(textoNuevo);
        });
    }

    private void configurarBusquedaRoles() {
        cb_rol.getEditor().textProperty().addListener((observable, textoAnterior, textoNuevo) -> {
            if (cargandoDatos) {
                return;
            }

            if (!cb_rol.getEditor().isFocused()) {
                return;
            }

            RolUsuario rolSeleccionado = cb_rol.getSelectionModel().getSelectedItem();

            if (rolSeleccionado != null
                    && rolSeleccionado.getDescripcion().equals(textoNuevo)) {
                return;
            }

            filtrarRoles(textoNuevo);
        });
    }

    private void configurarSeleccionEmpleado() {
        cb_empleado.valueProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo == null) {
                txt_correo.clear();
                return;
            }

            cargarCorreoEmpleado(nuevo);

            if (!cargandoDatos) {
                mostrarAvisoUsuarioAsociadoConRetraso(nuevo);
            }
        });
    }

    private void filtrarEmpleados(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            empleadosFiltrados.setPredicate(empleado -> true);
        } else {
            String textoBuscado = texto.trim().toLowerCase();

            empleadosFiltrados.setPredicate(empleado ->
                    obtenerNombreCompleto(empleado).toLowerCase().contains(textoBuscado)
            );
        }

        if (!cb_empleado.isShowing()) {
            cb_empleado.show();
        }
    }

    private void filtrarRoles(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            rolesFiltrados.setPredicate(rol -> true);
        } else {
            String textoBuscado = texto.trim().toLowerCase();

            rolesFiltrados.setPredicate(rol ->
                    rol.getDescripcion().toLowerCase().contains(textoBuscado)
            );
        }

        if (!cb_rol.isShowing()) {
            cb_rol.show();
        }
    }

    private void cargarEmpleados() {
        try {
            List<Empleado> empleados = usuarioDAO.buscarEmpleados();

            empleadosBase.clear();
            empleadosBase.addAll(empleados);

            empleadosFiltrados.setPredicate(empleado -> true);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar empleados",
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

    private void cargarRoles() {
        try {
            List<RolUsuario> roles = usuarioDAO.buscarRoles();

            rolesBase.clear();
            rolesBase.addAll(roles);

            rolesFiltrados.setPredicate(rol -> true);

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

    private void cargarCorreoEmpleado(Empleado empleado) {
        if (empleado.getCorreoElectronico() == null) {
            txt_correo.clear();
            return;
        }

        txt_correo.setText(empleado.getCorreoElectronico());
    }

    private void mostrarAvisoUsuarioAsociadoConRetraso(Empleado empleado) {
        PauseTransition delay = new PauseTransition(Duration.millis(250));

        delay.setOnFinished(event -> {
            Platform.runLater(() -> verificarUsuarioAsociado(empleado));
        });
        delay.play();
    }

    private void verificarUsuarioAsociado(Empleado empleado) {
        if (empleado == null || empleado.getNoEmpleado() == null) {
            return;
        }

        if (esEdicion
                && usuarioEdicion != null
                && usuarioEdicion.getEmpleado() != null
                && usuarioEdicion.getEmpleado().getNoEmpleado().equals(empleado.getNoEmpleado())) {
            return;
        }

        try {
            if (usuarioDAO.empleadoTieneUsuario(empleado.getNoEmpleado())) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Empleado con usuario asociado",
                        "El empleado seleccionado ya tiene al menos un usuario asociado. Puede continuar si necesita asignarle otro rol.",
                        Alert.AlertType.WARNING
                );
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al verificar usuario",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al verificar usuario",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        Empleado empleado = obtenerEmpleadoSeleccionado();
        RolUsuario rol = obtenerRolSeleccionado();

        String idUsuario = obtenerTexto(txt_usuario);
        String password = obtenerTextoPassword(pf_password);
        String confirmar = obtenerTextoPassword(pf_confirmar);

        String mensajeValidacion = validarDatos(empleado, rol, idUsuario, password, confirmar);

        if (!mensajeValidacion.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Datos inválidos",
                    mensajeValidacion,
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            usuario.setEmpleado(empleado);
            usuario.setIdRol(rol.getIdRol());

            if (!password.isEmpty()) {
                usuario.setPassword(hashearPassword(password));
            }

            boolean exito;

            if (esEdicion) {
                exito = usuarioDAO.actualizar(usuario);
            } else {
                exito = usuarioDAO.registrar(usuario);
            }

            if (exito) {
                UtilidadesFX.mostrarAlertaSimple(
                        esEdicion ? "Actualización exitosa" : "Registro exitoso",
                        esEdicion
                                ? "El usuario se actualizó correctamente."
                                : "El usuario se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (NoSuchAlgorithmException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al hashear contraseña",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al guardar usuario",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al guardar usuario",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private String validarDatos(Empleado empleado, RolUsuario rol, String idUsuario,
                                String password, String confirmar) {

        StringBuilder mensaje = new StringBuilder();

        if (empleado == null) {
            mensaje.append("Seleccione un empleado existente.\n");
        }

        if (rol == null) {
            mensaje.append("Seleccione un rol existente.\n");
        }

        if (idUsuario.isEmpty()) {
            mensaje.append("Ingrese el identificador del usuario.\n");
        }

        if (!esEdicion && password.isEmpty()) {
            mensaje.append("Ingrese la contraseña.\n");
        }

        if (!password.isEmpty() || !confirmar.isEmpty()) {
            if (!password.equals(confirmar)) {
                mensaje.append("Las contraseñas no coinciden.\n");
            }

            if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                mensaje.append("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas y al menos un número.\n");
            }
        }

        return mensaje.toString();
    }

    @FXML
    private void clicLimpiar(ActionEvent event) {
        limpiarCampos();

        if (esEdicion && usuarioEdicion != null) {
            inicializarEdicion(usuarioEdicion);
        }
    }

    private void limpiarCampos() {
        cargandoDatos = true;

        empleadosFiltrados.setPredicate(empleado -> true);
        cb_empleado.getSelectionModel().clearSelection();
        cb_empleado.getEditor().clear();

        rolesFiltrados.setPredicate(rol -> true);
        cb_rol.getSelectionModel().clearSelection();
        cb_rol.getEditor().clear();

        txt_correo.clear();

        if (!esEdicion) {
            txt_usuario.clear();
        }

        pf_password.clear();
        pf_confirmar.clear();

        cargandoDatos = false;
    }

    private void seleccionarEmpleado(Usuario usuario) {
        if (usuario.getEmpleado() == null || usuario.getEmpleado().getNoEmpleado() == null) {
            return;
        }

        cargandoDatos = true;

        empleadosFiltrados.setPredicate(empleado -> true);

        Integer noEmpleado = usuario.getEmpleado().getNoEmpleado();

        for (Empleado empleado : empleadosBase) {
            if (empleado.getNoEmpleado().equals(noEmpleado)) {
                cb_empleado.getSelectionModel().select(empleado);
                cb_empleado.getEditor().setText(obtenerNombreCompleto(empleado));
                cargarCorreoEmpleado(empleado);
                break;
            }
        }

        cargandoDatos = false;
    }

    private void seleccionarRol(Usuario usuario) {
        if (usuario.getIdRol() == null) {
            return;
        }

        cargandoDatos = true;

        rolesFiltrados.setPredicate(rol -> true);

        for (RolUsuario rol : rolesBase) {
            if (rol.getIdRol().equals(usuario.getIdRol())) {
                cb_rol.getSelectionModel().select(rol);
                cb_rol.getEditor().setText(rol.getDescripcion());
                break;
            }
        }

        cargandoDatos = false;
    }

    private Empleado obtenerEmpleadoSeleccionado() {
        Empleado empleado = cb_empleado.getValue();

        if (empleado != null) {
            return empleado;
        }

        return buscarEmpleadoPorTexto(cb_empleado.getEditor().getText());
    }

    private RolUsuario obtenerRolSeleccionado() {
        RolUsuario rol = cb_rol.getValue();

        if (rol != null) {
            return rol;
        }

        return buscarRolPorTexto(cb_rol.getEditor().getText());
    }

    private Empleado buscarEmpleadoPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        String textoBuscado = texto.trim();

        for (Empleado empleado : empleadosBase) {
            if (obtenerNombreCompleto(empleado).equalsIgnoreCase(textoBuscado)) {
                return empleado;
            }
        }

        return null;
    }

    private RolUsuario buscarRolPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        String textoBuscado = texto.trim();

        for (RolUsuario rol : rolesBase) {
            if (rol.getDescripcion().equalsIgnoreCase(textoBuscado)) {
                return rol;
            }
        }

        return null;
    }

    private String obtenerNombreCompleto(Empleado empleado) {
        if (empleado == null) {
            return "";
        }

        String nombre = empleado.getNombre();
        String paterno = empleado.getPaterno();
        String materno = empleado.getMaterno();

        if (nombre == null) {
            nombre = "";
        }

        if (paterno == null) {
            paterno = "";
        }

        if (materno == null) {
            materno = "";
        }

        return (nombre + " " + paterno + " " + materno).trim();
    }

    private String obtenerTexto(TextField textField) {
        if (textField.getText() == null) {
            return "";
        }

        return textField.getText().trim();
    }

    private String obtenerTextoPassword(PasswordField passwordField) {
        if (passwordField.getText() == null) {
            return "";
        }

        return passwordField.getText();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txt_usuario.getScene().getWindow();
        stage.close();
    }

    private static byte[] hashearPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}