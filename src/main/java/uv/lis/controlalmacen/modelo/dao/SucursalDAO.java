package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
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

public class SucursalDAO implements OperacionesCatalogoDAO<Sucursal, Integer> {

    @Override
    public boolean registrar(Sucursal sucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (existeNombre(conn, sucursal.getNombre(), null)) {
                throw new SQLException("Ya existe una sucursal con ese nombre.");
            }

            String consulta = "INSERT INTO sucursal(nombre, direccion, telefono) VALUES (?, ?, ?)";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, sucursal.getNombre());
            sentencia.setString(2, sucursal.getDireccion());
            sentencia.setString(3, sucursal.getTelefono());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(Sucursal sucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (tieneDepartamentosAsociados(conn, sucursal.getNoSucursal())) {
                throw new SQLException("No se puede eliminar la sucursal porque tiene departamentos asociados.");
            }

            String consulta = "DELETE FROM sucursal WHERE no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, sucursal.getNoSucursal());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Sucursal sucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (existeNombre(conn, sucursal.getNombre(), sucursal.getNoSucursal())) {
                throw new SQLException("Ya existe otra sucursal con ese nombre.");
            }

            String consulta = "UPDATE sucursal " +
                    "SET nombre = ?, direccion = ?, telefono = ? " +
                    "WHERE no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, sucursal.getNombre());
            sentencia.setString(2, sucursal.getDireccion());
            sentencia.setString(3, sucursal.getTelefono());
            sentencia.setInt(4, sucursal.getNoSucursal());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public List<Sucursal> buscarTodos()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Sucursal> sucursales = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_sucursal, nombre, direccion, telefono " +
                    "FROM sucursal " +
                    "ORDER BY no_sucursal";

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

    @Override
    public Sucursal buscarUno(Integer noSucursal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Sucursal sucursal = new Sucursal();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_sucursal, nombre, direccion, telefono " +
                    "FROM sucursal " +
                    "WHERE no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, noSucursal);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                sucursal.setNoSucursal(resultado.getInt("no_sucursal"));
                sucursal.setNombre(resultado.getString("nombre"));
                sucursal.setDireccion(resultado.getString("direccion"));
                sucursal.setTelefono(resultado.getString("telefono"));
            }
        }

        return sucursal;
    }

    public List<Sucursal> buscarPorNombre(String nombre)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Sucursal> sucursales = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_sucursal, nombre, direccion, telefono " +
                    "FROM sucursal " +
                    "WHERE nombre LIKE ? " +
                    "ORDER BY no_sucursal";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, "%" + nombre + "%");

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

    private boolean tieneDepartamentosAsociados(Connection conn, Integer noSucursal)
            throws SQLException {

        String consulta = "SELECT COUNT(*) AS total " +
                "FROM departamento " +
                "WHERE no_sucursal = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, noSucursal);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }

    private boolean existeNombre(Connection conn, String nombre, Integer noSucursalIgnorada)
            throws SQLException {

        String consulta;

        if (noSucursalIgnorada == null) {
            consulta = "SELECT COUNT(*) AS total " +
                    "FROM sucursal " +
                    "WHERE LOWER(nombre) = LOWER(?)";
        } else {
            consulta = "SELECT COUNT(*) AS total " +
                    "FROM sucursal " +
                    "WHERE LOWER(nombre) = LOWER(?) " +
                    "AND no_sucursal <> ?";
        }

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, nombre);

        if (noSucursalIgnorada != null) {
            sentencia.setInt(2, noSucursalIgnorada);
        }

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }
}