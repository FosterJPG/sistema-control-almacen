package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.SucursalDAO;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegistroSucursalController implements Initializable {

    @FXML
    private Label lbl_titulo;

    @FXML
    private TextField txt_nombre;

    @FXML
    private TextField txt_direccion;

    @FXML
    private TextField txt_telefono;

    @FXML
    private Label lbl_mensajeError;

    private final SucursalDAO sucursalDAO = new SucursalDAO();

    private boolean esEdicion = false;
    private Sucursal sucursalEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_titulo.setText("Registrar Sucursal");
        lbl_mensajeError.setWrapText(true);
        lbl_mensajeError.setText("");
    }

    public void inicializarRegistro() {
        esEdicion = false;
        sucursalEdicion = null;

        lbl_titulo.setText("Registrar Sucursal");
        limpiarCampos();
    }

    public void inicializarEdicion(Sucursal sucursal) {
        if (sucursal == null) {
            return;
        }

        esEdicion = true;
        sucursalEdicion = sucursal;

        lbl_titulo.setText("Modificar Sucursal");
        txt_nombre.setText(sucursal.getNombre());
        txt_direccion.setText(sucursal.getDireccion());
        txt_telefono.setText(sucursal.getTelefono());
        lbl_mensajeError.setText("");
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        lbl_mensajeError.setText("");

        String nombre = obtenerTexto(txt_nombre);
        String direccion = obtenerTexto(txt_direccion);
        String telefono = obtenerTexto(txt_telefono);

        String mensajeValidacion = validarDatos(nombre, direccion, telefono);

        if (!mensajeValidacion.isEmpty()) {
            lbl_mensajeError.setText(mensajeValidacion);
            return;
        }

        if (esEdicion) {
            guardarEdicion(nombre, direccion, telefono);
        } else {
            guardarRegistro(nombre, direccion, telefono);
        }
    }

    private void guardarRegistro(String nombre, String direccion, String telefono) {
        try {
            Sucursal sucursal = new Sucursal();
            sucursal.setNombre(nombre);
            sucursal.setDireccion(direccion);
            sucursal.setTelefono(telefono);

            if (sucursalDAO.registrar(sucursal)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Registro exitoso",
                        "La sucursal se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());

        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al registrar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void guardarEdicion(String nombre, String direccion, String telefono) {
        if (sucursalEdicion == null || sucursalEdicion.getNoSucursal() == null) {
            lbl_mensajeError.setText("No hay una sucursal seleccionada para modificar.");
            return;
        }

        try {
            Sucursal sucursal = new Sucursal();
            sucursal.setNoSucursal(sucursalEdicion.getNoSucursal());
            sucursal.setNombre(nombre);
            sucursal.setDireccion(direccion);
            sucursal.setTelefono(telefono);

            if (sucursalDAO.actualizar(sucursal)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Actualización exitosa",
                        "La sucursal se actualizó correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());

        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al modificar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private String validarDatos(String nombre, String direccion, String telefono) {
        StringBuilder mensaje = new StringBuilder();

        if (nombre.isEmpty()) {
            mensaje.append("Ingrese el nombre de la sucursal.\n");
        }

        if (direccion.isEmpty()) {
            mensaje.append("Ingrese la dirección de la sucursal.\n");
        }

        if (telefono.isEmpty()) {
            mensaje.append("Ingrese el teléfono de la sucursal.\n");
        }

        return mensaje.toString();
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private String obtenerTexto(TextField textField) {
        if (textField.getText() == null) {
            return "";
        }

        return textField.getText().trim();
    }

    private void limpiarCampos() {
        txt_nombre.clear();
        txt_direccion.clear();
        txt_telefono.clear();
        lbl_mensajeError.setText("");
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txt_nombre.getScene().getWindow();
        stage.close();
    }
}