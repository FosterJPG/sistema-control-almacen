package uv.lis.controlalmacen.utilidades;

import javafx.scene.control.Alert;

public class UtilidadesFX {

    /**
     * Metodo para crear ventanas genericas de error, advertencia o confirmacion.
     * @param titulo
     * @param mensaje
     * @param tipo
     */
    public static void mostrarAlertaSimple(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
