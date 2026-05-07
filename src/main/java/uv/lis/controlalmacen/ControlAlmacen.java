package uv.lis.controlalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControlAlmacen extends Application {

    // El método start es obligatorio en JavaFX
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/InicioSesion.fxml"));
        Scene scene = new Scene(root);

        // Cargar el CSS
        String css = this.getClass().getResource("/css/estilos.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setTitle("Control de Almacén - GLOBAL FINANCE");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}