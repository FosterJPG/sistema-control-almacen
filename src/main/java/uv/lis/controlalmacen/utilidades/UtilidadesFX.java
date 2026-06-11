package uv.lis.controlalmacen.utilidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import uv.lis.controlalmacen.ControlAlmacen;

import java.io.IOException;
import java.util.Optional;

public class UtilidadesFX {


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

    public static boolean mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    public static Optional<String> mostrarAlertaEntradaTexto(String titulo, String mensaje, String prompt) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefWidth(300);

        alerta.getDialogPane().setContent(textField);

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            return Optional.of(textField.getText());
        }

        return Optional.empty();
    }
}
