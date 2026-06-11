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
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistroSucursalController implements Initializable {

    @FXML private Label lbl_titulo;
    @FXML private TextField txt_nombre;
    @FXML private TextField txt_direccion;
    @FXML private TextField txt_telefono;

    private Sucursal sucursalEditar;
    private final SucursalDAO dao = new SucursalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setSucursal(Sucursal sucursal) {
        this.sucursalEditar = sucursal;
        lbl_titulo.setText("Modificar Sucursal");
        txt_nombre.setText(sucursal.getNombre());
        txt_direccion.setText(sucursal.getDireccion() != null ? sucursal.getDireccion() : "");
        txt_telefono.setText(sucursal.getTelefono() != null ? sucursal.getTelefono() : "");
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        String nombre    = txt_nombre.getText().trim();
        String direccion = txt_direccion.getText().trim();
        String telefono  = txt_telefono.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos incompletos",
                    "El nombre y la dirección son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        if (!telefono.isEmpty() && !telefono.matches("\\d{10}")) {
            UtilidadesFX.mostrarAlertaSimple("Teléfono inválido",
                    "El teléfono debe tener exactamente 10 dígitos numéricos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Sucursal s = sucursalEditar != null ? sucursalEditar : new Sucursal();
            s.setNombre(nombre);
            s.setDireccion(direccion);
            s.setTelefono(telefono.isEmpty() ? null : telefono);

            if (sucursalEditar == null) {
                dao.registrar(s);
                UtilidadesFX.mostrarAlertaSimple("Registrada", "Sucursal registrada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                dao.actualizar(s);
                UtilidadesFX.mostrarAlertaSimple("Actualizada", "Sucursal actualizada correctamente.", Alert.AlertType.INFORMATION);
            }
            cerrar();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudo guardar la sucursal:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txt_nombre.getScene().getWindow()).close();
    }
}
