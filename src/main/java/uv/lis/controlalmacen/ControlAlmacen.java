package uv.lis.controlalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControlAlmacen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/InicioSesion.fxml"));
        
        Scene scene = new Scene(root, 600, 400);

        String css = this.getClass().getResource("/css/estilos.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setTitle("Control de Almacén - GLOBAL FINANCE");
        primaryStage.setScene(scene); // ¡Esta línea es crucial para que se vea el contenido!
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}