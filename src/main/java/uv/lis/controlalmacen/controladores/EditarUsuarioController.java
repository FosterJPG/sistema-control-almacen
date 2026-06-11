package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.UsuarioDAO;
import uv.lis.controlalmacen.modelo.dto.Usuario;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EditarUsuarioController implements Initializable {

    @FXML private Label lblIdUsuario;
    @FXML private Label lblNombre;
    @FXML private ComboBox<String> cbRol;
    @FXML private PasswordField pfPassword;
    @FXML private PasswordField pfConfirmar;
    @FXML private Label lblError;

    private Usuario usuario;
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
        cbRol.setItems(FXCollections.observableArrayList(ROLES.keySet()));
    }

    public void cargarUsuario(Usuario usuario) {
        this.usuario = usuario;
        lblIdUsuario.setText(usuario.getIdUsuario());
        lblNombre.setText(usuario.getEmpleado() != null ? usuario.getEmpleado().getNombreCompleto() : "");

        String rolActual = ROLES.entrySet().stream()
                .filter(e -> e.getValue().equals(usuario.getIdRol()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
        cbRol.setValue(rolActual);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        lblError.setText("");

        if (cbRol.getValue() == null) {
            lblError.setText("Selecciona un rol.");
            return;
        }

        String nuevaPassword = pfPassword.getText();
        String confirmar = pfConfirmar.getText();

        if (!nuevaPassword.isEmpty()) {
            if (!nuevaPassword.equals(confirmar)) {
                lblError.setText("Las contraseñas no coinciden.");
                pfPassword.clear();
                pfConfirmar.clear();
                return;
            }
            if (nuevaPassword.length() < 6) {
                lblError.setText("La contraseña debe tener al menos 6 caracteres.");
                return;
            }
        }

        try {
            int idRol = ROLES.get(cbRol.getValue());
            byte[] hash = nuevaPassword.isEmpty() ? null : hashear(nuevaPassword);

            usuarioDAO.actualizar(usuario.getIdUsuario(), hash, idRol);

            UtilidadesFX.mostrarAlertaSimple("Actualización exitosa",
                    "El usuario \"" + usuario.getIdUsuario() + "\" fue actualizado correctamente.",
                    Alert.AlertType.INFORMATION);
            cerrar();
        } catch (NoSuchAlgorithmException e) {
            lblError.setText("Error al cifrar la contraseña.");
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al actualizar",
                    "No se pudo actualizar el usuario:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) lblIdUsuario.getScene().getWindow()).close();
    }

    private byte[] hashear(String texto) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256")
                .digest(texto.getBytes(StandardCharsets.UTF_8));
    }
}
