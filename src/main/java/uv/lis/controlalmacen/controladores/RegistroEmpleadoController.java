package uv.lis.controlalmacen.controladores;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uv.lis.controlalmacen.modelo.dao.DepartamentoDAO;
import uv.lis.controlalmacen.modelo.dao.EmpleadoDAO;
import uv.lis.controlalmacen.modelo.dao.PuestoDAO;
import uv.lis.controlalmacen.modelo.dao.SucursalDAO;
import uv.lis.controlalmacen.modelo.dto.*;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroEmpleadoController implements Initializable {

    @FXML private TextField txt_nombre;
    @FXML private TextField txt_paterno;
    @FXML private TextField txt_materno;
    @FXML private TextField txt_direccion;
    @FXML private TextField txt_telefono;
    @FXML private TextField txt_correo;
    @FXML private ComboBox<Puesto> cb_puesto;
    @FXML private ComboBox<Sucursal> cb_sucursal;
    @FXML private ComboBox<Departamento> cb_departamento;

    private final EmpleadoDAO empleadoDAO   = new EmpleadoDAO();
    private final PuestoDAO puestoDAO       = new PuestoDAO();
    private final SucursalDAO sucursalDAO   = new SucursalDAO();
    private final DepartamentoDAO deptoDAO  = new DepartamentoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPuestos();
        cargarSucursales();
        cb_sucursal.setOnAction(e -> cargarDepartamentos());
        cb_departamento.setDisable(true);
    }

    private void cargarPuestos() {
        try {
            List<Puesto> puestos = puestoDAO.buscarTodos();
            cb_puesto.setItems(FXCollections.observableArrayList(puestos));
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar los puestos.", Alert.AlertType.ERROR);
        }
    }

    private void cargarSucursales() {
        try {
            List<Sucursal> sucursales = sucursalDAO.buscarTodos();
            cb_sucursal.setItems(FXCollections.observableArrayList(sucursales));
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar las sucursales.", Alert.AlertType.ERROR);
        }
    }

    private void cargarDepartamentos() {
        Sucursal sucursal = cb_sucursal.getValue();
        cb_departamento.getItems().clear();
        cb_departamento.setDisable(sucursal == null);
        if (sucursal == null) return;

        try {
            List<Departamento> deptos = deptoDAO.buscarPorSucursal(sucursal.getNoSucursal());
            cb_departamento.setItems(FXCollections.observableArrayList(deptos));
            cb_departamento.setDisable(false);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar los departamentos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        String nombre    = txt_nombre.getText().trim();
        String paterno   = txt_paterno.getText().trim();
        String materno   = txt_materno.getText().trim();
        String direccion = txt_direccion.getText().trim();
        String telefono  = txt_telefono.getText().trim();
        String correo    = txt_correo.getText().trim();

        if (nombre.isEmpty() || paterno.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos incompletos",
                    "Nombre, apellido paterno, dirección y teléfono son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        if (!telefono.matches("\\d{10}")) {
            UtilidadesFX.mostrarAlertaSimple("Teléfono inválido",
                    "El teléfono debe tener exactamente 10 dígitos numéricos.", Alert.AlertType.WARNING);
            return;
        }
        if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")) {
            UtilidadesFX.mostrarAlertaSimple("Correo inválido",
                    "El correo electrónico no tiene un formato válido.", Alert.AlertType.WARNING);
            return;
        }
        if (cb_puesto.getValue() == null) {
            UtilidadesFX.mostrarAlertaSimple("Puesto requerido",
                    "Selecciona el puesto del empleado.", Alert.AlertType.WARNING);
            return;
        }
        if (cb_departamento.getValue() == null) {
            UtilidadesFX.mostrarAlertaSimple("Departamento requerido",
                    "Selecciona la sucursal y el departamento del empleado.", Alert.AlertType.WARNING);
            return;
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setPaterno(paterno);
        empleado.setMaterno(materno.isEmpty() ? null : materno);
        empleado.setDireccion(direccion);
        empleado.setTelefono(telefono);
        empleado.setCorreoElectronico(correo.isEmpty() ? null : correo);
        empleado.setPuesto(cb_puesto.getValue());
        empleado.setDepartamento(cb_departamento.getValue());

        try {
            empleadoDAO.registrar(empleado);
            UtilidadesFX.mostrarAlertaSimple("Registrado",
                    "Empleado registrado correctamente.", Alert.AlertType.INFORMATION);
            cerrar();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al registrar",
                    "No se pudo registrar el empleado:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txt_nombre.getScene().getWindow()).close();
    }
}
