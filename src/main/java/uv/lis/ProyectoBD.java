package uv.lis;

import javafx.application.Application;
import javafx.stage.Stage;

public class ProyectoBD extends Application {

    // El método start es obligatorio en JavaFX
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Control de Almacén - GLOBAL FINANCE");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}