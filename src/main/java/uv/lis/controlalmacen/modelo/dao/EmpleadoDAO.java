package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class EmpleadoDAO implements OperacionesCatalogoDAO<Empleado, Integer> {

    @Override
    public boolean registrar(Empleado empleado) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            conn.setAutoCommit(false);
            try {
                // 1. INSERT empleado — no_empleado es AI
                int noEmpleado;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO empleado(nombre, paterno, materno, direccion, correo_electronico, telefono) VALUES(?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, empleado.getNombre());
                    ps.setString(2, empleado.getPaterno());
                    ps.setString(3, empleado.getMaterno());
                    ps.setString(4, empleado.getDireccion());
                    ps.setString(5, empleado.getCorreoElectronico());
                    ps.setString(6, empleado.getTelefono());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se generó no_empleado");
                        noEmpleado = keys.getInt(1);
                    }
                }

                // 2. Asignar puesto
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO empleado_tiene_puesto(no_empleado, id_puesto, fecha) VALUES(?,?,CURDATE())")) {
                    ps.setInt(1, noEmpleado);
                    ps.setInt(2, empleado.getPuesto().getIdPuesto());
                    ps.executeUpdate();
                }

                // 3. Asignar departamento
                if (empleado.getDepartamento() != null && empleado.getDepartamento().getIdDepto() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO trabaja_en(no_empleado, id_depto, fecha) VALUES(?,?,CURDATE())")) {
                        ps.setInt(1, noEmpleado);
                        ps.setInt(2, empleado.getDepartamento().getIdDepto());
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean eliminar(Empleado empleado) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            conn.setAutoCommit(false);
            try {
                int no = empleado.getNoEmpleado();
                ejecutar(conn, "DELETE FROM trabaja_en WHERE no_empleado = ?", no);
                ejecutar(conn, "DELETE FROM empleado_tiene_puesto WHERE no_empleado = ?", no);
                ejecutar(conn, "DELETE FROM empleado WHERE no_empleado = ?", no);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean actualizar(Empleado empleado) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE empleado SET nombre=?, paterno=?, materno=?, direccion=?, correo_electronico=?, telefono=? WHERE no_empleado=?")) {
                ps.setString(1, empleado.getNombre());
                ps.setString(2, empleado.getPaterno());
                ps.setString(3, empleado.getMaterno());
                ps.setString(4, empleado.getDireccion());
                ps.setString(5, empleado.getCorreoElectronico());
                ps.setString(6, empleado.getTelefono());
                ps.setInt(7, empleado.getNoEmpleado());
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public List<Empleado> buscarTodos() throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        List<Empleado> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            String sql = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, " +
                         "correo_electronico, telefono, id_depto, departamento, no_sucursal, sucursal " +
                         "FROM vista_datos_empleados ORDER BY paterno, nombre";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public Empleado buscarUno(Integer id) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        Empleado empleado = new Empleado();
        try (Connection conn = ConnectionFactory.crearParaAutenticacion()) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            String sql = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, " +
                         "correo_electronico, telefono, id_depto, departamento, no_sucursal, sucursal " +
                         "FROM vista_datos_empleados WHERE no_empleado = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) empleado = mapear(rs);
            }
        }
        return empleado;
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        Sucursal sucursal = new Sucursal();
        sucursal.setNoSucursal(rs.getInt("no_sucursal"));
        sucursal.setNombre(rs.getString("sucursal"));

        Puesto puesto = new Puesto();
        puesto.setIdPuesto(rs.getInt("id_puesto"));
        puesto.setPuesto(rs.getString("puesto"));

        Departamento departamento = new Departamento();
        departamento.setIdDepto(rs.getInt("id_depto"));
        departamento.setDescripcion(rs.getString("departamento"));
        departamento.setSucursal(sucursal);

        Empleado e = new Empleado();
        e.setNoEmpleado(rs.getInt("no_empleado"));
        e.setNombre(rs.getString("nombre"));
        e.setPaterno(rs.getString("paterno"));
        e.setMaterno(rs.getString("materno"));
        e.setDireccion(rs.getString("direccion"));
        e.setCorreoElectronico(rs.getString("correo_electronico"));
        e.setTelefono(rs.getString("telefono"));
        e.setPuesto(puesto);
        e.setDepartamento(departamento);
        return e;
    }

    private void ejecutar(Connection conn, String sql, int param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ps.executeUpdate();
        }
    }
}
