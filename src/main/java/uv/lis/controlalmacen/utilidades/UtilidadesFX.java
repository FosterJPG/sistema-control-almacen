package uv.lis.controlalmacen.utilidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import uv.lis.controlalmacen.ControlAlmacen;

import java.io.IOException;

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

    /**
     * Metodo que retorna un el archivo FXML cargado. Este metodo se encarga de manejar la ruta.
     * @param fxml nombre del archivo a cargar
     * @return
     * @throws IOException
     */
    public static FXMLLoader cargarFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ControlAlmacen.class.getResource("/fxml/"+ fxml + ".fxml"));
        return fxmlLoader;
    }
}
