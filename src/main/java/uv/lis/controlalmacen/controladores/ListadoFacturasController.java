package uv.lis.controlalmacen.controladores;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import uv.lis.controlalmacen.modelo.dao.FacturaDAO;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ListadoFacturasController implements Initializable {

    private static final FacturaDAO facturaDAO = new  FacturaDAO();

    @FXML
    private TextField txt_partidaBusqueda;
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

    private ObservableList<Factura> facturas;
    private boolean filtroFechaAplicado = false;
    private boolean filtroPartidaAplicado = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
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
                    cargarPedidosPorFecha();
                }
            }
        });

        dp_fechaFinal.valueProperty().addListener(
                (observableValue, localDate, t1) ->
                        cargarPedidosPorFecha());
    }

    private void cargarInformacionTabla() {
        try {
            facturas = FXCollections.observableArrayList();
            List<Factura> facturasBD = facturaDAO.buscarTodos();
            facturas.addAll(facturasBD);
            tv_facturas.setItems(facturas);

        } catch (SQLException | IOException | ClassNotFoundException e) {
            // TODO Errores
        }
    }

    // TODO refactorizar para que implemente la logica de la busqueda combinada si filtrosAplicados == 1
    private void cargarPedidosPorFecha() {
        if (dp_fechaInicial.getValue() == null || dp_fechaFinal.getValue() == null) {
            return;
        }

        try {
            facturas = FXCollections.observableArrayList();
            List<Factura> facturasBD = facturaDAO.buscarPorFecha(dp_fechaInicial.getValue(), dp_fechaFinal.getValue());
            facturas.addAll(facturasBD);
            tv_facturas.setItems(facturas);
        } catch (SQLException e){
            e.printStackTrace();
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clicVerTodos(ActionEvent actionEvent) {
        cargarInformacionTabla();
        filtroFechaAplicado = false;
        filtroPartidaAplicado = false;
        dp_fechaFinal.setValue(null);
        dp_fechaInicial.setValue(null);
        txt_partidaBusqueda.setText("");
    }

    @FXML
    public void clicBuscarPorFolio(ActionEvent actionEvent) {
        String folioBuscar = txt_buscar.getText();
        // TODO busqueda por folio
    }

    @FXML
    public void clicBuscarPorPartida(ActionEvent actionEvent) {
        String partidaBuscar = txt_partidaBusqueda.getText();
        if (partidaBuscar == null || partidaBuscar.isEmpty()) {
            return;
        }
        try {
            List<Factura> facturasBD;
            facturas = FXCollections.observableArrayList();
            if (!filtroFechaAplicado) {
                facturasBD = facturaDAO.buscarPorPartidaPresupuestal(partidaBuscar);
                filtroPartidaAplicado = true;
            } else {
                facturasBD = facturaDAO.buscarPorPartidaYFecha(partidaBuscar, dp_fechaInicial.getValue(), dp_fechaFinal.getValue());
            }
            facturas.addAll(facturasBD);
            tv_facturas.setItems(facturas);

        } catch (SQLException e){
            e.printStackTrace();
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
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
        /*
        TODO
        1. ver si está seleccionada
        2. cargar el modal
         */
        Factura facturaSeleccionada =  tv_facturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin selección",
                    "No hay una factura seleccionada para mostrar sus detalles",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("DetallesFactura");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Detalles de factura");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicGenerarReporte(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ReporteIngresos");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Reporte ingresos");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
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