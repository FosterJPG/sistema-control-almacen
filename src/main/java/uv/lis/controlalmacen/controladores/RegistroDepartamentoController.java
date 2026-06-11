package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.DepartamentoDAO;
import uv.lis.controlalmacen.modelo.dao.SucursalDAO;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroDepartamentoController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<Sucursal> cbSucursal;
    @FXML private Label lblError;

    private Departamento departamentoEdicion;
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarSucursales();
        cbSucursal.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Sucursal s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getNombre());
            }
        });
        cbSucursal.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Sucursal s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getNombre());
            }
        });
    }

    private void cargarSucursales() {
        try {
            List<Sucursal> lista = sucursalDAO.buscarTodos();
            cbSucursal.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar las sucursales.", Alert.AlertType.ERROR);
        }
    }

    public void inicializarRegistro() {
        lblTitulo.setText("Registrar Departamento");
        cbSucursal.setDisable(false);
    }

    public void inicializarEdicion(Departamento departamento) {
        this.departamentoEdicion = departamento;
        lblTitulo.setText("Modificar Departamento");
        txtDescripcion.setText(departamento.getDescripcion());
        cbSucursal.setDisable(true);
        cbSucursal.getItems().stream()
                .filter(s -> s.getNoSucursal() == departamento.getSucursal().getNoSucursal())
                .findFirst()
                .ifPresent(cbSucursal::setValue);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        lblError.setText("");
        String descripcion = txtDescripcion.getText().trim();

        if (descripcion.isEmpty()) {
            lblError.setText("La descripción es obligatoria.");
            return;
        }
        if (departamentoEdicion == null && cbSucursal.getValue() == null) {
            lblError.setText("Selecciona una sucursal.");
            return;
        }

        try {
            if (departamentoEdicion == null) {
                departamentoDAO.registrar(descripcion, cbSucursal.getValue().getNoSucursal());
                UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                        "Departamento registrado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                departamentoDAO.actualizar(departamentoEdicion.getIdDepto(), descripcion);
                UtilidadesFX.mostrarAlertaSimple("Actualización exitosa",
                        "Departamento actualizado correctamente.", Alert.AlertType.INFORMATION);
            }
            cerrar();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al guardar",
                    "No se pudo guardar el departamento:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtDescripcion.getScene().getWindow()).close();
    }
}
