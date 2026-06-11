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
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistroPartidaController implements Initializable {

    @FXML private Label lbl_titulo;
    @FXML private TextField txt_descripcion;

    private PartidaPresupuestal partidaEditar;
    private final PartidaPresupuestalDAO dao = new PartidaPresupuestalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setPartida(PartidaPresupuestal partida) {
        this.partidaEditar = partida;
        lbl_titulo.setText("Modificar Partida Presupuestal");
        txt_descripcion.setText(partida.getDescripcionPartida());
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        String descripcion = txt_descripcion.getText().trim();
        if (descripcion.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campo vacío", "La descripción no puede estar vacía.", Alert.AlertType.WARNING);
            return;
        }
        try {
            if (partidaEditar == null) {
                PartidaPresupuestal nueva = new PartidaPresupuestal();
                nueva.setDescripcionPartida(descripcion);
                dao.registrar(nueva);
                UtilidadesFX.mostrarAlertaSimple("Registrada", "Partida registrada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                partidaEditar.setDescripcionPartida(descripcion);
                dao.actualizar(partidaEditar);
                UtilidadesFX.mostrarAlertaSimple("Actualizada", "Partida actualizada correctamente.", Alert.AlertType.INFORMATION);
            }
            cerrar();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudo guardar la partida:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txt_descripcion.getScene().getWindow()).close();
    }
}
