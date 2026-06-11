package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegistroPartidaController implements Initializable {

    @FXML
    private Label lbl_titulo;
    @FXML
    private TextField txt_descripcion;
    @FXML
    private Label lbl_mensajeError;

    private final PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();

    private boolean esEdicion = false;
    private PartidaPresupuestal partidaEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_mensajeError.setWrapText(true);
        lbl_mensajeError.setText("");
    }

    public void inicializarRegistro() {
        esEdicion = false;
        partidaEdicion = null;

        lbl_titulo.setText("Registrar Partida Presupuestal");
        txt_descripcion.clear();
        lbl_mensajeError.setText("");
    }

    public void inicializarEdicion(PartidaPresupuestal partidaPresupuestal) {
        if (partidaPresupuestal == null) {
            return;
        }

        esEdicion = true;
        partidaEdicion = partidaPresupuestal;

        lbl_titulo.setText("Modificar Partida Presupuestal");
        txt_descripcion.setText(partidaPresupuestal.getDescripcionPartida());
        lbl_mensajeError.setText("");
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        lbl_mensajeError.setText("");

        String descripcion = obtenerDescripcion();

        if (descripcion.isEmpty()) {
            lbl_mensajeError.setText("Ingrese la descripción de la partida presupuestal.");
            return;
        }

        if (esEdicion) {
            guardarEdicion(descripcion);
        } else {
            guardarRegistro(descripcion);
        }
    }

    private void guardarRegistro(String descripcion) {
        try {
            PartidaPresupuestal partidaPresupuestal = new PartidaPresupuestal();
            partidaPresupuestal.setDescripcionPartida(descripcion);

            if (partidaPresupuestalDAO.registrar(partidaPresupuestal)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Registro exitoso",
                        "La partida presupuestal se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                ((Stage) txt_descripcion.getScene().getWindow()).close();
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al registrar",
                    ex.getMessage(),
                    Alert.AlertType.WARNING
            );

        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar partida a registrar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void guardarEdicion(String descripcion) {
        if (partidaEdicion == null || partidaEdicion.getCodigo() == null) {
            lbl_mensajeError.setText("No hay una partida presupuestal seleccionada para modificar.");
            return;
        }

        try {
            PartidaPresupuestal partidaPresupuestal = new PartidaPresupuestal();
            partidaPresupuestal.setCodigo(partidaEdicion.getCodigo());
            partidaPresupuestal.setDescripcionPartida(descripcion);

            if (partidaPresupuestalDAO.actualizar(partidaPresupuestal)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Actualización exitosa",
                        "La partida presupuestal se actualizó correctamente.",
                        Alert.AlertType.INFORMATION
                );

                ((Stage) txt_descripcion.getScene().getWindow()).close();
            }

        }catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al editar",
                    ex.getMessage(),
                    Alert.AlertType.WARNING
            );

        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar partida a editar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage) txt_descripcion.getScene().getWindow()).close();
    }

    private String obtenerDescripcion() {
        if (txt_descripcion.getText() == null) {
            return "";
        }

        return txt_descripcion.getText().trim();
    }
}