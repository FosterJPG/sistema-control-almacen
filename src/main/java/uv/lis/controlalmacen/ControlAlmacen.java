package uv.lis.controlalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;

public class ControlAlmacen extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/InicioSesion.fxml"));

        scene = new Scene(root);

        String css = this.getClass().getResource("/css/estilos.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Control de Almacén - GLOBAL FINANCE");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}