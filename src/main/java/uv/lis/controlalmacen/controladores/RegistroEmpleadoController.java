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
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uv.lis.controlalmacen.modelo.dao.EmpleadoDAO;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Puesto;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;
import uv.lis.controlalmacen.utilidades.Constantes;
import uv.lis.controlalmacen.utilidades.UtilidadesFX;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class RegistroEmpleadoController implements Initializable {

    private enum ModoFormulario {
        REGISTRO_ENCARGADO,
        EDICION_ENCARGADO,
        REGISTRO_EMPLEADO,
        EDICION_EMPLEADO
    }

    @FXML
    private Label lbl_titulo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_paterno;
    @FXML
    private TextField txt_materno;
    @FXML
    private TextField txt_direccion;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_correo;
    @FXML
    private Label lbl_puesto;
    @FXML
    private ComboBox<Puesto> cb_puesto;
    @FXML
    private Label lbl_sucursal;
    @FXML
    private ComboBox<Sucursal> cb_sucursal;
    @FXML
    private Label lbl_departamento;
    @FXML
    private ComboBox<Departamento> cb_departamento;
    @FXML
    private Label lbl_mensajeError;
    
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    private final ObservableList<Puesto> puestosBase = FXCollections.observableArrayList();
    private final ObservableList<Sucursal> sucursalesBase = FXCollections.observableArrayList();
    private final ObservableList<Departamento> departamentosBase = FXCollections.observableArrayList();

    private ModoFormulario modoFormulario;
    private Empleado empleadoEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_mensajeError.setWrapText(true);
        lbl_mensajeError.setText("");

        configurarCampoTelefono();

        configurarComboPuestos();
        configurarComboSucursales();
        configurarComboDepartamentos();

        configurarBusquedaPuestos();
        configurarBusquedaSucursales();
        configurarBusquedaDepartamentos();
    }

    public void inicializarRegistroEncargado() {
        modoFormulario = ModoFormulario.REGISTRO_ENCARGADO;
        empleadoEdicion = null;

        lbl_titulo.setText("Registrar Encargado");
        limpiarCampos();

        mostrarControlesSucursal(true);
        mostrarControlesPuesto(false);
        mostrarControlesDepartamento(false);

        cargarSucursales();
    }

    public void inicializarEdicionEncargado(Empleado empleado) {
        if (empleado == null) {
            return;
        }

        modoFormulario = ModoFormulario.EDICION_ENCARGADO;
        empleadoEdicion = empleado;

        lbl_titulo.setText("Modificar Encargado");
        limpiarCampos();

        mostrarControlesSucursal(true);
        mostrarControlesPuesto(false);
        mostrarControlesDepartamento(false);

        cargarSucursales();
        cargarDatosEmpleado(empleado);
        seleccionarSucursal(empleado);
    }

    public void inicializarRegistroEmpleado() {
        modoFormulario = ModoFormulario.REGISTRO_EMPLEADO;
        empleadoEdicion = null;

        lbl_titulo.setText("Registrar Empleado");
        limpiarCampos();

        mostrarControlesSucursal(false);
        mostrarControlesPuesto(true);
        mostrarControlesDepartamento(true);

        cargarPuestosEmpleado();
        cargarDepartamentosSucursalActual();
    }

    public void inicializarEdicionEmpleado(Empleado empleado) {
        if (empleado == null) {
            return;
        }

        modoFormulario = ModoFormulario.EDICION_EMPLEADO;
        empleadoEdicion = empleado;

        lbl_titulo.setText("Modificar Empleado");
        limpiarCampos();

        mostrarControlesSucursal(false);
        mostrarControlesPuesto(true);
        mostrarControlesDepartamento(true);

        cargarPuestosEmpleado();
        cargarDepartamentosSucursalActual();

        cargarDatosEmpleado(empleado);
        seleccionarPuesto(empleado);
        seleccionarDepartamento(empleado);
    }

    private void configurarCampoTelefono() {
        UnaryOperator<TextFormatter.Change> filtroTelefono = change -> {
            String textoNuevo = change.getControlNewText();

            if (textoNuevo.matches("\\d{0,10}")) {
                return change;
            }

            return null;
        };

        txt_telefono.setTextFormatter(new TextFormatter<>(filtroTelefono));
    }

    private void configurarComboPuestos() {
        cb_puesto.setConverter(new StringConverter<Puesto>() {
            @Override
            public String toString(Puesto puesto) {
                if (puesto == null) {
                    return "";
                }

                return puesto.getPuesto();
            }

            @Override
            public Puesto fromString(String texto) {
                return buscarPuestoPorTexto(texto);
            }
        });

        cb_puesto.setCellFactory(listView -> new ListCell<Puesto>() {
            @Override
            protected void updateItem(Puesto puesto, boolean empty) {
                super.updateItem(puesto, empty);

                if (empty || puesto == null) {
                    setText(null);
                } else {
                    setText(puesto.getPuesto());
                }
            }
        });

        cb_puesto.setButtonCell(new ListCell<Puesto>() {
            @Override
            protected void updateItem(Puesto puesto, boolean empty) {
                super.updateItem(puesto, empty);

                if (empty || puesto == null) {
                    setText(null);
                } else {
                    setText(puesto.getPuesto());
                }
            }
        });
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

    private void configurarComboDepartamentos() {
        cb_departamento.setConverter(new StringConverter<Departamento>() {
            @Override
            public String toString(Departamento departamento) {
                if (departamento == null) {
                    return "";
                }

                return departamento.getDescripcion();
            }

            @Override
            public Departamento fromString(String texto) {
                return buscarDepartamentoPorTexto(texto);
            }
        });

        cb_departamento.setCellFactory(listView -> new ListCell<Departamento>() {
            @Override
            protected void updateItem(Departamento departamento, boolean empty) {
                super.updateItem(departamento, empty);

                if (empty || departamento == null) {
                    setText(null);
                } else {
                    setText(departamento.getDescripcion());
                }
            }
        });

        cb_departamento.setButtonCell(new ListCell<Departamento>() {
            @Override
            protected void updateItem(Departamento departamento, boolean empty) {
                super.updateItem(departamento, empty);

                if (empty || departamento == null) {
                    setText(null);
                } else {
                    setText(departamento.getDescripcion());
                }
            }
        });
    }

    private void configurarBusquedaPuestos() {
        cb_puesto.getEditor().textProperty().addListener((observable, textoAnterior, textoNuevo) -> {
            if (!cb_puesto.getEditor().isFocused()) {
                return;
            }

            filtrarPuestos(textoNuevo);
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

    private void configurarBusquedaDepartamentos() {
        cb_departamento.getEditor().textProperty().addListener((observable, textoAnterior, textoNuevo) -> {
            if (!cb_departamento.getEditor().isFocused()) {
                return;
            }

            filtrarDepartamentos(textoNuevo);
        });
    }

    private void filtrarPuestos(String texto) {
        List<Puesto> puestosFiltrados = new ArrayList<>();

        if (texto == null || texto.trim().isEmpty()) {
            puestosFiltrados.addAll(puestosBase);
        } else {
            String textoBuscado = texto.trim().toLowerCase();

            for (Puesto puesto : puestosBase) {
                if (puesto.getPuesto().toLowerCase().contains(textoBuscado)) {
                    puestosFiltrados.add(puesto);
                }
            }
        }

        cb_puesto.setItems(FXCollections.observableArrayList(puestosFiltrados));
        cb_puesto.show();
        cb_puesto.getEditor().positionCaret(cb_puesto.getEditor().getText().length());
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

    private void filtrarDepartamentos(String texto) {
        List<Departamento> departamentosFiltrados = new ArrayList<>();

        if (texto == null || texto.trim().isEmpty()) {
            departamentosFiltrados.addAll(departamentosBase);
        } else {
            String textoBuscado = texto.trim().toLowerCase();

            for (Departamento departamento : departamentosBase) {
                if (departamento.getDescripcion().toLowerCase().contains(textoBuscado)) {
                    departamentosFiltrados.add(departamento);
                }
            }
        }

        cb_departamento.setItems(FXCollections.observableArrayList(departamentosFiltrados));
        cb_departamento.show();
        cb_departamento.getEditor().positionCaret(cb_departamento.getEditor().getText().length());
    }

    private void cargarSucursales() {
        try {
            List<Sucursal> sucursales = empleadoDAO.buscarSucursales();

            sucursalesBase.clear();
            sucursalesBase.addAll(sucursales);

            cb_sucursal.setItems(sucursalesBase);

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar sucursales",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void cargarPuestosEmpleado() {
        try {
            List<Puesto> puestos = empleadoDAO.buscarPuestosEmpleado();

            puestosBase.clear();
            puestosBase.addAll(puestos);

            cb_puesto.setItems(puestosBase);

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar puestos",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void cargarDepartamentosSucursalActual() {
        try {
            Integer noSucursal = Sesion.getUsuarioActual()
                    .getEmpleado()
                    .getDepartamento()
                    .getSucursal()
                    .getNoSucursal();

            List<Departamento> departamentos = empleadoDAO.buscarDepartamentosPorSucursal(noSucursal);

            departamentosBase.clear();
            departamentosBase.addAll(departamentos);

            cb_departamento.setItems(departamentosBase);

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al cargar departamentos",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void mostrarControlesPuesto(boolean visible) {
        lbl_puesto.setVisible(visible);
        lbl_puesto.setManaged(visible);
        cb_puesto.setVisible(visible);
        cb_puesto.setManaged(visible);
    }

    private void mostrarControlesSucursal(boolean visible) {
        lbl_sucursal.setVisible(visible);
        lbl_sucursal.setManaged(visible);
        cb_sucursal.setVisible(visible);
        cb_sucursal.setManaged(visible);
    }

    private void mostrarControlesDepartamento(boolean visible) {
        lbl_departamento.setVisible(visible);
        lbl_departamento.setManaged(visible);
        cb_departamento.setVisible(visible);
        cb_departamento.setManaged(visible);
    }

    private void cargarDatosEmpleado(Empleado empleado) {
        txt_nombre.setText(empleado.getNombre());
        txt_paterno.setText(empleado.getPaterno());
        txt_materno.setText(empleado.getMaterno());
        txt_direccion.setText(empleado.getDireccion());
        txt_telefono.setText(empleado.getTelefono());
        txt_correo.setText(empleado.getCorreoElectronico());
        lbl_mensajeError.setText("");
    }

    private void seleccionarSucursal(Empleado empleado) {
        if (empleado.getDepartamento() == null
                || empleado.getDepartamento().getSucursal() == null
                || empleado.getDepartamento().getSucursal().getNoSucursal() == null) {
            return;
        }

        cb_sucursal.setItems(sucursalesBase);

        Integer noSucursal = empleado.getDepartamento().getSucursal().getNoSucursal();

        for (Sucursal sucursal : sucursalesBase) {
            if (sucursal.getNoSucursal().equals(noSucursal)) {
                cb_sucursal.getSelectionModel().select(sucursal);
                return;
            }
        }
    }

    private void seleccionarPuesto(Empleado empleado) {
        if (empleado.getPuesto() == null || empleado.getPuesto().getIdPuesto() == null) {
            return;
        }

        cb_puesto.setItems(puestosBase);

        Integer idPuesto = empleado.getPuesto().getIdPuesto();

        for (Puesto puesto : puestosBase) {
            if (puesto.getIdPuesto().equals(idPuesto)) {
                cb_puesto.getSelectionModel().select(puesto);
                return;
            }
        }
    }

    private void seleccionarDepartamento(Empleado empleado) {
        if (empleado.getDepartamento() == null || empleado.getDepartamento().getIdDepto() == null) {
            return;
        }

        cb_departamento.setItems(departamentosBase);

        Integer idDepto = empleado.getDepartamento().getIdDepto();

        for (Departamento departamento : departamentosBase) {
            if (departamento.getIdDepto().equals(idDepto)) {
                cb_departamento.getSelectionModel().select(departamento);
                return;
            }
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        lbl_mensajeError.setText("");

        if (modoFormulario == null) {
            lbl_mensajeError.setText("No se pudo determinar el tipo de registro.");
            return;
        }

        String nombre = obtenerTexto(txt_nombre);
        String paterno = obtenerTexto(txt_paterno);
        String materno = obtenerTexto(txt_materno);
        String direccion = obtenerTexto(txt_direccion);
        String telefono = obtenerTexto(txt_telefono);
        String correo = obtenerTexto(txt_correo);

        String mensajeValidacion = validarDatos(nombre, paterno, direccion, telefono, correo);

        if (!mensajeValidacion.isEmpty()) {
            lbl_mensajeError.setText(mensajeValidacion);
            return;
        }

        if (esModoEncargado()) {
            guardarEncargado(nombre, paterno, materno, direccion, telefono, correo);
        } else {
            guardarEmpleado(nombre, paterno, materno, direccion, telefono, correo);
        }
    }

    private void guardarEncargado(String nombre, String paterno, String materno,
                                  String direccion, String telefono, String correo) {

        Sucursal sucursalSeleccionada = obtenerSucursalSeleccionada();

        if (sucursalSeleccionada == null) {
            lbl_mensajeError.setText("Seleccione una sucursal existente para el encargado.");
            return;
        }

        try {
            Puesto puestoEncargado = empleadoDAO.buscarPuestoEncargado();

            if (puestoEncargado.getIdPuesto() == null) {
                lbl_mensajeError.setText("No se encontró el puesto Gerente Administrativo.");
                return;
            }

            Departamento departamentoAdministracion =
                    empleadoDAO.buscarDepartamentoAdministracionPorSucursal(sucursalSeleccionada.getNoSucursal());

            if (departamentoAdministracion.getIdDepto() == null) {
                lbl_mensajeError.setText("No se encontró el departamento Administración para la sucursal seleccionada.");
                return;
            }

            Empleado empleado = construirEmpleado(nombre, paterno, materno, direccion, telefono, correo);
            empleado.setPuesto(puestoEncargado);
            empleado.setDepartamento(departamentoAdministracion);

            boolean exito;

            if (modoFormulario == ModoFormulario.EDICION_ENCARGADO) {
                if (empleadoEdicion == null || empleadoEdicion.getNoEmpleado() == null) {
                    lbl_mensajeError.setText("No hay un encargado seleccionado para modificar.");
                    return;
                }

                empleado.setNoEmpleado(empleadoEdicion.getNoEmpleado());
                exito = empleadoDAO.actualizar(empleado);
            } else {
                exito = empleadoDAO.registrar(empleado);
            }

            if (exito) {
                UtilidadesFX.mostrarAlertaSimple(
                        modoFormulario == ModoFormulario.EDICION_ENCARGADO
                                ? "Actualización exitosa"
                                : "Registro exitoso",
                        modoFormulario == ModoFormulario.EDICION_ENCARGADO
                                ? "El encargado se actualizó correctamente."
                                : "El encargado se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al guardar encargado",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private void guardarEmpleado(String nombre, String paterno, String materno,
                                 String direccion, String telefono, String correo) {

        Puesto puestoSeleccionado = obtenerPuestoSeleccionado();
        Departamento departamentoSeleccionado = obtenerDepartamentoSeleccionado();

        if (puestoSeleccionado == null) {
            lbl_mensajeError.setText("Seleccione un puesto existente para el empleado.");
            return;
        }

        if (departamentoSeleccionado == null) {
            lbl_mensajeError.setText("Seleccione un departamento existente para el empleado.");
            return;
        }

        try {
            Empleado empleado = construirEmpleado(nombre, paterno, materno, direccion, telefono, correo);
            empleado.setPuesto(puestoSeleccionado);
            empleado.setDepartamento(departamentoSeleccionado);

            boolean exito;

            if (modoFormulario == ModoFormulario.EDICION_EMPLEADO) {
                if (empleadoEdicion == null || empleadoEdicion.getNoEmpleado() == null) {
                    lbl_mensajeError.setText("No hay un empleado seleccionado para modificar.");
                    return;
                }

                empleado.setNoEmpleado(empleadoEdicion.getNoEmpleado());
                exito = empleadoDAO.actualizar(empleado);
            } else {
                exito = empleadoDAO.registrar(empleado);
            }

            if (exito) {
                UtilidadesFX.mostrarAlertaSimple(
                        modoFormulario == ModoFormulario.EDICION_EMPLEADO
                                ? "Actualización exitosa"
                                : "Registro exitoso",
                        modoFormulario == ModoFormulario.EDICION_EMPLEADO
                                ? "El empleado se actualizó correctamente."
                                : "El empleado se registró correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();
            }

        } catch (SQLException ex) {
            lbl_mensajeError.setText(ex.getMessage());
        } catch (NullPointerException | ClassNotFoundException | IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al guardar empleado",
                    Constantes.MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.WARNING
            );
        }
    }

    private Empleado construirEmpleado(String nombre, String paterno, String materno,
                                       String direccion, String telefono, String correo) {

        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setPaterno(paterno);
        empleado.setMaterno(materno);
        empleado.setDireccion(direccion);
        empleado.setTelefono(telefono);

        if (correo.isEmpty()) {
            empleado.setCorreoElectronico(null);
        } else {
            empleado.setCorreoElectronico(correo);
        }

        return empleado;
    }

    private String validarDatos(String nombre, String paterno, String direccion,
                                String telefono, String correo) {

        StringBuilder mensaje = new StringBuilder();

        if (nombre.isEmpty()) {
            mensaje.append("Ingrese el nombre del empleado.\n");
        }

        if (paterno.isEmpty()) {
            mensaje.append("Ingrese el apellido paterno.\n");
        }

        if (direccion.isEmpty()) {
            mensaje.append("Ingrese la dirección del empleado.\n");
        }

        if (telefono.isEmpty()) {
            mensaje.append("Ingrese el teléfono del empleado.\n");
        } else if (!telefono.matches("^[0-9]{10}$")) {
            mensaje.append("El teléfono debe contener exactamente 10 dígitos.\n");
        }

        if (!correo.isEmpty()
                && !correo.matches("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")) {
            mensaje.append("El correo electrónico no tiene un formato válido.\n");
        }

        return mensaje.toString();
    }

    private boolean esModoEncargado() {
        return modoFormulario == ModoFormulario.REGISTRO_ENCARGADO
                || modoFormulario == ModoFormulario.EDICION_ENCARGADO;
    }

    private Puesto obtenerPuestoSeleccionado() {
        Puesto puesto = cb_puesto.getValue();

        if (puesto != null) {
            return puesto;
        }

        String texto = cb_puesto.getEditor().getText();
        return buscarPuestoPorTexto(texto);
    }

    private Sucursal obtenerSucursalSeleccionada() {
        Sucursal sucursal = cb_sucursal.getValue();

        if (sucursal != null) {
            return sucursal;
        }

        String texto = cb_sucursal.getEditor().getText();
        return buscarSucursalPorTexto(texto);
    }

    private Departamento obtenerDepartamentoSeleccionado() {
        Departamento departamento = cb_departamento.getValue();

        if (departamento != null) {
            return departamento;
        }

        String texto = cb_departamento.getEditor().getText();
        return buscarDepartamentoPorTexto(texto);
    }

    private Puesto buscarPuestoPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        String textoBuscado = texto.trim();

        for (Puesto puesto : puestosBase) {
            if (puesto.getPuesto().equalsIgnoreCase(textoBuscado)) {
                return puesto;
            }
        }

        return null;
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

    private Departamento buscarDepartamentoPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        String textoBuscado = texto.trim();

        for (Departamento departamento : departamentosBase) {
            if (departamento.getDescripcion().equalsIgnoreCase(textoBuscado)) {
                return departamento;
            }
        }

        return null;
    }

    private String obtenerTexto(TextField textField) {
        if (textField.getText() == null) {
            return "";
        }

        return textField.getText().trim();
    }

    private void limpiarCampos() {
        txt_nombre.clear();
        txt_paterno.clear();
        txt_materno.clear();
        txt_direccion.clear();
        txt_telefono.clear();
        txt_correo.clear();

        cb_puesto.setItems(puestosBase);
        cb_puesto.getSelectionModel().clearSelection();
        cb_puesto.getEditor().clear();

        cb_sucursal.setItems(sucursalesBase);
        cb_sucursal.getSelectionModel().clearSelection();
        cb_sucursal.getEditor().clear();

        cb_departamento.setItems(departamentosBase);
        cb_departamento.getSelectionModel().clearSelection();
        cb_departamento.getEditor().clear();

        lbl_mensajeError.setText("");
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txt_nombre.getScene().getWindow();
        stage.close();
    }
}