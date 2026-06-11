package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.RolUsuario;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class UsuarioDAO implements OperacionesCatalogoDAO<Usuario, String> {

    @Override
    public boolean registrar(Usuario usuario)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (existeUsuario(conn, usuario.getIdUsuario())) {
                throw new SQLException("Ya existe un usuario con ese identificador.");
            }

            String consulta = "INSERT INTO usuario(id_usuario, contrasenia, fecha_registro, no_empleado, id_rol) " +
                    "VALUES (?, ?, CURDATE(), ?, ?)";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, usuario.getIdUsuario());
            sentencia.setBytes(2, usuario.getPassword());
            sentencia.setInt(3, usuario.getEmpleado().getNoEmpleado());
            sentencia.setInt(4, usuario.getIdRol());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(Usuario usuario)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "DELETE FROM usuario WHERE id_usuario = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, usuario.getIdUsuario());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Usuario usuario)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (usuario.getPassword() == null) {
                String consulta = "UPDATE usuario " +
                        "SET no_empleado = ?, id_rol = ? " +
                        "WHERE id_usuario = ?";

                PreparedStatement sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, usuario.getEmpleado().getNoEmpleado());
                sentencia.setInt(2, usuario.getIdRol());
                sentencia.setString(3, usuario.getIdUsuario());

                return sentencia.executeUpdate() > 0;
            }

            String consulta = "UPDATE usuario " +
                    "SET contrasenia = ?, no_empleado = ?, id_rol = ? " +
                    "WHERE id_usuario = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setBytes(1, usuario.getPassword());
            sentencia.setInt(2, usuario.getEmpleado().getNoEmpleado());
            sentencia.setInt(3, usuario.getIdRol());
            sentencia.setString(4, usuario.getIdUsuario());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public List<Usuario> buscarTodos()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT u.id_usuario, u.contrasenia, u.fecha_registro, u.no_empleado, u.id_rol, " +
                    "r.descripcion AS rol, " +
                    "e.nombre, e.paterno, e.materno, e.direccion, e.correo_electronico, e.telefono " +
                    "FROM usuario u " +
                    "JOIN empleado e ON u.no_empleado = e.no_empleado " +
                    "JOIN rol r ON u.id_rol = r.id_rol " +
                    "ORDER BY e.nombre, e.paterno, e.materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Empleado empleado = new Empleado();
                empleado.setNoEmpleado(resultado.getInt("no_empleado"));
                empleado.setNombre(resultado.getString("nombre"));
                empleado.setPaterno(resultado.getString("paterno"));
                empleado.setMaterno(resultado.getString("materno"));
                empleado.setDireccion(resultado.getString("direccion"));
                empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
                empleado.setTelefono(resultado.getString("telefono"));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultado.getString("id_usuario"));
                usuario.setPassword(resultado.getBytes("contrasenia"));
                usuario.setFechaRegistro(resultado.getDate("fecha_registro"));
                usuario.setEmpleado(empleado);
                usuario.setIdRol(resultado.getInt("id_rol"));
                usuario.setDescripcionRol(resultado.getString("rol"));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    @Override
    public Usuario buscarUno(String idUsuario)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Usuario usuario = new Usuario();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT u.id_usuario, u.contrasenia, u.fecha_registro, u.no_empleado, u.id_rol, " +
                    "r.descripcion AS rol, " +
                    "e.nombre, e.paterno, e.materno, e.direccion, e.correo_electronico, e.telefono " +
                    "FROM usuario u " +
                    "JOIN empleado e ON u.no_empleado = e.no_empleado " +
                    "JOIN rol r ON u.id_rol = r.id_rol " +
                    "WHERE u.id_usuario = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, idUsuario);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                Empleado empleado = new Empleado();
                empleado.setNoEmpleado(resultado.getInt("no_empleado"));
                empleado.setNombre(resultado.getString("nombre"));
                empleado.setPaterno(resultado.getString("paterno"));
                empleado.setMaterno(resultado.getString("materno"));
                empleado.setDireccion(resultado.getString("direccion"));
                empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
                empleado.setTelefono(resultado.getString("telefono"));

                usuario.setIdUsuario(resultado.getString("id_usuario"));
                usuario.setPassword(resultado.getBytes("contrasenia"));
                usuario.setFechaRegistro(resultado.getDate("fecha_registro"));
                usuario.setEmpleado(empleado);
                usuario.setIdRol(resultado.getInt("id_rol"));
                usuario.setDescripcionRol(resultado.getString("rol"));
            }
        }

        return usuario;
    }

    public List<Usuario> buscarPorNombreEmpleado(String nombreEmpleado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT u.id_usuario, u.contrasenia, u.fecha_registro, u.no_empleado, u.id_rol, " +
                    "r.descripcion AS rol, " +
                    "e.nombre, e.paterno, e.materno, e.direccion, e.correo_electronico, e.telefono " +
                    "FROM usuario u " +
                    "JOIN empleado e ON u.no_empleado = e.no_empleado " +
                    "JOIN rol r ON u.id_rol = r.id_rol " +
                    "WHERE e.nombre LIKE ? OR e.paterno LIKE ? OR e.materno LIKE ? " +
                    "ORDER BY e.nombre, e.paterno, e.materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, "%" + nombreEmpleado + "%");
            sentencia.setString(2, "%" + nombreEmpleado + "%");
            sentencia.setString(3, "%" + nombreEmpleado + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Empleado empleado = new Empleado();
                empleado.setNoEmpleado(resultado.getInt("no_empleado"));
                empleado.setNombre(resultado.getString("nombre"));
                empleado.setPaterno(resultado.getString("paterno"));
                empleado.setMaterno(resultado.getString("materno"));
                empleado.setDireccion(resultado.getString("direccion"));
                empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
                empleado.setTelefono(resultado.getString("telefono"));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultado.getString("id_usuario"));
                usuario.setPassword(resultado.getBytes("contrasenia"));
                usuario.setFechaRegistro(resultado.getDate("fecha_registro"));
                usuario.setEmpleado(empleado);
                usuario.setIdRol(resultado.getInt("id_rol"));
                usuario.setDescripcionRol(resultado.getString("rol"));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    public List<Usuario> buscarPorRol(String descripcionRol)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT u.id_usuario, u.contrasenia, u.fecha_registro, u.no_empleado, u.id_rol, " +
                    "r.descripcion AS rol, " +
                    "e.nombre, e.paterno, e.materno, e.direccion, e.correo_electronico, e.telefono " +
                    "FROM usuario u " +
                    "JOIN empleado e ON u.no_empleado = e.no_empleado " +
                    "JOIN rol r ON u.id_rol = r.id_rol " +
                    "WHERE r.descripcion = ? " +
                    "ORDER BY e.nombre, e.paterno, e.materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, descripcionRol);

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Empleado empleado = new Empleado();
                empleado.setNoEmpleado(resultado.getInt("no_empleado"));
                empleado.setNombre(resultado.getString("nombre"));
                empleado.setPaterno(resultado.getString("paterno"));
                empleado.setMaterno(resultado.getString("materno"));
                empleado.setDireccion(resultado.getString("direccion"));
                empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
                empleado.setTelefono(resultado.getString("telefono"));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultado.getString("id_usuario"));
                usuario.setPassword(resultado.getBytes("contrasenia"));
                usuario.setFechaRegistro(resultado.getDate("fecha_registro"));
                usuario.setEmpleado(empleado);
                usuario.setIdRol(resultado.getInt("id_rol"));
                usuario.setDescripcionRol(resultado.getString("rol"));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    public List<Usuario> buscarPorNombreEmpleadoYRol(String nombreEmpleado, String descripcionRol)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT u.id_usuario, u.contrasenia, u.fecha_registro, u.no_empleado, u.id_rol, " +
                    "r.descripcion AS rol, " +
                    "e.nombre, e.paterno, e.materno, e.direccion, e.correo_electronico, e.telefono " +
                    "FROM usuario u " +
                    "JOIN empleado e ON u.no_empleado = e.no_empleado " +
                    "JOIN rol r ON u.id_rol = r.id_rol " +
                    "WHERE r.descripcion = ? " +
                    "AND (e.nombre LIKE ? OR e.paterno LIKE ? OR e.materno LIKE ?) " +
                    "ORDER BY e.nombre, e.paterno, e.materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, descripcionRol);
            sentencia.setString(2, "%" + nombreEmpleado + "%");
            sentencia.setString(3, "%" + nombreEmpleado + "%");
            sentencia.setString(4, "%" + nombreEmpleado + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Empleado empleado = new Empleado();
                empleado.setNoEmpleado(resultado.getInt("no_empleado"));
                empleado.setNombre(resultado.getString("nombre"));
                empleado.setPaterno(resultado.getString("paterno"));
                empleado.setMaterno(resultado.getString("materno"));
                empleado.setDireccion(resultado.getString("direccion"));
                empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
                empleado.setTelefono(resultado.getString("telefono"));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultado.getString("id_usuario"));
                usuario.setPassword(resultado.getBytes("contrasenia"));
                usuario.setFechaRegistro(resultado.getDate("fecha_registro"));
                usuario.setEmpleado(empleado);
                usuario.setIdRol(resultado.getInt("id_rol"));
                usuario.setDescripcionRol(resultado.getString("rol"));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    public List<RolUsuario> buscarRoles()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<RolUsuario> roles = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT MIN(id_rol) AS id_rol, descripcion " +
                    "FROM rol " +
                    "GROUP BY descripcion " +
                    "ORDER BY descripcion";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                RolUsuario rolUsuario = new RolUsuario();
                rolUsuario.setIdRol(resultado.getInt("id_rol"));
                rolUsuario.setDescripcion(resultado.getString("descripcion"));

                roles.add(rolUsuario);
            }
        }

        return roles;
    }

    public List<Empleado> buscarEmpleados()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, nombre, paterno, materno, direccion, correo_electronico, telefono " +
                    "FROM empleado " +
                    "ORDER BY nombre, paterno, materno";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Empleado empleado = new Empleado();
                empleado.setNoEmpleado(resultado.getInt("no_empleado"));
                empleado.setNombre(resultado.getString("nombre"));
                empleado.setPaterno(resultado.getString("paterno"));
                empleado.setMaterno(resultado.getString("materno"));
                empleado.setDireccion(resultado.getString("direccion"));
                empleado.setCorreoElectronico(resultado.getString("correo_electronico"));
                empleado.setTelefono(resultado.getString("telefono"));

                empleados.add(empleado);
            }
        }

        return empleados;
    }

    public boolean empleadoTieneUsuario(Integer noEmpleado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT COUNT(*) AS total FROM usuario WHERE no_empleado = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, noEmpleado);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("total") > 0;
            }
        }

        return false;
    }

    private boolean existeUsuario(Connection conn, String idUsuario)
            throws SQLException {

        String consulta = "SELECT COUNT(*) AS total FROM usuario WHERE id_usuario = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, idUsuario);

        ResultSet resultado = sentencia.executeQuery();

        if (resultado.next()) {
            return resultado.getInt("total") > 0;
        }

        return false;
    }
}