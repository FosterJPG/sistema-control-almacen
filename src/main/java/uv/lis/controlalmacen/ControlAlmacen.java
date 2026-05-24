package uv.lis.controlalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.util.logging.Logger;

public class ControlAlmacen extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("InicioSesion");
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("Control de Almacén - GLOBAL FINANCE");
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.setScene(scene);

            String css = this.getClass().getResource("/css/estilos.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}