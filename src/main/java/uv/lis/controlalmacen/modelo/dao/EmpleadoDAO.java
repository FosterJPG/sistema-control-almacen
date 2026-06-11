package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Puesto;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class EmpleadoDAO implements OperacionesCatalogoDAO<Empleado, Integer> {

    private static final String PUESTO_ENCARGADO = "Gerente Administrativo";
    private static final String DEPARTAMENTO_ADMINISTRACION = "Administracion";

    @Override
    public boolean registrar(Empleado empleado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        if (empleado.getPuesto() == null || empleado.getPuesto().getIdPuesto() == null) {
            throw new SQLException("Debe seleccionar un puesto para el empleado.");
        }

        if (empleado.getDepartamento() == null || empleado.getDepartamento().getIdDepto() == null) {
            throw new SQLException("Debe seleccionar un departamento para el empleado.");
        }

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            conn.setAutoCommit(false);

            try {
                String consultaEmpleado = "INSERT INTO empleado(nombre, paterno, materno, direccion, correo_electronico, telefono) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement sentenciaEmpleado = conn.prepareStatement(
                        consultaEmpleado,
                        Statement.RETURN_GENERATED_KEYS
                );

                sentenciaEmpleado.setString(1, empleado.getNombre());
                sentenciaEmpleado.setString(2, empleado.getPaterno());
                sentenciaEmpleado.setString(3, empleado.getMaterno());
                sentenciaEmpleado.setString(4, empleado.getDireccion());
                asignarCorreo(sentenciaEmpleado, 5, empleado.getCorreoElectronico());
                sentenciaEmpleado.setString(6, empleado.getTelefono());

                int filasAfectadas = sentenciaEmpleado.executeUpdate();

                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }

                ResultSet clavesGeneradas = sentenciaEmpleado.getGeneratedKeys();

                if (!clavesGeneradas.next()) {
                    conn.rollback();
                    throw new SQLException("No se pudo obtener el número del empleado registrado.");
                }

                Integer noEmpleado = clavesGeneradas.getInt(1);
                empleado.setNoEmpleado(noEmpleado);

                registrarPuestoEmpleado(conn, noEmpleado, empleado.getPuesto().getIdPuesto());
                registrarDepartamentoEmpleado(conn, noEmpleado, empleado.getDepartamento().getIdDepto());

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean eliminar(Empleado empleado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        if (empleado == null || empleado.getNoEmpleado() == null) {
            throw new SQLException("No hay un empleado seleccionado para eliminar.");
        }

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (tieneUsuarioAsociado(conn, empleado.getNoEmpleado())) {
                throw new SQLException("No se puede eliminar el empleado porque tiene un usuario asociado.");
            }

            if (tieneSolicitudesAsociadas(conn, empleado.getNoEmpleado())) {
                throw new SQLException("No se puede eliminar el empleado porque tiene solicitudes asociadas.");
            }

            conn.setAutoCommit(false);

            try {
                String eliminarTrabajaEn = "DELETE FROM trabaja_en WHERE no_empleado = ?";
                PreparedStatement sentenciaTrabajaEn = conn.prepareStatement(eliminarTrabajaEn);
                sentenciaTrabajaEn.setInt(1, empleado.getNoEmpleado());
                sentenciaTrabajaEn.executeUpdate();

                String eliminarPuesto = "DELETE FROM empleado_tiene_puesto WHERE no_empleado = ?";
                PreparedStatement sentenciaPuesto = conn.prepareStatement(eliminarPuesto);
                sentenciaPuesto.setInt(1, empleado.getNoEmpleado());
                sentenciaPuesto.executeUpdate();

                String eliminarEmpleado = "DELETE FROM empleado WHERE no_empleado = ?";
                PreparedStatement sentenciaEmpleado = conn.prepareStatement(eliminarEmpleado);
                sentenciaEmpleado.setInt(1, empleado.getNoEmpleado());

                boolean eliminado = sentenciaEmpleado.executeUpdate() > 0;

                conn.commit();
                return eliminado;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean actualizar(Empleado empleado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        if (empleado == null || empleado.getNoEmpleado() == null) {
            throw new SQLException("No hay un empleado seleccionado para modificar.");
        }

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            conn.setAutoCommit(false);

            try {
                String consultaEmpleado = "UPDATE empleado " +
                        "SET nombre = ?, paterno = ?, materno = ?, direccion = ?, correo_electronico = ?, telefono = ? " +
                        "WHERE no_empleado = ?";

                PreparedStatement sentenciaEmpleado = conn.prepareStatement(consultaEmpleado);
                sentenciaEmpleado.setString(1, empleado.getNombre());
                sentenciaEmpleado.setString(2, empleado.getPaterno());
                sentenciaEmpleado.setString(3, empleado.getMaterno());
                sentenciaEmpleado.setString(4, empleado.getDireccion());
                asignarCorreo(sentenciaEmpleado, 5, empleado.getCorreoElectronico());
                sentenciaEmpleado.setString(6, empleado.getTelefono());
                sentenciaEmpleado.setInt(7, empleado.getNoEmpleado());

                boolean actualizado = sentenciaEmpleado.executeUpdate() > 0;

                if (empleado.getPuesto() != null && empleado.getPuesto().getIdPuesto() != null) {
                    Integer puestoActual = obtenerPuestoActual(conn, empleado.getNoEmpleado());

                    if (puestoActual == null || !puestoActual.equals(empleado.getPuesto().getIdPuesto())) {
                        registrarPuestoEmpleado(conn, empleado.getNoEmpleado(), empleado.getPuesto().getIdPuesto());
                    }
                }

                if (empleado.getDepartamento() != null && empleado.getDepartamento().getIdDepto() != null) {
                    Integer departamentoActual = obtenerDepartamentoActual(conn, empleado.getNoEmpleado());

                    if (departamentoActual == null || !departamentoActual.equals(empleado.getDepartamento().getIdDepto())) {
                        registrarDepartamentoEmpleado(conn, empleado.getNoEmpleado(), empleado.getDepartamento().getIdDepto());
                    }
                }

                conn.commit();
                return actualizado;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Empleado> buscarTodos()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "ORDER BY nombre, paterno, materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                empleados.add(construirEmpleado(resultado));
            }
        }

        return empleados;
    }

    @Override
    public Empleado buscarUno(Integer id)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Empleado empleado = new Empleado();

        try (Connection conn = ConnectionFactory.crearParaAutenticacion()) {
            if (conn == null) {
                throw new SQLException("Error: No se pudo conectar a la base de datos");
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE no_empleado = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                empleado = construirEmpleado(resultado);
            }
        }

        return empleado;
    }

    public Empleado buscarUnoPorRol(Integer id)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Empleado empleado = new Empleado();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE no_empleado = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                empleado = construirEmpleado(resultado);
            }
        }

        return empleado;
    }

    public List<Empleado> buscarPorNombre(String nombre)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE nombre LIKE ? OR paterno LIKE ? OR materno LIKE ? " +
                    "ORDER BY nombre, paterno, materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, "%" + nombre + "%");
            sentencia.setString(2, "%" + nombre + "%");
            sentencia.setString(3, "%" + nombre + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                empleados.add(construirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public List<Empleado> buscarEncargados()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE puesto = ? AND departamento = ? " +
                    "ORDER BY sucursal, nombre, paterno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, PUESTO_ENCARGADO);
            sentencia.setString(2, DEPARTAMENTO_ADMINISTRACION);

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                empleados.add(construirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public List<Empleado> buscarEncargadosPorNombre(String nombre)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE puesto = ? " +
                    "AND departamento = ? " +
                    "AND (nombre LIKE ? OR paterno LIKE ? OR materno LIKE ?) " +
                    "ORDER BY sucursal, nombre, paterno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, PUESTO_ENCARGADO);
            sentencia.setString(2, DEPARTAMENTO_ADMINISTRACION);
            sentencia.setString(3, "%" + nombre + "%");
            sentencia.setString(4, "%" + nombre + "%");
            sentencia.setString(5, "%" + nombre + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                empleados.add(construirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public List<Empleado> buscarEmpleadosPorSucursal(Integer noSucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE no_sucursal = ? AND puesto <> ? " +
                    "ORDER BY nombre, paterno, materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, noSucursal);
            sentencia.setString(2, PUESTO_ENCARGADO);

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                empleados.add(construirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public List<Empleado> buscarEmpleadosPorNombreYSucursal(String nombre, Integer noSucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                    "telefono, id_depto, departamento, no_sucursal, sucursal " +
                    "FROM vista_datos_empleados " +
                    "WHERE no_sucursal = ? " +
                    "AND puesto <> ? " +
                    "AND (nombre LIKE ? OR paterno LIKE ? OR materno LIKE ?) " +
                    "ORDER BY nombre, paterno, materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, noSucursal);
            sentencia.setString(2, PUESTO_ENCARGADO);
            sentencia.setString(3, "%" + nombre + "%");
            sentencia.setString(4, "%" + nombre + "%");
            sentencia.setString(5, "%" + nombre + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                empleados.add(construirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public List<Puesto> buscarPuestosEmpleado()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Puesto> puestos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_puesto, puesto " +
                    "FROM puesto " +
                    "WHERE puesto <> ? " +
                    "ORDER BY puesto";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, PUESTO_ENCARGADO);

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Puesto puesto = new Puesto();
                puesto.setIdPuesto(resultado.getInt("id_puesto"));
                puesto.setPuesto(resultado.getString("puesto"));
                puestos.add(puesto);
            }
        }

        return puestos;
    }

    public Puesto buscarPuestoEncargado()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Puesto puesto = new Puesto();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_puesto, puesto " +
                    "FROM puesto " +
                    "WHERE puesto = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, PUESTO_ENCARGADO);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                puesto.setIdPuesto(resultado.getInt("id_puesto"));
                puesto.setPuesto(resultado.getString("puesto"));
            }
        }

        return puesto;
    }

    public List<Sucursal> buscarSucursales()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Sucursal> sucursales = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_sucursal, nombre, direccion, telefono " +
                    "FROM sucursal " +
                    "ORDER BY nombre";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setNoSucursal(resultado.getInt("no_sucursal"));
                sucursal.setNombre(resultado.getString("nombre"));
                sucursal.setDireccion(resultado.getString("direccion"));
                sucursal.setTelefono(resultado.getString("telefono"));
                sucursales.add(sucursal);
            }
        }

        return sucursales;
    }

    public List<Departamento> buscarDepartamentosPorSucursal(Integer noSucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Departamento> departamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT d.id_depto, d.descripcion, s.no_sucursal, s.nombre AS sucursal " +
                    "FROM departamento d " +
                    "JOIN sucursal s ON d.no_sucursal = s.no_sucursal " +
                    "WHERE d.no_sucursal = ? " +
                    "ORDER BY d.descripcion";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, noSucursal);

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setNoSucursal(resultado.getInt("no_sucursal"));
                sucursal.setNombre(resultado.getString("sucursal"));

                Departamento departamento = new Departamento();
                departamento.setIdDepto(resultado.getInt("id_depto"));
                departamento.setDescripcion(resultado.getString("descripcion"));
                departamento.setSucursal(sucursal);

                departamentos.add(departamento);
            }
        }

        return departamentos;
    }

    public Departamento buscarDepartamentoAdministracionPorSucursal(Integer noSucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Departamento departamento = new Departamento();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT d.id_depto, d.descripcion, s.no_sucursal, s.nombre AS sucursal " +
                    "FROM departamento d " +
                    "JOIN sucursal s ON d.no_sucursal = s.no_sucursal " +
                    "WHERE d.no_sucursal = ? AND d.descripcion = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, noSucursal);
            sentencia.setString(2, DEPARTAMENTO_ADMINISTRACION);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setNoSucursal(resultado.getInt("no_sucursal"));
                sucursal.setNombre(resultado.getString("sucursal"));

                departamento.setIdDepto(resultado.getInt("id_depto"));
                departamento.setDescripcion(resultado.getString("descripcion"));
                departamento.setSucursal(sucursal);
            }
        }

        return departamento;
    }

    private void registrarPuestoEmpleado(Connection conn, Integer noEmpleado, Integer idPuesto)
            throws SQLException {

        String consulta = "INSERT INTO empleado_tiene_puesto(no_empleado, id_puesto, fecha) " +
                "VALUES (?, ?, CURDATE())";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noEmpleado);
        sentencia.setInt(2, idPuesto);
        sentencia.executeUpdate();
    }

    private void registrarDepartamentoEmpleado(Connection conn, Integer noEmpleado, Integer idDepto)
            throws SQLException {

        String consulta = "INSERT INTO trabaja_en(no_empleado, id_depto, fecha) " +
                "VALUES (?, ?, CURDATE())";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noEmpleado);
        sentencia.setInt(2, idDepto);
        sentencia.executeUpdate();
    }

    private Integer obtenerPuestoActual(Connection conn, Integer noEmpleado)
            throws SQLException {

        String consulta = "SELECT id_puesto " +
                "FROM vista_datos_empleados " +
                "WHERE no_empleado = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noEmpleado);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("id_puesto");
        }

        return null;
    }

    private Integer obtenerDepartamentoActual(Connection conn, Integer noEmpleado)
            throws SQLException {

        String consulta = "SELECT id_depto " +
                "FROM vista_datos_empleados " +
                "WHERE no_empleado = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noEmpleado);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("id_depto");
        }

        return null;
    }

    private boolean tieneUsuarioAsociado(Connection conn, Integer noEmpleado)
            throws SQLException {

        String consulta = "SELECT COUNT(*) AS total " +
                "FROM usuario " +
                "WHERE no_empleado = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noEmpleado);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }

    private boolean tieneSolicitudesAsociadas(Connection conn, Integer noEmpleado)
            throws SQLException {

        String consulta = "SELECT COUNT(*) AS total " +
                "FROM solicitud " +
                "WHERE no_empleado = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noEmpleado);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }

    private void asignarCorreo(PreparedStatement sentencia, Integer posicion, String correo)
            throws SQLException {

        if (correo == null || correo.trim().isEmpty()) {
            sentencia.setNull(posicion, Types.VARCHAR);
        } else {
            sentencia.setString(posicion, correo.trim());
        }
    }

    private Empleado construirEmpleado(ResultSet resultado)
            throws SQLException {

        Sucursal sucursal = new Sucursal();
        sucursal.setNoSucursal(resultado.getInt("no_sucursal"));
        sucursal.setNombre(resultado.getString("sucursal"));

        Puesto puesto = new Puesto();
        puesto.setIdPuesto(resultado.getInt("id_puesto"));
        puesto.setPuesto(resultado.getString("puesto"));

        Departamento departamento = new Departamento();
        departamento.setIdDepto(resultado.getInt("id_depto"));
        departamento.setDescripcion(resultado.getString("departamento"));
        departamento.setSucursal(sucursal);

        Empleado empleado = new Empleado();
        empleado.setNoEmpleado(resultado.getInt("no_empleado"));
        empleado.setNombre(resultado.getString("nombre"));
        empleado.setPaterno(resultado.getString("paterno"));
        empleado.setMaterno(resultado.getString("materno"));
        empleado.setDireccion(resultado.getString("direccion"));
        empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
        empleado.setTelefono(resultado.getString("telefono"));
        empleado.setPuesto(puesto);
        empleado.setDepartamento(departamento);

        return empleado;
    }
}