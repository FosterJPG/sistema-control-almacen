package uv.lis.controlalmacen.controladores;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.FacturaDAO;
import uv.lis.controlalmacen.modelo.dao.PartidaPresupuestalDAO;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.ExportadorPDF;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ListadoFacturasController implements Initializable {

    // Atributos de JavaFX
    @FXML
    private ComboBox<String> cb_partidaBusqueda;
    @FXML
    private DatePicker dp_fechaInicial;
    @FXML
    private DatePicker dp_fechaFinal;
    @FXML
    private TableView<Factura> tv_facturas;
    @FXML
    private TextField txt_buscar;
    @FXML
    private TableColumn<Factura, String> col_folio;
    @FXML
    private TableColumn<Factura, Date> col_fecha;
    @FXML
    private TableColumn<Factura, String> col_rfc;
    @FXML
    private TableColumn<Factura, String> col_razonSocial;
    @FXML
    private TableColumn<Factura, String> col_telefono;

    // Atributos de configuración del controller
    private ObservableList<Factura> facturas;
    private static final FacturaDAO facturaDAO = new  FacturaDAO();

    private final PartidaPresupuestalDAO partidaPresupuestalDAO = new PartidaPresupuestalDAO();
    private final ObservableList<String> partidasPresupuestales = FXCollections.observableArrayList();
    private FilteredList<String> partidasFiltradas;

    private boolean seleccionandoPartida = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarComboPartidas();

        cargarPartidasPresupuestales();
        cargarInformacionTabla();

        dp_fechaFinal.setDisable(true);
        configurarSeleccionFecha();
    }

    private void configurarTabla() {
        col_folio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_rfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));
        col_razonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    private void configurarSeleccionFecha() {
        dp_fechaInicial.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate newValue) {

                if (newValue != null) {
                    dp_fechaFinal.setDisable(false);
                } else {
                    dp_fechaFinal.setDisable(true);
                    dp_fechaFinal.setValue(null);
                    return;
                }

                if (dp_fechaFinal.getValue() != null && dp_fechaFinal.getValue().isBefore(newValue)) {
                    dp_fechaFinal.setValue(null);
                }

                dp_fechaFinal.setDayCellFactory(dp -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(empty || item.isBefore(newValue));
                    }
                });

                if (dp_fechaFinal.getValue() != null && !dp_fechaFinal.getValue().isBefore(newValue)) {
                    aplicarFiltros();
                }
            }
        });

        dp_fechaFinal.valueProperty().addListener(
                (observableValue, localDate, t1) ->
                        aplicarFiltros());
    }

    private void configurarComboPartidas() {

        cb_partidaBusqueda.setEditable(true);

        partidasFiltradas = new FilteredList<>(partidasPresupuestales, partida -> true);

        cb_partidaBusqueda.setItems(partidasFiltradas);

        cb_partidaBusqueda.valueProperty().addListener((obs, anterior, nuevo) -> {
                if (nuevo == null) {
                    return;
                }

                seleccionandoPartida = true;

                Platform.runLater(() -> {
                    cb_partidaBusqueda.getEditor().setText(nuevo);
                    cb_partidaBusqueda.getEditor().positionCaret(nuevo.length());
                    partidasFiltradas.setPredicate(partida -> true);
                    aplicarFiltros();
                    seleccionandoPartida = false;
                });
            }
        );

        cb_partidaBusqueda.getEditor().textProperty().addListener((obs, anterior, nuevo) -> {

            if (seleccionandoPartida) {
                return;
            }

            filtrarPartidas(nuevo);
        });
    }

    private void filtrarPartidas(String textoBusqueda) {

        String texto = textoBusqueda == null ? "" : textoBusqueda.trim().toLowerCase();

        partidasFiltradas.setPredicate(partida -> {
            if (texto.isEmpty()) {
                return true;
            }
            return partida.toLowerCase().contains(texto);
        });

        if (cb_partidaBusqueda.isFocused() && !partidasFiltradas.isEmpty()) {

            Platform.runLater(() -> {
                if (!cb_partidaBusqueda.isShowing()) {
                    cb_partidaBusqueda.show();
                }
            });
        }
    }

    private void cargarInformacionTabla() {
        try {
            facturas = FXCollections.observableArrayList();
            List<Factura> facturasBD = facturaDAO.buscarTodos();
            facturas.addAll(facturasBD);
            tv_facturas.setItems(facturas);

        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    private void cargarPartidasPresupuestales() {
        try {
            List<PartidaPresupuestal> partidasBD = partidaPresupuestalDAO.buscarTodos();
            partidasPresupuestales.clear();

            for (PartidaPresupuestal partida : partidasBD) {
                partidasPresupuestales.add(partida.getDescripcionPartida());
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException | IOException | ClassNotFoundException ex) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    "Lo sentimos, las partidas presupuestales no pueden ser cargadas en este momento, " +
                            "por favor inténtelo más tarde.",
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void clicVerTodos(ActionEvent actionEvent) {
        //cargarInformacionTabla();
        dp_fechaFinal.setValue(null);
        dp_fechaInicial.setValue(null);
        cb_partidaBusqueda.setValue(null);
        cb_partidaBusqueda.getEditor().clear();
        txt_buscar.setText("");
        aplicarFiltros();
    }

    @FXML
    public void clicBuscarPorFolio(ActionEvent actionEvent) {
        String folioBuscar = txt_buscar.getText();
        if (folioBuscar == null || folioBuscar.isEmpty()) {
            return;
        }
        try {
            facturas = FXCollections.observableArrayList();
            Factura factura = facturaDAO.buscarUno(folioBuscar);
            facturas.add(factura);
            tv_facturas.setItems(facturas);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    private void aplicarFiltros() {
        try {
            String partidaBuscar = cb_partidaBusqueda.getEditor().getText();

            boolean hayPartida = partidaBuscar != null && !partidaBuscar.isBlank();

            boolean hayFecha = dp_fechaInicial.getValue() != null && dp_fechaFinal.getValue() != null;

            List<Factura> facturasBD;

            if (hayPartida && hayFecha) {
                facturasBD = facturaDAO.buscarPorPartidaYFecha(partidaBuscar, dp_fechaInicial.getValue(), dp_fechaFinal.getValue());
            } else if (hayPartida) {
                facturasBD = facturaDAO.buscarPorPartidaPresupuestal(partidaBuscar);
            } else if (hayFecha) {
                facturasBD = facturaDAO.buscarPorFecha(dp_fechaInicial.getValue(), dp_fechaFinal.getValue());
            } else {
                facturasBD = facturaDAO.buscarTodos();
            }

            facturas = FXCollections.observableArrayList();
            facturas.addAll(facturasBD);

            tv_facturas.setItems(facturas);

        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clicRegresar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista = loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Menu principal");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicVerDetalles(ActionEvent actionEvent) {
        try {
            Factura facturaSeleccionada = tv_facturas.getSelectionModel().getSelectedItem();

            if (facturaSeleccionada == null) {
                UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                        "Primero selecciona una factura para poder ver sus detalles.",
                        Alert.AlertType.INFORMATION);
                return;
            }

            Factura facturaCompleta = new  Factura();
            if ( facturaSeleccionada.getDetallesFactura() == null
                    || facturaSeleccionada.getDetallesFactura().isEmpty()) {
                facturaCompleta = facturaDAO.buscarUno(facturaSeleccionada.getFolio());
            }
            if (facturaSeleccionada.getDetallesFactura() == null
                    || facturaSeleccionada.getDetallesFactura().isEmpty()) {
                cargarVistaDetallesFactura(facturaCompleta);
            } else {
                cargarVistaDetallesFactura(facturaSeleccionada);
            }

        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    private void cargarVistaDetallesFactura(Factura factura) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("DetallesFactura");
            Parent vista = loader.load();
            DetallesFacturaController controller = loader.getController();
            controller.inicializarInformacion(factura);

            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Detalles de factura");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void clicGenerarReporte(ActionEvent actionEvent) {
        List<Factura> facturasTabla = new ArrayList<Factura>(tv_facturas.getItems());

        if (facturasTabla.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin datos",
                    "No hay facturas que exportar", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de ingresos");
        fileChooser.setInitialFileName("reporte-facturas.pdf");
        fileChooser.getExtensionFilters().add(new  FileChooser.ExtensionFilter("PDF", "*.pdf"));

        Stage stageActual = (Stage) txt_buscar.getScene().getWindow();
        File archivo =  fileChooser.showSaveDialog(stageActual);

        if (archivo == null) {
            return;
        }

        try {
            List <Factura> facturasConDetalles =new ArrayList<>();

            for (Factura f : facturasTabla) {
                Factura facturaCompleta = facturaDAO.buscarUno(f.getFolio());
                facturasConDetalles.add(facturaCompleta);
            }

            String partidaSeleccionada = null;

            if (!cb_partidaBusqueda.getEditor().getText().isEmpty()) {
                partidaSeleccionada = cb_partidaBusqueda.getEditor().getText();
            }

            ExportadorPDF.generarReporteIngresos(archivo.getAbsolutePath(), facturasConDetalles,
                    dp_fechaInicial.getValue(), dp_fechaFinal.getValue(), partidaSeleccionada);

            UtilidadesFX.mostrarAlertaSimple("Exportación exitosa",
                    "Reporte guardado en:\n" + archivo.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (FileNotFoundException e) {
                UtilidadesFX.mostrarAlertaSimple("Error al generar formato",
                        "Lo sentimos, no pudimos generar el reporte de ingresos por un problema con el archivo. " +
                                "Intente nuevamente más tarde",
                        Alert.AlertType.ERROR);
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clicRegistrarFactura(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroFactura");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Registro de facturas");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicRegistrarItem(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItemSucursal");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Registro de items para la sucursal");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicConsultarItems(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoItems");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Listado de items almacenados");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicConsultarBitacora(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("BitacoraPedidos");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Bitacora de pedidos");
            stage.setResizable(false);

            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}