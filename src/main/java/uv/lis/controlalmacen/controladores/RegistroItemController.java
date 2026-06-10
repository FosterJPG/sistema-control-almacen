package uv.lis.controlalmacen.controladores;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uv.lis.controlalmacen.modelo.dao.ItemDAO;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

public class RegistroItemController implements Initializable {

    @FXML
    private TextField txt_codigo;

    @FXML
    private TextField txt_descripcion;

    @FXML
    private ComboBox<PartidaPresupuestal> cb_partidaPresupuestal;

    private final ItemDAO itemDAO = new ItemDAO();
    private final PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();

    private final ObservableList<PartidaPresupuestal> partidasPresupuestales =
            FXCollections.observableArrayList();

    private FilteredList<PartidaPresupuestal> partidasFiltradas;

    private boolean seleccionandoPartida = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarComboPartidasPresupuestales();
        cargarInformacionPartidasPresupuestales();
    }

    private void configurarComboPartidasPresupuestales() {
        cb_partidaPresupuestal.setEditable(true);

        partidasFiltradas = new FilteredList<>(partidasPresupuestales, partida -> true);
        cb_partidaPresupuestal.setItems(partidasFiltradas);

        cb_partidaPresupuestal.setConverter(new StringConverter<PartidaPresupuestal>() {
            @Override
            public String toString(PartidaPresupuestal partidaPresupuestal) {
                if (partidaPresupuestal == null) {
                    return "";
                }
                return partidaPresupuestal.getDescripcionPartida();
            }

            @Override
            public PartidaPresupuestal fromString(String descripcion) {
                return buscarPartidaPorDescripcionExacta(descripcion);
            }
        });

        cb_partidaPresupuestal.valueProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            if (valorNuevo == null) {
                return;
            }

            seleccionandoPartida = true;

            Platform.runLater(() -> {
                String descripcion = valorNuevo.getDescripcionPartida();
                cb_partidaPresupuestal.getEditor().setText(descripcion);
                cb_partidaPresupuestal.getEditor().positionCaret(descripcion.length());
                partidasFiltradas.setPredicate(partida -> true);
                seleccionandoPartida = false;
            });
        });

        cb_partidaPresupuestal.getEditor().textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            if (seleccionandoPartida) {
                return;
            }

            filtrarPartidasPresupuestales(valorNuevo);
        });
    }

    private void cargarInformacionPartidasPresupuestales() {
        try {
            List<PartidaPresupuestal> partidasPresupuestalesDB =
                    partidaPresupuestalDAO.buscarTodos();

            partidasPresupuestales.setAll(partidasPresupuestalesDB);

        } catch (SQLException | IOException | ClassNotFoundException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar",
                    "Lo sentimos, las partidas presupuestales no pueden ser cargadas en este momento, por favor inténtelo más tarde.",
                    Alert.AlertType.WARNING
            );
        }
    }

    private void filtrarPartidasPresupuestales(String textoBusqueda) {
        String texto = textoBusqueda == null ? "" : textoBusqueda.trim().toLowerCase();

        partidasFiltradas.setPredicate(partida -> {
            if (texto.isEmpty()) {
                return true;
            }

            return partida.getDescripcionPartida()
                    .toLowerCase()
                    .contains(texto);
        });

        if (cb_partidaPresupuestal.isFocused() && !partidasFiltradas.isEmpty()) {
            Platform.runLater(() -> {
                if (!cb_partidaPresupuestal.isShowing()) {
                    cb_partidaPresupuestal.show();
                }
            });
        }
    }

    private PartidaPresupuestal buscarPartidaPorDescripcionExacta(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return null;
        }

        String descripcionBuscada = descripcion.trim();

        for (PartidaPresupuestal partidaPresupuestal : partidasPresupuestales) {
            if (partidaPresupuestal.getDescripcionPartida().equalsIgnoreCase(descripcionBuscada)) {
                return partidaPresupuestal;
            }
        }

        return null;
    }

    private PartidaPresupuestal obtenerPartidaSeleccionada() {
        PartidaPresupuestal partidaSeleccionada = cb_partidaPresupuestal.getValue();
        String textoEditor = cb_partidaPresupuestal.getEditor().getText();

        if (partidaSeleccionada != null
                && textoEditor != null
                && partidaSeleccionada.getDescripcionPartida().equalsIgnoreCase(textoEditor.trim())) {
            return partidaSeleccionada;
        }

        return buscarPartidaPorDescripcionExacta(textoEditor);
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        String codigo = txt_codigo.getText().trim().toUpperCase();
        String descripcion = txt_descripcion.getText().trim();
        PartidaPresupuestal partidaSeleccionada = obtenerPartidaSeleccionada();

        if (codigo.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Datos incompletos",
                    "Ingrese el código del ítem.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (descripcion.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Datos incompletos",
                    "Ingrese la descripción del ítem.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (partidaSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Datos incompletos",
                    "Seleccione una partida presupuestal válida.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Item item = new Item();
        item.setIdItem(codigo);
        item.setDescripcionItem(descripcion);
        item.setCodigoPartidaPresupuestal(partidaSeleccionada.getCodigo());

        try {
            if (itemDAO.registrar(item)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Registro exitoso",
                        "El ítem se ha registrado en el catálogo correctamente.",
                        Alert.AlertType.INFORMATION
                );

                limpiarCampos();
            }

        } catch (SQLException | IOException | ClassNotFoundException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al registrar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar",
                    "Lo sentimos, el ítem no puede ser registrado en este momento, por favor inténtelo más tarde.",
                    Alert.AlertType.WARNING
            );
        }
    }

    private void limpiarCampos() {
        txt_codigo.clear();
        txt_descripcion.clear();

        seleccionandoPartida = true;

        cb_partidaPresupuestal.getSelectionModel().clearSelection();
        cb_partidaPresupuestal.setValue(null);
        cb_partidaPresupuestal.getEditor().clear();
        partidasFiltradas.setPredicate(partida -> true);

        seleccionandoPartida = false;
    }
        
    //MÉTODOS DE NAVEGABILIDAD
        
    @FXML
    private void clicCancelar(ActionEvent event) {
        try{
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);
            
            Stage stage = (Stage) txt_descripcion.getScene().getWindow();
            stage.setTitle("Menu Principal");
            stage.centerOnScreen();
            
            stage.setScene(escena);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
