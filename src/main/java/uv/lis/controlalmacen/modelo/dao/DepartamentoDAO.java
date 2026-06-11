package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Sucursal;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class DepartamentoDAO {

    private static final String SELECT_BASE =
            "SELECT d.id_depto, d.descripcion, d.no_sucursal, s.nombre AS nombre_sucursal " +
            "FROM departamento d JOIN sucursal s ON d.no_sucursal = s.no_sucursal ";

    public List<Departamento> buscarTodos() throws SQLException, IOException, ClassNotFoundException {
        return ejecutarConsulta(SELECT_BASE + "ORDER BY s.nombre, d.descripcion", null);
    }

    public List<Departamento> buscarPorSucursal(Integer noSucursal) throws SQLException, IOException, ClassNotFoundException {
        if (noSucursal == null) return buscarTodos();
        return ejecutarConsulta(SELECT_BASE + "WHERE d.no_sucursal = ? ORDER BY d.descripcion",
                ps -> ps.setInt(1, noSucursal));
    }

    public List<Departamento> buscarPorDescripcion(String descripcion) throws SQLException, IOException, ClassNotFoundException {
        return ejecutarConsulta(SELECT_BASE + "WHERE d.descripcion LIKE ? ORDER BY d.descripcion",
                ps -> ps.setString(1, "%" + descripcion + "%"));
    }

    public boolean registrar(String descripcion, int noSucursal) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO departamento(descripcion, no_sucursal) VALUES(?, ?)")) {
                ps.setString(1, descripcion);
                ps.setInt(2, noSucursal);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean actualizar(int idDepto, String descripcion) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE departamento SET descripcion = ? WHERE id_depto = ?")) {
                ps.setString(1, descripcion);
                ps.setInt(2, idDepto);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminar(int idDepto) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM departamento WHERE id_depto = ?")) {
                ps.setInt(1, idDepto);
                return ps.executeUpdate() > 0;
            }
        }
    }

    private List<Departamento> ejecutarConsulta(String sql, SqlConsumer<PreparedStatement> setter)
            throws SQLException, IOException, ClassNotFoundException {
        List<Departamento> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (setter != null) setter.accept(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private Departamento mapear(ResultSet rs) throws SQLException {
        Sucursal sucursal = new Sucursal();
        sucursal.setNoSucursal(rs.getInt("no_sucursal"));
        sucursal.setNombre(rs.getString("nombre_sucursal"));

        Departamento d = new Departamento();
        d.setIdDepto(rs.getInt("id_depto"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setSucursal(sucursal);
        return d;
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
