package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.EmpleadoDAO;
import uv.lis.controlalmacen.modelo.dao.UsuarioDAO;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroUsuariosController implements Initializable {

    @FXML private ComboBox<Empleado> cbEmpleado;
    @FXML private ComboBox<String> cbRol;
    @FXML private TextField txtIdUsuario;
    @FXML private PasswordField pfPassword;
    @FXML private PasswordField pfConfirmar;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private static final Map<String, Integer> ROLES = new LinkedHashMap<>();
    static {
        ROLES.put("Usuario central", 1);
        ROLES.put("Usuario encargado", 2);
        ROLES.put("Usuario de salidas", 3);
        ROLES.put("Usuario de solicitudes", 4);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEmpleados();
        cbRol.setItems(FXCollections.observableArrayList(ROLES.keySet()));

        cbEmpleado.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getNombreCompleto());
            }
        });
        cbEmpleado.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Empleado e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getNombreCompleto());
            }
        });
    }

    private void cargarEmpleados() {
        try {
            List<Empleado> lista = empleadoDAO.buscarTodos();
            cbEmpleado.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar los empleados.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        Empleado empleado = cbEmpleado.getValue();
        String rolSeleccionado = cbRol.getValue();
        String idUsuario = txtIdUsuario.getText().trim();
        String password = pfPassword.getText();
        String confirmar = pfConfirmar.getText();

        if (empleado == null || rolSeleccionado == null || idUsuario.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos incompletos",
                    "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        if (!password.equals(confirmar)) {
            UtilidadesFX.mostrarAlertaSimple("Contraseñas no coinciden",
                    "La contraseña y su confirmación deben ser iguales.", Alert.AlertType.WARNING);
            pfPassword.clear();
            pfConfirmar.clear();
            return;
        }
        if (password.length() < 6) {
            UtilidadesFX.mostrarAlertaSimple("Contraseña débil",
                    "La contraseña debe tener al menos 6 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        try {
            byte[] hash = hashear(password);
            int idRol = ROLES.get(rolSeleccionado);
            usuarioDAO.registrar(idUsuario, hash, empleado.getNoEmpleado(), idRol);
            UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                    "El usuario \"" + idUsuario + "\" fue registrado correctamente.", Alert.AlertType.INFORMATION);
            ((Stage) txtIdUsuario.getScene().getWindow()).close();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al registrar",
                    "No se pudo registrar el usuario:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicLimpiar(ActionEvent event) {
        cbEmpleado.setValue(null);
        cbRol.setValue(null);
        txtIdUsuario.clear();
        pfPassword.clear();
        pfConfirmar.clear();
    }

    private byte[] hashear(String texto) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256")
                .digest(texto.getBytes(StandardCharsets.UTF_8));
    }
}
