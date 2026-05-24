package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.FacturaDAO;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListadoFacturasController implements Initializable {

    private static final FacturaDAO facturaDAO = new  FacturaDAO();


    @FXML
    private TableView<Factura> tv_facturas;
    @FXML
    private TextField txt_buscar;
    @FXML
    private TableColumn col_folio;
    @FXML
    private TableColumn col_fecha;
    @FXML
    private TableColumn col_rfc;
    @FXML
    private TableColumn col_razonSocial;
    @FXML
    private TableColumn col_telefono;

    private ObservableList<Factura> facturas;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarInformacionTabla();
    }

    private void configurarTabla() {
        col_folio.setCellValueFactory(new PropertyValueFactory("folio"));
        col_fecha.setCellValueFactory(new PropertyValueFactory("fecha"));
        col_rfc.setCellValueFactory(new PropertyValueFactory("rfc"));
        col_razonSocial.setCellValueFactory(new PropertyValueFactory("razonSocial"));
        col_telefono.setCellValueFactory(new PropertyValueFactory("telefono"));
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
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void clicVerDetalles(ActionEvent actionEvent) {
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
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void clicRegistrarItem(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("RegistroItem");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_buscar.getScene().getWindow();
            stage.setTitle("Registro de items para la sucursal");
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.setScene(escena);
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
            stage.centerOnScreen();

            stage.setScene(escena);
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
            stage.centerOnScreen();

            stage.setScene(escena);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
