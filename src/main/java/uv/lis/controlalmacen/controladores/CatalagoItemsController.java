package uv.lis.controlalmacen.controladores;

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
import javafx.util.StringConverter;
import uv.lis.controlalmacen.modelo.dao.ItemDAO;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

public class CatalagoItemsController implements Initializable {

    @FXML
    private TableView<Item> tv_listado;
    @FXML
    private TableColumn col_idItem;
    @FXML
    private TableColumn col_descripcion;
    @FXML
    private TableColumn col_partida;
    @FXML
    private ComboBox<String> cb_filtro;
    @FXML
    private TextField txt_buscar;
    @FXML
    private ComboBox<PartidaPresupuestal> cb_partidaPresupuestal;

    private ObservableList<Item> items;
    private ObservableList<PartidaPresupuestal> partidasPresupuestales;
    private FilteredList<PartidaPresupuestal> partidasFiltradas;

    private ObservableList<String> opcionesBusqueda = FXCollections.observableArrayList(
            "Mostrar todos",
            "Código",
            "Descripción",
            "Partida presupuestal"
    );

    private String filtroBusqueda;

    private final ItemDAO itemDAO = new ItemDAO();
    private final PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();

    private boolean seleccionandoPartida = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filtroBusqueda = "";

        cb_filtro.setItems(opcionesBusqueda);

        configurarTabla();
        configurarComboPartidas();
        cargarInformacionPartidas();
        cargarInformacionItems();
        configurarSeleccionFiltro();
        configurarSeleccionPartida();

