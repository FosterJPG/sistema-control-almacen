package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uv.lis.controlalmacen.modelo.dao.DepartamentoDAO;
import uv.lis.controlalmacen.modelo.dao.SucursalDAO;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroDepartamentoController implements Initializable {

    @FXML
    private Label lbl_titulo;

    @FXML
    private TextField txt_descripcion;

    @FXML
    private ComboBox<Sucursal> cb_sucursal;

    @FXML
    private Label lblError;

    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();

    private final ObservableList<Sucursal> sucursalesBase = FXCollections.observableArrayList();

    private boolean esEdicion = false;
    private Departamento departamentoEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblError.setWrapText(true);
        lblError.setText("");

        configurarComboSucursales();
        configurarBusquedaSucursales();
    }

    public void inicializarRegistro() {
        esEdicion = false;
        departamentoEdicion = null;

        lbl_titulo.setText("Registrar Departamento");
        limpiarCampos();
        cargarSucursales();
    }

    public void inicializarEdicion(Departamento departamento) {
        if (departamento == null) {
            return;
        }

        esEdicion = true;
        departamentoEdicion = departamento;

        lbl_titulo.setText("Modificar Departamento");
        limpiarCampos();
        cargarSucursales();

        txt_descripcion.setText(departamento.getDescripcion());
        seleccionarSucursal(departamento);
        cb_sucursal.setDisable(true);
    }

    private void configurarComboSucursales() {
        cb_sucursal.setConverter(new StringConverter<Sucursal>() {
            @Override
            public String toString(Sucursal sucursal) {
                if (sucursal == null) {
                    return "";
                }

                return sucursal.getNombre();
            }

            @Override
            public Sucursal fromString(String texto) {
                return buscarSucursalPorTexto(texto);
            }
        });

        cb_sucursal.setCellFactory(listView -> new ListCell<Sucursal>() {
            @Override
            protected void updateItem(Sucursal sucursal, boolean empty) {
                super.updateItem(sucursal, empty);

                if (empty || sucursal == null) {
                    setText(null);
                } else {
                    setText(sucursal.getNombre());
                }
            }
        });

        cb_sucursal.setButtonCell(new ListCell<Sucursal>() {
            @Override
            protected void updateItem(Sucursal sucursal, boolean empty) {
                super.updateItem(sucursal, empty);

                if (empty || sucursal == null) {
                    setText(null);
                } else {
                    setText(sucursal.getNombre());
                }
            }
        });
    }

    private void configurarBusquedaSucursales() {
        cb_sucursal.getEditor().textProperty().addListener((observable, textoAnterior, textoNuevo) -> {
            if (!cb_sucursal.getEditor().isFocused()) {
                return;
            }

            filtrarSucursales(textoNuevo);
        });
    }

    private void filtrarSucursales(String texto) {
        List<Sucursal> sucursalesFiltradas = new ArrayList<>();

        if (texto == null || texto.trim().isEmpty()) {
            sucursalesFiltradas.addAll(sucursalesBase);
        } else {
            String textoBuscado = texto.trim().toLowerCase();

            for (Sucursal sucursal : sucursalesBase) {
                if (sucursal.getNombre().toLowerCase().contains(textoBuscado)) {
                    sucursalesFiltradas.add(sucursal);
                }
            }
        }

        cb_sucursal.setItems(FXCollections.observableArrayList(sucursalesFiltradas));
        cb_sucursal.show();
        cb_sucursal.getEditor().positionCaret(cb_sucursal.getEditor().getText().length());
    }

    private void cargarSucursales() {
        try {
            List<Sucursal> sucursales = sucursalDAO.buscarTodos();

            sucursalesBase.clear();
            sucursalesBase.addAll(sucursales);

            cb_sucursal.setItems(sucursalesBase);

        } catch (SQLException ex) {
            lblError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar sucursales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void seleccionarSucursal(Departamento departamento) {
        if (departamento.getSucursal() == null || departamento.getSucursal().getNoSucursal() == null) {
            return;
        }

        cb_sucursal.setItems(sucursalesBase);

        Integer noSucursal = departamento.getSucursal().getNoSucursal();

        for (Sucursal sucursal : sucursalesBase) {
            if (sucursal.getNoSucursal().equals(noSucursal)) {
                cb_sucursal.getSelectionModel().select(sucursal);
                return;
            }
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        lblError.setText("");

        String descripcion = obtenerDescripcion();
        Sucursal sucursal = obtenerSucursalSeleccionada();

        if (descripcion.isEmpty()) {
            lblError.setText("Ingrese la descripción del departamento.");
            return;
        }

        if (sucursal == null) {
            lblError.setText("Seleccione una sucursal existente.");
            return;
        }

        if (esEdicion) {
            guardarEdicion(descripcion, sucursal);
        } else {
            guardarRegistro(descripcion, sucursal);
        }
    }

    private void guardarRegistro(String descripcion, Sucursal sucursal) {
        try {
            Departamento departamento = new Departamento();
            departamento.setDescripcion(descripcion);
            departamento.setSucursal(sucursal);

            if (departamentoDAO.registrar(departamento)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Registro exitoso",
                        "El departamento se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (SQLException ex) {
            lblError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al registrar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void guardarEdicion(String descripcion, Sucursal sucursal) {
        if (departamentoEdicion == null || departamentoEdicion.getIdDepto() == null) {
            lblError.setText("No hay un departamento seleccionado para modificar.");
            return;
        }

        try {
            Departamento departamento = new Departamento();
            departamento.setIdDepto(departamentoEdicion.getIdDepto());
            departamento.setDescripcion(descripcion);
            departamento.setSucursal(sucursal);

            if (departamentoDAO.actualizar(departamento)) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Actualización exitosa",
                        "El departamento se actualizó correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (SQLException ex) {
            lblError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al modificar",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private Sucursal obtenerSucursalSeleccionada() {
        String texto = cb_sucursal.getEditor().getText();

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return buscarSucursalPorTexto(texto);
    }

    private Sucursal buscarSucursalPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        String textoBuscado = texto.trim();

        for (Sucursal sucursal : sucursalesBase) {
            if (sucursal.getNombre().equalsIgnoreCase(textoBuscado)) {
                return sucursal;
            }
        }

        return null;
    }

    private String obtenerDescripcion() {
        if (txt_descripcion.getText() == null) {
            return "";
        }

        return txt_descripcion.getText().trim();
    }

    private void limpiarCampos() {
        txt_descripcion.clear();

        cb_sucursal.setItems(sucursalesBase);
        cb_sucursal.getSelectionModel().clearSelection();
        cb_sucursal.getEditor().clear();

        lblError.setText("");
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txt_descripcion.getScene().getWindow();
        stage.close();
    }
}
