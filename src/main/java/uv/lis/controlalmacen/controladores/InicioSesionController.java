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
    private TextField tfUsuario;
    @FXML
    private TextField tfPassword;
    @FXML
    private Label lbErrorUsuario;
    @FXML
    private Label lbErrorPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void btnIniciarSesion(ActionEvent event) {
        String usuario = tfUsuario.getText();
        String password = tfPassword.getText();
        if (!validarCampos(usuario, password)) {
            return;
        }

        Usuario usuarioLogin;

        try {
            usuarioLogin = Autenticador.iniciarSesion(usuario, password);
            UtilidadesFX.mostrarAlertaSimple("Bienvenido(a)", "Bienvenido al sistema: " + usuarioLogin.getNombreEmpleado()
                    , Alert.AlertType.INFORMATION);
            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(usuarioLogin.getRol());
            System.out.println(rutaMenu);
            cargarEscena(rutaMenu);
        } catch (NoSuchAlgorithmException | SQLException | IOException | ClassNotFoundException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error", "Ocurrió un error al intentar iniciar sesion. " +
                    "Causa (Para el dev.): " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (UsuarioNoEncontradoException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos(String usuario, String password) {
        lbErrorUsuario.setText("");
        lbErrorPassword.setText("");

        boolean valido = true;
        if (usuario.trim().isEmpty()) {
            lbErrorUsuario.setText("Falta insertar usuario");
            valido = false;
        }

        if (password.trim().isEmpty()) {
            lbErrorPassword.setText("Falta insertar contraseña");
            valido = false;
        }

        return valido;
    }

    private void cargarEscena(String rutaMenu) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaMenu));
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tfPassword.getScene().getWindow();
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