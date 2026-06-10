/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package uv.lis.controlalmacen.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.ItemAlmacenadoDAO;
import uv.lis.controlalmacen.modelo.dao.ItemDAO;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class RegistroItemSucursalController implements Initializable {

    @FXML
    private TextField txt_idItem;

    @FXML
    private TextField txt_descripcion;

    @FXML
    private TextField txt_partidaPresupuestal;

    @FXML
    private TextField txt_stockMinimo;

    @FXML
    private TextField txt_stockMaximo;

    @FXML
    private Label txt_noEncontrado;

    @FXML
    private Label txt_faltanDatos;

    private final ItemDAO itemDAO = new ItemDAO();
    private final ItemAlmacenadoDAO itemAlmacenadoDAO = new ItemAlmacenadoDAO();

    private Item itemCatalogoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txt_descripcion.setEditable(false);
        txt_partidaPresupuestal.setEditable(false);

        txt_noEncontrado.setWrapText(true);
        txt_faltanDatos.setWrapText(true);

        configurarCamposNumericos();

        txt_idItem.textProperty().addListener((observable, oldValue, newValue) -> {
            limpiarDatosItemEncontrado();
        });
    }

    private void configurarCamposNumericos() {
        UnaryOperator<TextFormatter.Change> filtroEnteros = change -> {
            String textoNuevo = change.getControlNewText();

            if (textoNuevo.matches("\\d*")) {
                return change;
            }

            return null;
        };

        txt_stockMinimo.setTextFormatter(new TextFormatter<>(filtroEnteros));
        txt_stockMaximo.setTextFormatter(new TextFormatter<>(filtroEnteros));
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String idItem = obtenerTexto(txt_idItem);

        txt_noEncontrado.setText("");
        txt_faltanDatos.setText("");

        if (idItem.isEmpty()) {
            txt_noEncontrado.setText("Falta ingresar el código del ítem.");
            return;
        }

        buscarItemCatalogo(idItem);
    }

    private boolean buscarItemCatalogo(String idItem) {
        try {
            Item item = itemDAO.buscarUno(idItem);

            if (item.getIdItem() == null) {
                limpiarDatosItemEncontrado();
                txt_noEncontrado.setText("No existe un ítem con ese código en el catálogo general.");
                return false;
            }

            itemCatalogoSeleccionado = item;
            txt_descripcion.setText(item.getDescripcionItem());
            txt_partidaPresupuestal.setText(item.getDescripcionPartida());
            txt_noEncontrado.setText("");
            txt_faltanDatos.setText("");

            return true;

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al consultar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar el ítem",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }

        return false;
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        txt_noEncontrado.setText("");
        txt_faltanDatos.setText("");

        String idItem = obtenerTexto(txt_idItem);
        String stockMinimoTexto = obtenerTexto(txt_stockMinimo);
        String stockMaximoTexto = obtenerTexto(txt_stockMaximo);

        String mensajeValidacion = validarDatos(idItem, stockMinimoTexto, stockMaximoTexto);

        if (!mensajeValidacion.isEmpty()) {
            txt_faltanDatos.setText(mensajeValidacion);
            return;
        }

        if (itemCatalogoSeleccionado == null
                || itemCatalogoSeleccionado.getIdItem() == null
                || !itemCatalogoSeleccionado.getIdItem().equals(idItem)) {

            boolean existeItem = buscarItemCatalogo(idItem);

            if (!existeItem) {
                txt_faltanDatos.setText("No se puede guardar porque el ítem no existe en el catálogo.");
                return;
            }
        }

        Integer stockMinimo = convertirEntero(stockMinimoTexto, "stock mínimo");

        if (stockMinimo == null) {
            return;
        }

        Integer stockMaximo = convertirEntero(stockMaximoTexto, "stock máximo");

        if (stockMaximo == null) {
            return;
        }

        if (stockMaximo < stockMinimo) {
            txt_faltanDatos.setText("El stock máximo no puede ser menor que el stock mínimo.");
            return;
        }

        try {
            ItemAlmacenado itemExistente = itemAlmacenadoDAO.buscarUno(idItem);

            if (itemExistente.getIdItem() != null) {
                txt_faltanDatos.setText("Este ítem ya está registrado en el almacén de la sucursal. Edítalo desde Consultar items.");
                return;
            }

            ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
            itemAlmacenado.setIdItem(idItem);
            itemAlmacenado.setDescripcionItem(itemCatalogoSeleccionado.getDescripcionItem());
            itemAlmacenado.setCodigoPartidaPresupuestal(itemCatalogoSeleccionado.getCodigoPartidaPresupuestal());
            itemAlmacenado.setDescripcionPartida(itemCatalogoSeleccionado.getDescripcionPartida());
            itemAlmacenado.setExistencias(0);
            itemAlmacenado.setStockMin(stockMinimo);
            itemAlmacenado.setStockMax(stockMaximo);

            if (itemAlmacenadoDAO.registrar(itemAlmacenado)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Registro exitoso",
                        "El ítem se registró correctamente en el almacén de la sucursal.",
                        Alert.AlertType.INFORMATION
                );

                limpiarCampos();
            }

        } catch (SQLException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al registrar",
                    ex.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al registrar el ítem",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private String validarDatos(String idItem, String stockMinimoTexto, String stockMaximoTexto) {
        StringBuilder mensaje = new StringBuilder();

        if (idItem.isEmpty()) {
            mensaje.append("Falta el código del ítem.\n");
        }

        if (txt_descripcion.getText() == null || txt_descripcion.getText().trim().isEmpty()) {
            mensaje.append("Debe buscar un ítem válido del catálogo.\n");
        }

        if (txt_partidaPresupuestal.getText() == null || txt_partidaPresupuestal.getText().trim().isEmpty()) {
            mensaje.append("No se ha cargado la partida presupuestal.\n");
        }

        if (stockMinimoTexto.isEmpty()) {
            mensaje.append("Falta el stock mínimo.\n");
        }

        if (stockMaximoTexto.isEmpty()) {
            mensaje.append("Falta el stock máximo.\n");
        }

        return mensaje.toString();
    }

    private Integer convertirEntero(String valor, String campo) {
        try {
            return Integer.parseInt(valor);

        } catch (NumberFormatException ex) {
            txt_faltanDatos.setText("El " + campo + " debe ser un número entero válido.");
            return null;
        }
    }

    private String obtenerTexto(TextField textField) {
        if (textField.getText() == null) {
            return "";
        }

        return textField.getText().trim();
    }

    private void limpiarDatosItemEncontrado() {
        txt_descripcion.clear();
        txt_partidaPresupuestal.clear();
        txt_noEncontrado.setText("");
        txt_faltanDatos.setText("");
        itemCatalogoSeleccionado = null;
    }

    private void limpiarCampos() {
        txt_idItem.clear();
        txt_descripcion.clear();
        txt_partidaPresupuestal.clear();
        txt_stockMinimo.clear();
        txt_stockMaximo.clear();
        txt_noEncontrado.setText("");
        txt_faltanDatos.setText("");
        itemCatalogoSeleccionado = null;
    }

    @FXML
    public void clicCancelar(ActionEvent actionEvent) {
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

            Stage stage = (Stage) txt_idItem.getScene().getWindow();
            stage.setTitle(tituloVentana);
            stage.setResizable(false);
            stage.setScene(escena);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}