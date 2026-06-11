package uv.lis.controlalmacen.controladores;

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
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.FacturaDAO;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dao.ProveedorDAO;
import uv.lis.controlalmacen.modelo.dto.*;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroFacturaController implements Initializable {


    @FXML
    private TextField txt_folio;
    @FXML
    private DatePicker dp_fecha;
    @FXML
    private TextField txt_razonSocial;
    @FXML
    private TextField txt_domicilio;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_rfc;
    @FXML
    private TextField txt_cantidad;
    @FXML
    private TextField txt_costoUnitario;
    @FXML
    private TextField txt_idItem;
    @FXML
    private TextField txt_partida;
    @FXML
    private TextField txt_descripcion;
    @FXML
    private Label lb_errorItem;
    @FXML
    private Label lb_errorFactura;

    @FXML
    private TableView<DetallesFactura> tv_detallesFactura;
    @FXML
    private TableColumn<DetallesFactura, String> col_idItem;
    @FXML
    private TableColumn<DetallesFactura, String> col_descripcion;
    @FXML
    private TableColumn<DetallesFactura, Integer> col_cantidad;
    @FXML
    private TableColumn<DetallesFactura, Double> col_costoUnitario;
    @FXML
    private TableColumn<DetallesFactura, String> col_partidaPresupuestal;

    @FXML
    private Button btn_buscarProveedor;
    @FXML
    private RadioButton rbtn_proveedorExistente;
    @FXML
    private RadioButton rbtn_proveedorNuevo;
    @FXML
    private ToggleGroup tg_seleccionProveedor;

    private boolean esProveedorNuevo = false;
    private Proveedor proveedorSeleccionado;

    private ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();
    private FacturaDAO facturaDAO = new FacturaDAO();

    private ObservableList<DetallesFactura> listaDetallesFactura = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarSeleccionProveedor();
        configurarTablaDetallesFactura();
        tv_detallesFactura.setItems(listaDetallesFactura);
    }

    private void configurarSeleccionProveedor() {

        rbtn_proveedorExistente.setSelected(true);
        aplicarConfiguracionProveedor();

        tg_seleccionProveedor.selectedToggleProperty().addListener((obs, anterior, nuevo) -> {
            aplicarConfiguracionProveedor();
        });
    }

    private void aplicarConfiguracionProveedor() {

        esProveedorNuevo = rbtn_proveedorNuevo.isSelected();

        if (esProveedorNuevo) {
            btn_buscarProveedor.setVisible(false);
            txt_razonSocial.setDisable(false);
            txt_domicilio.setDisable(false);
            txt_telefono.setDisable(false);
        } else {
            btn_buscarProveedor.setVisible(true);
            txt_razonSocial.setDisable(true);
            txt_domicilio.setDisable(true);
            txt_telefono.setDisable(true);
        }
        limpiarDatosProveedor();
    }

    private void limpiarDatosProveedor() {
        proveedorSeleccionado = null;
        txt_rfc.clear();
        txt_razonSocial.clear();
        txt_domicilio.clear();
        txt_telefono.clear();
    }

    private void configurarTablaDetallesFactura(){
        col_idItem.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_costoUnitario.setCellValueFactory(new PropertyValueFactory<>("costoUnitario"));
        col_partidaPresupuestal.setCellValueFactory(new PropertyValueFactory<>("descripcionPartida"));
    }

    @FXML
    private void clicBuscarProveedor(ActionEvent actionEvent) {
        String rfcBusqueda = txt_rfc.getText();
        if (rfcBusqueda == null ||rfcBusqueda.trim().isEmpty()) {
            return;
        }

        try {
            Proveedor proveedor = ProveedorDAO.buscarUno(rfcBusqueda);

            if (proveedor == null) {
                return;
            }

            proveedorSeleccionado = proveedor;

            txt_razonSocial.setText(proveedor.getRazonSocial());
            txt_telefono.setText(proveedor.getTelefono());
            txt_domicilio.setText(proveedor.getDomicilioFiscal());

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
    private void clicGuardarFactura(ActionEvent actionEvent) {

        try {
            if (!validarDatosFactura()) {
                return;
            }

            String folio = txt_folio.getText().trim();

            if (facturaDAO.existeFolio(folio)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Folio repetido",
                        "Ya existe una factura registrada con ese folio.",
                        Alert.AlertType.WARNING
                );
                return;
            }

            if (esProveedorNuevo) {

                String rfc = txt_rfc.getText().trim();

                if (ProveedorDAO.existeRFC(rfc)) {
                    UtilidadesFX.mostrarAlertaSimple(
                            "Proveedor existente",
                            "Ya existe un proveedor registrado con ese RFC.",
                            Alert.AlertType.WARNING
                    );
                    return;
                }

                Proveedor proveedor = obtenerProveedorFormulario();

                boolean proveedorRegistrado = ProveedorDAO.registrarProveedor(proveedor);

                if (!proveedorRegistrado) {
                    UtilidadesFX.mostrarAlertaSimple(
                            "Error al registrar",
                            "No se pudo registrar el proveedor.",
                            Alert.AlertType.ERROR
                    );
                    return;
                }

            } else {

                if (proveedorSeleccionado == null) {
                    UtilidadesFX.mostrarAlertaSimple(
                            "Proveedor no seleccionado",
                            "Debe buscar y seleccionar un proveedor existente.",
                            Alert.AlertType.WARNING
                    );
                    return;
                }
            }

            Factura factura = obtenerFacturaFormulario();

            boolean facturaRegistrada = facturaDAO.registrar(factura);

            if (facturaRegistrada) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Factura registrada",
                        "La factura se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                limpiarFormularioFactura();

            } else {
                UtilidadesFX.mostrarAlertaSimple(
                        "Error al registrar",
                        "No se pudo registrar la factura.",
                        Alert.AlertType.ERROR
                );
            }

        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error de base de datos",
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );

        } catch (IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR
            );

        }
    }

    private boolean validarDatosFactura() {

        if (txt_folio.getText() == null || txt_folio.getText().trim().isEmpty()) {
            lb_errorFactura.setText("Ingrese el folio de la factura.");
            return false;
        }

        if (dp_fecha.getValue() == null) {
            lb_errorFactura.setText("Seleccione la fecha de la factura.");
            return false;
        }

        if (txt_rfc.getText() == null || txt_rfc.getText().trim().isEmpty()) {
            lb_errorFactura.setText("Ingrese el RFC del proveedor.");
            return false;
        }

        if (txt_razonSocial.getText() == null || txt_razonSocial.getText().trim().isEmpty()) {
            lb_errorFactura.setText("Ingrese la razón social del proveedor.");
            return false;
        }

        if (txt_domicilio.getText() == null || txt_domicilio.getText().trim().isEmpty()) {
            lb_errorFactura.setText("Ingrese el domicilio fiscal del proveedor.");
            return false;
        }

        if (txt_telefono.getText() == null || txt_telefono.getText().trim().isEmpty()) {
            lb_errorFactura.setText("Ingrese el teléfono del proveedor.");
            return false;
        }

        if (listaDetallesFactura.isEmpty()) {
            lb_errorItem.setText("Debe agregar al menos un item a la factura.");
            return false;
        }

        if (!esProveedorNuevo && proveedorSeleccionado == null) {
            lb_errorFactura.setText("Debe buscar un proveedor existente antes de guardar la factura.");
            return false;
        }

        return true;
    }

    private Proveedor obtenerProveedorFormulario() {

        Proveedor proveedor = new Proveedor();

        proveedor.setRfc(txt_rfc.getText().trim());
        proveedor.setRazonSocial(txt_razonSocial.getText().trim());
        proveedor.setDomicilioFiscal(txt_domicilio.getText().trim());
        proveedor.setTelefono(txt_telefono.getText().trim());

        return proveedor;
    }

    private Factura obtenerFacturaFormulario() {

        Empleado empleado = Sesion.getUsuarioActual().getEmpleado();
        int noSucursal = empleado.getDepartamento().getSucursal().getNoSucursal();

        Factura factura = new Factura();

        factura.setFolio(txt_folio.getText().trim());
        factura.setFecha(Date.valueOf(dp_fecha.getValue()));
        factura.setRfc(txt_rfc.getText().trim());
        factura.setRazonSocial(txt_razonSocial.getText().trim());
        factura.setTelefono(txt_telefono.getText().trim());
        factura.setDireccion(txt_domicilio.getText().trim());
        factura.setNoSucursal(noSucursal);

        factura.setDetallesFactura(listaDetallesFactura);

        return factura;
    }


    @FXML
    private void clicBuscarItem(ActionEvent actionEvent) {
        lb_errorItem.setText("");
        String itemBusqueda = txt_idItem.getText();
        if (itemBusqueda == null || itemBusqueda.trim().isEmpty()) {
            return;
        }

        try {
           ItemAlmacenado item = itemAlmacenadoDAO.buscarUno(itemBusqueda);
           txt_descripcion.setText(item.getDescripcionItem());
           txt_partida.setText(item.getDescripcionPartida());

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
    private void clicEliminarItem(ActionEvent event) {
        DetallesFactura detalleSeleccionado = tv_detallesFactura.getSelectionModel().getSelectedItem();

        if (detalleSeleccionado == null) {
            lb_errorItem.setText("Seleccione un item");
            return;
        }
        lb_errorItem.setText("");
        listaDetallesFactura.remove(detalleSeleccionado);
    }

    @FXML
    private void clicAgregarItem(ActionEvent event) {
        String id = txt_idItem.getText();
        String descripcion = txt_descripcion.getText();
        String descripcionPartida = txt_partida.getText();
        String cantidadStr = txt_cantidad.getText();
        String costoUnitarioStr = txt_costoUnitario.getText();

        if (id.isEmpty() || descripcion.isEmpty() || descripcionPartida.isEmpty()
                || cantidadStr.isEmpty() || costoUnitarioStr.isEmpty()) {
            lb_errorItem.setText("Datos incompletos");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lb_errorItem.setText("Cantidad no valida");
            return;
        }

        double costoUnitario;
        try {
            costoUnitario = Double.parseDouble(costoUnitarioStr);
            if (costoUnitario <= 0) {
                throw new  NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lb_errorItem.setText("Costo no valido");
            return;
        }

        boolean duplicado = listaDetallesFactura.stream().anyMatch(d -> d.getIdItem().equals(id));
        if (duplicado) {
            lb_errorItem.setText("El item ya fue agregado a la factura");
            return;
        }

        DetallesFactura detallesFactura = new DetallesFactura();
        detallesFactura.setIdItem(id);
        detallesFactura.setDescripcion(descripcion);
        detallesFactura.setDescripcionPartida(descripcionPartida);
        detallesFactura.setCantidad(cantidad);
        detallesFactura.setCostoUnitario(costoUnitario);
        listaDetallesFactura.add(detallesFactura);
        limpiarCamposItem();
    }

    private void limpiarCamposItem() {
        txt_idItem.clear();
        txt_descripcion.clear();
        txt_partida.clear();
        txt_cantidad.clear();
        txt_costoUnitario.clear();
        lb_errorItem.setText("");
    }

    private void limpiarFormularioFactura() {
        txt_folio.clear();
        dp_fecha.setValue(null);

        listaDetallesFactura.clear();
        limpiarCamposItem();

        rbtn_proveedorExistente.setSelected(true);
        aplicarConfiguracionProveedor();
    }

    //NAVEGACION
    @FXML
    public void clicCancelar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("MenuPrincipalEncargado");
            Parent vista =  loader.load();
            MenuController controller = loader.getController();
            controller.cargarDatos();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
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
    public void clicConsultarFacturas(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ListadoFacturas");
            Parent vista =  loader.load();
            Scene escena = new Scene(vista);

            Stage stage = (Stage) txt_folio.getScene().getWindow();
            stage.setTitle("Listado de facturas");
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

            Stage stage = (Stage) txt_folio.getScene().getWindow();
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

            Stage stage = (Stage) txt_folio.getScene().getWindow();
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

            Stage stage = (Stage) txt_folio.getScene().getWindow();
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