        cb_filtro.getSelectionModel().select("Mostrar todos");
    }

    private void configurarTabla() {
        col_idItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionItem"));
        col_partida.setCellValueFactory(new PropertyValueFactory<>("descripcionPartida"));
    }

    private void configurarComboPartidas() {
        partidasPresupuestales = FXCollections.observableArrayList();
        partidasFiltradas = new FilteredList<>(partidasPresupuestales, partida -> true);

        cb_partidaPresupuestal.setEditable(true);
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
                return buscarPartidaPorDescripcion(descripcion);
            }
        });

        cb_partidaPresupuestal.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (seleccionandoPartida) {
                return;
            }

            filtrarComboPartidas(newValue);
        });
    }

    private void filtrarComboPartidas(String textoBusqueda) {
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
            cb_partidaPresupuestal.show();
        }
    }

    private PartidaPresupuestal buscarPartidaPorDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return null;
        }

        String descripcionBuscada = descripcion.trim();

        for (PartidaPresupuestal partida : partidasPresupuestales) {
            if (partida.getDescripcionPartida().equalsIgnoreCase(descripcionBuscada)) {
                return partida;
            }
        }

        return null;
    }

    private void cargarInformacionPartidas() {
        try {
            List<PartidaPresupuestal> partidasBD = partidaPresupuestalDAO.buscarTodos();

            partidasPresupuestales.clear();
            partidasPresupuestales.addAll(partidasBD);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar",
                    "Lo sentimos, las partidas presupuestales no pueden ser cargadas en este momento, por favor inténtelo más tarde.",
                    Alert.AlertType.WARNING
            );
        }
    }

    private void cargarInformacionItems() {
        try {
            items = FXCollections.observableArrayList();

            List<Item> itemsBD = itemDAO.buscarTodos();
            items.addAll(itemsBD);

            tv_listado.setItems(items);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar",
                    "Lo sentimos, los ítems del catálogo no pueden ser cargados en este momento, por favor inténtelo más tarde.",
                    Alert.AlertType.WARNING
            );
        }
    }

    private void configurarSeleccionFiltro() {
        cb_filtro.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Mostrar todos")) {
                    filtroBusqueda = "";
                    txt_buscar.clear();
                    limpiarSeleccionPartida();
                    cargarInformacionItems();

                } else if (newValue.equals("Partida presupuestal")) {
                    filtroBusqueda = newValue;
                    txt_buscar.clear();
                    txt_buscar.setDisable(true);
                    cb_partidaPresupuestal.setDisable(false);

                } else {
                    filtroBusqueda = newValue;
                    txt_buscar.setDisable(false);
                    limpiarSeleccionPartida();
                }
            }
        });
    }

    private void configurarSeleccionPartida() {
        cb_partidaPresupuestal.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filtroBusqueda = "Partida presupuestal";
                cb_filtro.getSelectionModel().select("Partida presupuestal");

                seleccionandoPartida = true;
                cb_partidaPresupuestal.getEditor().setText(newValue.getDescripcionPartida());
                cb_partidaPresupuestal.getEditor().positionCaret(newValue.getDescripcionPartida().length());
                partidasFiltradas.setPredicate(partida -> true);
                seleccionandoPartida = false;

                cargarItemsPorPartida(newValue);
            }
        });
    }

    private void limpiarSeleccionPartida() {
        seleccionandoPartida = true;

        cb_partidaPresupuestal.getSelectionModel().clearSelection();
        cb_partidaPresupuestal.setValue(null);
        cb_partidaPresupuestal.getEditor().clear();

        if (partidasFiltradas != null) {
            partidasFiltradas.setPredicate(partida -> true);
        }

        seleccionandoPartida = false;
    }

    private void cargarItemsPorPartida(PartidaPresupuestal partidaSeleccionada) {
        if (partidaSeleccionada == null) {
            return;
        }

        try {
            items = FXCollections.observableArrayList();

            List<Item> itemsBD = itemDAO.buscarPorPartida(partidaSeleccionada.getCodigo());
            items.addAll(itemsBD);

            tv_listado.setItems(items);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar los items del catálogo",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        if (filtroBusqueda.equals("")) {
            cargarInformacionItems();
        } else if (filtroBusqueda.equals("Partida presupuestal")) {
            PartidaPresupuestal partidaSeleccionada = cb_partidaPresupuestal.getValue();

            if (partidaSeleccionada != null) {
                cargarItemsPorPartida(partidaSeleccionada);
            } else {
                cargarInformacionItems();
            }
        } else {
            buscarPorFiltro();
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        buscarPorFiltro();
    }

    private void buscarPorFiltro() {
        String campoBuscar = txt_buscar.getText() == null
                ? ""
                : txt_buscar.getText().trim();

        items = FXCollections.observableArrayList();

        if (filtroBusqueda == null || filtroBusqueda.equals("")) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin filtro",
                    "Por favor seleccione un filtro para realizar la búsqueda.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (filtroBusqueda.equals("Partida presupuestal")) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Filtro por partida",
                    "Seleccione una partida presupuestal del combo para realizar esta búsqueda.",
                    Alert.AlertType.INFORMATION
            );
            return;
        }

        if (campoBuscar.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Dato requerido",
                    "Ingrese el dato que desea buscar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            if (filtroBusqueda.equals("Código")) {
                Item item = itemDAO.buscarUno(campoBuscar);

                if (item.getIdItem() != null) {
                    items.add(item);
                }

            } else if (filtroBusqueda.equals("Descripción")) {
                List<Item> itemsBD = itemDAO.buscarPorDescripcion(campoBuscar);
                items.addAll(itemsBD);
            }

            tv_listado.setItems(items);


        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar items del catálogo",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicAgregar(ActionEvent event) {
        abrirFormularioItem(null, false);
    }

    @FXML
    private void clicModificar(ActionEvent event) {
        Item itemSeleccionado = tv_listado.getSelectionModel().getSelectedItem();

        if (itemSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un ítem para modificar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        abrirFormularioItem(itemSeleccionado, true);
    }

    private void abrirFormularioItem(Item item, boolean esEdicion) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItem");
            Parent vista = loader.load();

            RegistroItemController controller = loader.getController();

            if (esEdicion) {
                controller.inicializarEdicion(item);
            } else {
                controller.inicializarRegistro();
            }

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Modificar Ítem" : "Registrar Ítem");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

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
        Item itemSeleccionado = tv_listado.getSelectionModel().getSelectedItem();

        if (itemSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un ítem para eliminar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar el ítem " + itemSeleccionado.getIdItem() + "?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (itemDAO.eliminar(itemSeleccionado)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Eliminación exitosa",
                        "El ítem se eliminó correctamente.",
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
                    "Error al eliminar",
                    "Lo sentimos, el ítem no puede ser eliminado en este momento, por favor inténtelo más tarde.",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalCentral");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Menu Central");
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}