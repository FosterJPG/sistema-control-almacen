package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.logica.CargadorEscenas;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoPartidasController implements Initializable {

    @FXML
    private TextField txt_buscar;
    @FXML
    private TableView<PartidaPresupuestal> tv_listado;
    @FXML
    private TableColumn<PartidaPresupuestal, String> col_descripcion;

    private ObservableList<PartidaPresupuestal> partidasPresupuestales;
    private final PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarInformacionPartidas();
    }

    private void configurarTabla() {
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionPartida"));
    }

    private void cargarInformacionPartidas() {
        try {
            partidasPresupuestales = FXCollections.observableArrayList();

            List<PartidaPresupuestal> partidasBD = partidaPresupuestalDAO.buscarTodos();
            partidasPresupuestales.addAll(partidasBD);

            tv_listado.setItems(partidasPresupuestales);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar partidas presupuestales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String descripcion = obtenerTextoBusqueda();

        if (descripcion.isEmpty()) {
            cargarInformacionPartidas();
            return;
        }

        buscarPartidasPorDescripcion(descripcion);
    }

    private void buscarPartidasPorDescripcion(String descripcion) {
        try {
            partidasPresupuestales = FXCollections.observableArrayList();

            List<PartidaPresupuestal> partidasBD =
                    partidaPresupuestalDAO.buscarPorNombre(descripcion);

            partidasPresupuestales.addAll(partidasBD);
            tv_listado.setItems(partidasPresupuestales);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al buscar partidas presupuestales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioPartida(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        PartidaPresupuestal partidaSeleccionada = tv_listado.getSelectionModel().getSelectedItem();

        if (partidaSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione una partida presupuestal para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioPartida(partidaSeleccionada, true);
    }

    private void abrirFormularioPartida(PartidaPresupuestal partidaPresupuestal, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroPartida");
            Parent vista = loader.load();

            //RegistroPartidaController controller = loader.getController();

            if (esEdicion) {
                //controller.inicializarEdicion(partidaPresupuestal);
            } else {
                //controller.inicializarRegistro();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Partida Presupuestal" : "Registrar Partida Presupuestal");
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            actualizarInformacion();

        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al abrir formulario",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        PartidaPresupuestal partidaSeleccionada = tv_listado.getSelectionModel().getSelectedItem();

        if (partidaSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione una partida presupuestal para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar la partida \"" +
                        partidaSeleccionada.getDescripcionPartida() + "\"?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (partidaPresupuestalDAO.eliminar(partidaSeleccionada)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "La partida presupuestal se eliminó correctamente.",
                        Alert.AlertType.INFORMATION
                );

                actualizarInformacion();
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al eliminar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al eliminar partida presupuestal",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        String descripcion = obtenerTextoBusqueda();

        if (descripcion.isEmpty()) {
            cargarInformacionPartidas();
        } else {
            buscarPartidasPorDescripcion(descripcion);
        }
    }

    private String obtenerTextoBusqueda() {
        if (txt_buscar.getText() == null) {
            return "";
        }

        return txt_buscar.getText().trim();
    }

    @FXML
    private void clicVerTodos(ActionEvent event) {
        cargarInformacionPartidas();
    }


    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(Sesion.getUsuarioActual().getRol());
            FXMLLoader loader = UtilidadesFX.cargarFXML(rutaMenu);
            Parent vista = loader.load();

            MenuController controller = loader.getController();
            controller.cargarDatos();

            Stage stage = (Stage) tv_listado.getScene().getWindow();
            stage.setTitle("Menú principal");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setScene(new Scene(vista));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}