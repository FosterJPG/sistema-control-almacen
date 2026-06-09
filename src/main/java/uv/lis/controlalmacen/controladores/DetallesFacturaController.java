package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dto.DetallesFactura;
import uv.lis.controlalmacen.modelo.dto.Factura;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DetallesFacturaController implements Initializable {

    @FXML
    private Label lb_folio;
    @FXML
    private Label lb_folioFactura;
    @FXML
    private Label lb_fecha;
    @FXML
    private Label lb_rfc;
    @FXML
    private Label lb_razonSocial;
    @FXML
    private Label lb_domicilio;
    @FXML
    private Label lb_telefono;
    @FXML
    private TableView<DetallesFactura> tv_detalles;
    @FXML
    private TableColumn<DetallesFactura, String> col_item;
    @FXML
    private TableColumn<DetallesFactura, Integer> col_cantidad;
    @FXML
    private TableColumn<DetallesFactura, Double> col_costoUnitario;

    private ObservableList<DetallesFactura> detalles;
    private Factura factura;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaDetalles();
    }

    private void configurarTablaDetalles(){
        col_item.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_costoUnitario.setCellValueFactory(new PropertyValueFactory<>("costoUnitario"));
    }

    public void inicializarInformacion(Factura factura) {
        this.factura = factura;
        cargarDatosFactura();
        cargarTablaDetalles();
    }

    private void cargarDatosFactura() {
        lb_folio.setText(factura.getFolio());
        lb_folioFactura.setText(factura.getFolio());
        lb_fecha.setText(factura.getFecha().toString());
        lb_rfc.setText(factura.getRfc());
        lb_razonSocial.setText(factura.getRazonSocial());
        lb_telefono.setText(factura.getTelefono());
        lb_domicilio.setText(factura.getDireccion());
    }

    private void cargarTablaDetalles() {
        detalles = FXCollections.observableArrayList();
        detalles.addAll(factura.getDetallesFactura());
        tv_detalles.setItems(detalles);
    }


    @FXML
    public void clicRegresar(ActionEvent actionEvent) {
        ((Stage) lb_folio.getScene().getWindow()).close();
    }
}
