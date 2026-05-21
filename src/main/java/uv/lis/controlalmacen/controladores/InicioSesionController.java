package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.ControlAlmacen;
import uv.lis.controlalmacen.excepciones.UsuarioNoEncontradoException;
import uv.lis.controlalmacen.logica.Autenticador;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dto.Usuario;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField tf_usuario;
    @FXML
    private TextField tf_password;
    @FXML
    private Label lb_errorUsuario;
    @FXML
    private Label lb_errorPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void btnIniciarSesion(ActionEvent event) {
        String usuario = tf_usuario.getText();
        String password = tf_password.getText();
        if (!validarCampos(usuario, password)) {
            return;
        }

        Usuario usuarioLogin;

        try {
            usuarioLogin = Autenticador.iniciarSesion(usuario, password);
            UtilidadesFX.mostrarAlertaSimple("Bienvenido(a)", "Bienvenido al sistema: " + usuarioLogin.getNombreEmpleado()
                    , Alert.AlertType.INFORMATION);
            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(usuarioLogin.getRol());
            cargarEscena(rutaMenu);
        } catch (NoSuchAlgorithmException | SQLException | IOException | ClassNotFoundException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error", "Ocurrió un error al intentar iniciar sesion. " +
                    "Causa (Para el dev.): " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (UsuarioNoEncontradoException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos(String usuario, String password) {
        lb_errorUsuario.setText("");
        lb_errorPassword.setText("");

        boolean valido = true;
        if (usuario.trim().isEmpty()) {
            lb_errorUsuario.setText("Falta insertar usuario");
            valido = false;
        }

        if (password.trim().isEmpty()) {
            lb_errorPassword.setText("Falta insertar contraseña");
            valido = false;
        }

        return valido;
    }

    private void cargarEscena(String rutaMenu) {
        try {
            Parent vista = UtilidadesFX.cargarFXML(rutaMenu);
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tf_password.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Menu principal");
            stage.show();
            stage.setResizable(false);
            stage.centerOnScreen();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}