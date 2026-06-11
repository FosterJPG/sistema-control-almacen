package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Rol;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Usuario;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class UsuarioDAO {

    public List<Usuario> buscarTodos() throws SQLException, IOException, ClassNotFoundException {
        return buscarConFiltro(null);
    }

    public List<Usuario> buscarPorRol(int idRol) throws SQLException, IOException, ClassNotFoundException {
        return buscarConFiltro(idRol);
    }

    private List<Usuario> buscarConFiltro(Integer idRol) throws SQLException, IOException, ClassNotFoundException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            String sql = "SELECT u.id_usuario, e.no_empleado, e.nombre, e.paterno, e.materno, " +
                         "e.correo_electronico, u.id_rol, u.fecha_registro " +
                         "FROM usuario u " +
                         "JOIN empleado e ON e.no_empleado = u.no_empleado " +
                         (idRol != null ? "WHERE u.id_rol = ? " : "") +
                         "ORDER BY e.paterno, e.nombre";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (idRol != null) ps.setInt(1, idRol);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public boolean eliminar(String idUsuario) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM usuario WHERE id_usuario = ?")) {
                ps.setString(1, idUsuario);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean registrar(String idUsuario, byte[] passwordHash, int noEmpleado, int idRol)
            throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuario(id_usuario, contrasenia, fecha_registro, no_empleado, id_rol) " +
                    "VALUES(?, ?, CURDATE(), ?, ?)")) {
                ps.setString(1, idUsuario);
                ps.setBytes(2, passwordHash);
                ps.setInt(3, noEmpleado);
                ps.setInt(4, idRol);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean actualizar(String idUsuario, byte[] passwordHash, int idRol)
            throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            if (passwordHash != null) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE usuario SET id_rol = ?, contrasenia = ? WHERE id_usuario = ?")) {
                    ps.setInt(1, idRol);
                    ps.setBytes(2, passwordHash);
                    ps.setString(3, idUsuario);
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE usuario SET id_rol = ? WHERE id_usuario = ?")) {
                    ps.setInt(1, idRol);
                    ps.setString(2, idUsuario);
                    return ps.executeUpdate() > 0;
                }
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setNoEmpleado(rs.getInt("no_empleado"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setPaterno(rs.getString("paterno"));
        empleado.setMaterno(rs.getString("materno"));
        empleado.setCorreoElectronico(rs.getString("correo_electronico"));

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getString("id_usuario"));
        u.setFechaRegistro(rs.getDate("fecha_registro"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setRol(mapearRol(rs.getInt("id_rol")));
        u.setEmpleado(empleado);
        return u;
    }

    private Rol mapearRol(int idRol) {
        return switch (idRol) {
            case 1 -> Rol.CENTRAL;
            case 2 -> Rol.ENCARGADO;
            case 3 -> Rol.SALIDAS;
            case 4 -> Rol.SOLICITUDES;
            default -> null;
        };
    }
}
