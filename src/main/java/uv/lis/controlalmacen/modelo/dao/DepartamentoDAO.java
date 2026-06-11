package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class DepartamentoDAO implements OperacionesCatalogoDAO<Departamento, Integer> {

    @Override
    public boolean registrar(Departamento departamento)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (existeDescripcionEnSucursal(
                    conn,
                    departamento.getDescripcion(),
                    departamento.getSucursal().getNoSucursal(),
                    null)) {
                throw new SQLException("Ya existe un departamento con esa descripción en la sucursal seleccionada.");
            }

            String consulta = "INSERT INTO departamento(descripcion, no_sucursal) VALUES (?, ?)";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, departamento.getDescripcion());
            sentencia.setInt(2, departamento.getSucursal().getNoSucursal());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(Departamento departamento)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (tieneEmpleadosAsociados(conn, departamento.getIdDepto())) {
                throw new SQLException("No se puede eliminar el departamento porque tiene empleados asociados.");
            }

            String consulta = "DELETE FROM departamento WHERE id_depto = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, departamento.getIdDepto());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Departamento departamento)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (existeDescripcionEnSucursal(
                    conn,
                    departamento.getDescripcion(),
                    departamento.getSucursal().getNoSucursal(),
                    departamento.getIdDepto())) {
                throw new SQLException("Ya existe otro departamento con esa descripción en la sucursal seleccionada.");
            }

            String consulta = "UPDATE departamento " +
                    "SET descripcion = ?, no_sucursal = ? " +
                    "WHERE id_depto = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, departamento.getDescripcion());
            sentencia.setInt(2, departamento.getSucursal().getNoSucursal());
            sentencia.setInt(3, departamento.getIdDepto());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public List<Departamento> buscarTodos()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Departamento> departamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT d.id_depto, d.descripcion, s.no_sucursal, s.nombre AS sucursal " +
                    "FROM departamento d " +
                    "JOIN sucursal s ON d.no_sucursal = s.no_sucursal " +
                    "ORDER BY s.nombre, d.descripcion";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
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

    @Override
    public Departamento buscarUno(Integer id)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Departamento departamento = new Departamento();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT d.id_depto, d.descripcion, s.no_sucursal, s.nombre AS sucursal " +
                    "FROM departamento d " +
                    "JOIN sucursal s ON d.no_sucursal = s.no_sucursal " +
                    "WHERE d.id_depto = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);

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

    public List<Departamento> buscarPorDescripcion(String descripcion)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Departamento> departamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT d.id_depto, d.descripcion, s.no_sucursal, s.nombre AS sucursal " +
                    "FROM departamento d " +
                    "JOIN sucursal s ON d.no_sucursal = s.no_sucursal " +
                    "WHERE d.descripcion LIKE ? " +
                    "ORDER BY s.nombre, d.descripcion";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, "%" + descripcion + "%");

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

    private boolean existeDescripcionEnSucursal(Connection conn, String descripcion, Integer noSucursal, Integer idIgnorado)
            throws SQLException {

        String consulta;

        if (idIgnorado == null) {
            consulta = "SELECT COUNT(*) AS total " +
                    "FROM departamento " +
                    "WHERE LOWER(descripcion) = LOWER(?) AND no_sucursal = ?";
        } else {
            consulta = "SELECT COUNT(*) AS total " +
                    "FROM departamento " +
                    "WHERE LOWER(descripcion) = LOWER(?) " +
                    "AND no_sucursal = ? " +
                    "AND id_depto <> ?";
        }

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, descripcion);
        sentencia.setInt(2, noSucursal);

        if (idIgnorado != null) {
            sentencia.setInt(3, idIgnorado);
        }

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }

    private boolean tieneEmpleadosAsociados(Connection conn, Integer idDepto)
            throws SQLException {

        String consulta = "SELECT COUNT(*) AS total " +
                "FROM trabaja_en " +
                "WHERE id_depto = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, idDepto);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }
}