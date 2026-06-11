package uv.lis.controlalmacen.controladores;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.UsuarioDAO;
import uv.lis.controlalmacen.modelo.dto.Rol;
import uv.lis.controlalmacen.modelo.dto.Usuario;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultarUsuariosController implements Initializable {

    @FXML private ComboBox<String> cbRol;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, String> colIdUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colFechaRegistro;

    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Usuario> listaFiltrada;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarFiltros();
        cargarUsuarios();
    }

    private void configurarTabla() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(cellData -> {
            Usuario u = cellData.getValue();
            if (u.getEmpleado() == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(u.getEmpleado().getNombreCompleto());
        });
        colCorreo.setCellValueFactory(cellData -> {
            Usuario u = cellData.getValue();
            if (u.getEmpleado() == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(u.getEmpleado().getCorreoElectronico() != null
                    ? u.getEmpleado().getCorreoElectronico() : "");
        });
        colRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(descripcionRol(cellData.getValue().getRol())));
        colFechaRegistro.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaRegistro();
            return new SimpleStringProperty(fecha != null ? SDF.format(fecha) : "");
        });
        tvUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        listaFiltrada = new FilteredList<>(listaUsuarios, u -> true);
        tvUsuarios.setItems(listaFiltrada);
    }

    private void configurarFiltros() {
        cbRol.setItems(FXCollections.observableArrayList(
                "Todos los roles",
                "Usuario central",
                "Usuario encargado",
                "Usuario de salidas",
                "Usuario de solicitudes"
        ));
        cbRol.setValue("Todos los roles");
        cbRol.valueProperty().addListener((obs, ant, nuevo) -> aplicarFiltro());
        txtBuscar.textProperty().addListener((obs, ant, nuevo) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String rolSeleccionado = cbRol.getValue();
        String texto = txtBuscar.getText();
        listaFiltrada.setPredicate(u -> {
            boolean pasaRol = rolSeleccionado == null || rolSeleccionado.equals("Todos los roles")
                    || descripcionRol(u.getRol()).equals(rolSeleccionado);
            boolean pasaTexto = texto == null || texto.isBlank()
                    || (u.getIdUsuario() != null && u.getIdUsuario().toLowerCase().contains(texto.toLowerCase()))
                    || (u.getEmpleado() != null && u.getEmpleado().getNombreCompleto().toLowerCase().contains(texto.toLowerCase()));
            return pasaRol && pasaTexto;
        });
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> lista = usuarioDAO.buscarTodos();
            listaUsuarios.setAll(lista);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "No se pudieron cargar los usuarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        aplicarFiltro();
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroUsuarios");
            Parent vista = loader.load();
            Stage modal = new Stage();
            modal.setTitle("Registrar Usuario");
            modal.setResizable(false);
            modal.setScene(new Scene(vista));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(tvUsuarios.getScene().getWindow());
            modal.centerOnScreen();
            modal.showAndWait();
            cargarUsuarios();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        UtilidadesFX.mostrarAlertaSimple("No disponible",
                "La modificación de usuarios no está habilitada en esta versión.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Usuario seleccionado = tvUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "Selecciona un usuario de la tabla para eliminarlo.", Alert.AlertType.WARNING);
            return;
        }
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion("Eliminar usuario",
                "¿Eliminar al usuario \"" + seleccionado.getIdUsuario() + "\"?\nEsta acción no se puede deshacer.");
        if (!confirmar) return;
        try {
            usuarioDAO.eliminar(seleccionado.getIdUsuario());
            listaUsuarios.remove(seleccionado);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    "No se pudo eliminar el usuario:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalCentral");
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Stage stage = (Stage) tvUsuarios.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String descripcionRol(Rol rol) {
        if (rol == null) return "Desconocido";
        return switch (rol) {
            case CENTRAL -> "Usuario central";
            case ENCARGADO -> "Usuario encargado";
            case SALIDAS -> "Usuario de salidas";
            case SOLICITUDES -> "Usuario de solicitudes";
        };
    }
}
