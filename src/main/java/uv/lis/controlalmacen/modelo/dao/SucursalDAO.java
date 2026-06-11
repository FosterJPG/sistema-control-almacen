package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class SucursalDAO implements OperacionesCatalogoDAO<Sucursal, Integer> {

    @Override
    public boolean registrar(Sucursal sucursal) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sucursal(nombre, direccion, telefono) VALUES(?, ?, ?)")) {
                ps.setString(1, sucursal.getNombre());
                ps.setString(2, sucursal.getDireccion());
                ps.setString(3, sucursal.getTelefono());
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean eliminar(Sucursal sucursal) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM sucursal WHERE no_sucursal = ?")) {
                ps.setInt(1, sucursal.getNoSucursal());
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean actualizar(Sucursal sucursal) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE sucursal SET nombre = ?, direccion = ?, telefono = ? WHERE no_sucursal = ?")) {
                ps.setString(1, sucursal.getNombre());
                ps.setString(2, sucursal.getDireccion());
                ps.setString(3, sucursal.getTelefono());
                ps.setInt(4, sucursal.getNoSucursal());
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public List<Sucursal> buscarTodos() throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        List<Sucursal> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT no_sucursal, nombre, direccion, telefono FROM sucursal ORDER BY nombre")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Sucursal buscarUno(Integer id) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        Sucursal sucursal = new Sucursal();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT no_sucursal, nombre, direccion, telefono FROM sucursal WHERE no_sucursal = ?")) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) sucursal = mapear(rs);
            }
        }
        return sucursal;
    }

    public List<Sucursal> buscarPorNombre(String nombre) throws SQLException, IOException, ClassNotFoundException {
        List<Sucursal> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT no_sucursal, nombre, direccion, telefono FROM sucursal WHERE nombre LIKE ? ORDER BY nombre")) {
                ps.setString(1, "%" + nombre + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Sucursal mapear(ResultSet rs) throws SQLException {
        Sucursal s = new Sucursal();
        s.setNoSucursal(rs.getInt("no_sucursal"));
        s.setNombre(rs.getString("nombre"));
        s.setDireccion(rs.getString("direccion"));
        s.setTelefono(rs.getString("telefono"));
        return s;
    }
}
