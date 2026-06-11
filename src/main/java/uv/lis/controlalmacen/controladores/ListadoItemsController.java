package uv.lis.controlalmacen.controladores;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListadoItemsController implements Initializable {

    @FXML
    private ComboBox<String> cb_filtroBusqueda;

    @FXML
    private TextField txt_buscarIdProducto;

    @FXML
    private ComboBox<String> cb_filtroStock;

    @FXML
    private ComboBox<PartidaPresupuestal> cb_partidaPresupuestal;

    @FXML
    private TableView<ItemAlmacenado> tv_inventario;

    @FXML
    private TableColumn<ItemAlmacenado, String> col_idItem;

    @FXML
    private TableColumn<ItemAlmacenado, String> col_partida;

    @FXML
    private TableColumn<ItemAlmacenado, String> col_descripcion;

    @FXML
    private TableColumn<ItemAlmacenado, Integer> col_existencias;

    @FXML
    private TableColumn<ItemAlmacenado, Integer> col_stockMin;

    @FXML
    private TableColumn<ItemAlmacenado, Integer> col_stockMax;

    private final ObservableList<String> listaOpcionesBusqueda = FXCollections.observableArrayList(
            "Código",
            "Descripción"
    );

    private final ObservableList<String> listaOpcionesStock = FXCollections.observableArrayList(
            "Sobre el máximo",
            "Menor que el mínimo",
            "Mostrar Todos"
    );

    private ObservableList<ItemAlmacenado> itemsAlmacenados;
    private ObservableList<PartidaPresupuestal> partidasPresupuestales;
    private FilteredList<PartidaPresupuestal> partidasFiltradas;

    private final ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();
    private final PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();

    private boolean cambiandoPartida = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cb_filtroBusqueda.setItems(listaOpcionesBusqueda);
        cb_filtroStock.setItems(listaOpcionesStock);

        configurarTabla();
        configurarComboPartidas();
        cargarInformacionPartidas();
        cargarInformacionItems();
        configurarSeleccionStock();
    }

    private void configurarTabla() {
        col_idItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        col_partida.setCellValueFactory(new PropertyValueFactory<>("descripcionPartida"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionItem"));
        col_existencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        col_stockMin.setCellValueFactory(new PropertyValueFactory<>("stockMin"));
        col_stockMax.setCellValueFactory(new PropertyValueFactory<>("stockMax"));
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
                return buscarPartidaPorDescripcionExacta(descripcion);
            }
        });

        cb_partidaPresupuestal.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (cambiandoPartida) {
                return;
            }

            filtrarComboPartidas(newValue);
        });

        cb_partidaPresupuestal.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (cambiandoPartida) {
                return;
            }

            if (newValue != null) {
                procesarPartidaSeleccionada(newValue);
            }
        });

        cb_partidaPresupuestal.getEditor().setOnAction(event -> seleccionarPartidaEscrita());

        cb_partidaPresupuestal.focusedProperty().addListener((observable, oldValue, estaEnFoco) -> {
            if (!estaEnFoco && cb_partidaPresupuestal.getValue() == null) {
                seleccionarPartidaEscrita();
            }
        });
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
                    "Error al cargar partidas presupuestales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void filtrarComboPartidas(String textoBusqueda) {
        String texto = textoBusqueda == null
                ? ""
                : textoBusqueda.trim().toLowerCase();

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

        for (PartidaPresupuestal partida : partidasPresupuestales) {
            if (partida.getDescripcionPartida().equalsIgnoreCase(descripcionBuscada)) {
                return partida;
            }
        }

        return null;
    }

    private PartidaPresupuestal buscarPartidaPorCoincidencia(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return null;
        }

        String descripcionBuscada = descripcion.trim().toLowerCase();

        for (PartidaPresupuestal partida : partidasPresupuestales) {
            if (partida.getDescripcionPartida().toLowerCase().contains(descripcionBuscada)) {
                return partida;
            }
        }

        return null;
    }

    private void procesarPartidaSeleccionada(PartidaPresupuestal partidaSeleccionada) {
        if (partidaSeleccionada == null) {
            return;
        }

        cambiandoPartida = true;

        cb_partidaPresupuestal.hide();

        String descripcion = partidaSeleccionada.getDescripcionPartida();
        cb_partidaPresupuestal.getEditor().setText(descripcion);
        cb_partidaPresupuestal.getEditor().positionCaret(descripcion.length());

        limpiarBusquedaTexto();
        cb_filtroStock.getSelectionModel().clearSelection();
        cargarItemsPorPartida(partidaSeleccionada);

        Platform.runLater(() -> {
            partidasFiltradas.setPredicate(partida -> true);
            cambiandoPartida = false;
        });
    }

    private void seleccionarPartidaEscrita() {
        String textoEditor = cb_partidaPresupuestal.getEditor().getText();

        if (textoEditor == null || textoEditor.trim().isEmpty()) {
            return;
        }

        PartidaPresupuestal partidaEncontrada = buscarPartidaPorDescripcionExacta(textoEditor);

        if (partidaEncontrada == null) {
            partidaEncontrada = buscarPartidaPorCoincidencia(textoEditor);
        }

        if (partidaEncontrada == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin resultados",
                    "No se encontró una partida presupuestal con ese criterio.",
                    Alert.AlertType.INFORMATION
            );
            return;
        }

        cambiandoPartida = true;

        cb_partidaPresupuestal.hide();
        cb_partidaPresupuestal.getSelectionModel().select(partidaEncontrada);
        cb_partidaPresupuestal.setValue(partidaEncontrada);
        cb_partidaPresupuestal.getEditor().setText(partidaEncontrada.getDescripcionPartida());
        cb_partidaPresupuestal.getEditor().positionCaret(partidaEncontrada.getDescripcionPartida().length());

        limpiarBusquedaTexto();
        cb_filtroStock.getSelectionModel().clearSelection();
        cargarItemsPorPartida(partidaEncontrada);

        Platform.runLater(() -> {
            partidasFiltradas.setPredicate(partida -> true);
            cambiandoPartida = false;
        });
    }

    private void configurarSeleccionStock() {
        cb_filtroStock.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                limpiarBusquedaTexto();
                limpiarSeleccionPartida();

                if (newValue.equals("Mostrar Todos")) {
                    cargarInformacionItems();
                } else {
                    cargarItemsPorStock(newValue);
                }
            }
        });
    }

    private void cargarInformacionItems() {
        try {
            itemsAlmacenados = FXCollections.observableArrayList();

            List<ItemAlmacenado> itemsAlmacenadosBD = itemAlmacenadoDAO.buscarTodos();
            itemsAlmacenados.addAll(itemsAlmacenadosBD);

            tv_inventario.setItems(itemsAlmacenados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar los items de esta sucursal",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void cargarItemsPorStock(String stock) {
        try {
            itemsAlmacenados = FXCollections.observableArrayList();

            List<ItemAlmacenado> itemsAlmacenadosBD = itemAlmacenadoDAO.buscarPorStock(stock);
            itemsAlmacenados.addAll(itemsAlmacenadosBD);

            tv_inventario.setItems(itemsAlmacenados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar los items de la sucursal",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void cargarItemsPorPartida(PartidaPresupuestal partidaSeleccionada) {
        if (partidaSeleccionada == null || partidaSeleccionada.getCodigo() == null) {
            return;
        }

        try {
            itemsAlmacenados = FXCollections.observableArrayList();

            List<ItemAlmacenado> itemsAlmacenadosBD =
                    itemAlmacenadoDAO.buscarPorPartida(partidaSeleccionada.getCodigo());

            itemsAlmacenados.addAll(itemsAlmacenadosBD);
            tv_inventario.setItems(itemsAlmacenados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar los items de la partida",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicBuscarItemAlmacenado(ActionEvent event) {
        buscarItemAlmacenadoPorFiltro();
    }

    private void buscarItemAlmacenadoPorFiltro() {
        String filtroBusqueda = cb_filtroBusqueda.getValue();
        String campoBuscar = txt_buscarIdProducto.getText() == null
                ? ""
                : txt_buscarIdProducto.getText().trim();

        if (filtroBusqueda == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin filtro",
                    "Seleccione si desea buscar por código o por descripción.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (campoBuscar.isEmpty()) {
            return;
        }

        limpiarSeleccionPartida();
        cb_filtroStock.getSelectionModel().clearSelection();

        try {
            itemsAlmacenados = FXCollections.observableArrayList();

            if (filtroBusqueda.equals("Código")) {
                ItemAlmacenado itemAlmacenado = itemAlmacenadoDAO.buscarUno(campoBuscar);

                if (itemAlmacenado.getIdItem() != null) {
                    itemsAlmacenados.add(itemAlmacenado);
                }

            } else if (filtroBusqueda.equals("Descripción")) {
                List<ItemAlmacenado> itemsAlmacenadosBD =
                        itemAlmacenadoDAO.buscarPorDescripcion(campoBuscar);

                itemsAlmacenados.addAll(itemsAlmacenadosBD);
            }

            tv_inventario.setItems(itemsAlmacenados);

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar el item buscado",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicEditarItemSucursal(ActionEvent event) {
        ItemAlmacenado itemSeleccionado = tv_inventario.getSelectionModel().getSelectedItem();

        if (itemSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un ítem para editar sus límites de stock.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItemSucursal");
            Parent vista = loader.load();

            RegistroItemSucursalController controller = loader.getController();
            controller.inicializarEdicion(itemSeleccionado);

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Editar Ítem de Sucursal");
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            actualizarInformacion();

        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al abrir edición",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void clicDarDeBaja(ActionEvent event) {
        ItemAlmacenado itemSeleccionado = tv_inventario.getSelectionModel().getSelectedItem();

        if (itemSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "Seleccione un ítem para darlo de baja.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Optional<String> resultado = UtilidadesFX.mostrarAlertaEntradaTexto(
                "Dar de baja ítem",
                "Ingrese la razón de la baja del ítem " + itemSeleccionado.getIdItem() + ":",
                "Razón de la baja"
        );

        if (!resultado.isPresent()) {
            return;
        }

        String razon = resultado.get().trim();

        if (razon.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Dato requerido",
                    "Debe ingresar la razón de la baja.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (razon.length() > 45) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Dato inválido",
                    "La razón de la baja no puede superar los 45 caracteres.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmacion = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar baja",
                "¿Está seguro de dar de baja el ítem " + itemSeleccionado.getIdItem() + "?"
        );

        if (!confirmacion) {
            return;
        }

        try {
            if (itemAlmacenadoDAO.darDeBaja(itemSeleccionado, razon)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Baja registrada",
                        "El ítem se dio de baja correctamente en la sucursal.",
                        Alert.AlertType.INFORMATION
                );

                actualizarInformacion();
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al dar de baja",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al dar de baja",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void actualizarInformacion() {
        String filtroStock = cb_filtroStock.getValue();
        PartidaPresupuestal partidaSeleccionada = cb_partidaPresupuestal.getValue();
        String filtroBusqueda = cb_filtroBusqueda.getValue();
        String textoBusqueda = txt_buscarIdProducto.getText() == null
                ? ""
                : txt_buscarIdProducto.getText().trim();

        if (filtroStock != null) {
            if (filtroStock.equals("Mostrar Todos")) {
                cargarInformacionItems();
            } else {
                cargarItemsPorStock(filtroStock);
            }

            return;
        }

        if (partidaSeleccionada != null) {
            cargarItemsPorPartida(partidaSeleccionada);
            return;
        }

        if (filtroBusqueda != null && !textoBusqueda.isEmpty()) {
            buscarItemAlmacenadoPorFiltro();
            return;
        }

        cargarInformacionItems();
    }

    private void limpiarBusquedaTexto() {
        txt_buscarIdProducto.clear();
        cb_filtroBusqueda.getSelectionModel().clearSelection();
    }

    private void limpiarSeleccionPartida() {
        if (cb_partidaPresupuestal == null) {
            return;
        }

        cambiandoPartida = true;

        cb_partidaPresupuestal.hide();
        cb_partidaPresupuestal.getSelectionModel().clearSelection();
        cb_partidaPresupuestal.setValue(null);
        cb_partidaPresupuestal.getEditor().clear();

        if (partidasFiltradas != null) {
            partidasFiltradas.setPredicate(partida -> true);
        }

        Platform.runLater(() -> cambiandoPartida = false);
    }

    @FXML
    private void clicVerKardex(ActionEvent event) {
        ItemAlmacenado itemElegido = tv_inventario.getSelectionModel().getSelectedItem();

        if (itemElegido != null) {
            try {
                FXMLLoader loader = UtilidadesFX.cargarFXML("Kardex");
                Parent vista = loader.load();

                KardexController controller = loader.getController();
                controller.cargarKardexItem(itemElegido);

                Scene escena = new Scene(vista);

                Stage stage = new Stage();
                stage.setTitle("Kárdex");
                stage.setResizable(false);
                stage.setScene(escena);
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin selección",
                    "No hay un ítem seleccionado para mostrar un kárdex.",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    public void clicRegresar(ActionEvent actionEvent) {
        navegarA("MenuPrincipalEncargado", "Menú principal");
    }

    @FXML
    public void clicRegistrarFactura(ActionEvent actionEvent) {
        navegarA("RegistroFactura", "Registrar Factura");
    }

    @FXML
    public void clicConsultarFacturas(ActionEvent actionEvent) {
        navegarA("ListadoFacturas", "Consultar Factura");
    }

    @FXML
    public void clicRegistrarItem(ActionEvent actionEvent) {
        navegarA("RegistroItemSucursal", "Registrar Ítem");
    }

    @FXML
    public void clicConsultarItems(ActionEvent actionEvent) {
        navegarA("ListadoItems", "Consultar Ítems");
    }

    @FXML
    public void clicConsultarBitacora(ActionEvent actionEvent) {
        navegarA("BitacoraPedidos", "Consultar Bitácora");
    }

    private void navegarA(String nombreFXML, String tituloVentana) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML(nombreFXML);
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) tv_inventario.getScene().getWindow();
            stage.setTitle(tituloVentana);
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void clicExportar(ActionEvent event) {
    }
}