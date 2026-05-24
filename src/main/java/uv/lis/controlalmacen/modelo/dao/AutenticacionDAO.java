package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.excepciones.UsuarioNoEncontradoException;
import uv.lis.controlalmacen.modelo.dto.Rol;
import uv.lis.controlalmacen.modelo.dto.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase DAO para iniciar sesión en el sistema
 */
public class AutenticacionDAO {

    private static final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    /**
     * Consulta en la tabla de usuarios con el usuario_autenticacion para realizar el inicio de sesión
     * @param usuario
     * @param  password Contraseña hasheada y convertida en Bytes previamente
     * @return objeto de tipo {@code Usuario}
     * @throws SQLException
     */
    public static Usuario autenticarUsuario(String usuario, byte[] password) throws SQLException, IOException, ClassNotFoundException, NullPointerException {

        Usuario usuarioLogin = new Usuario();

        try (Connection conn = ConnectionFactory.crearParaAutenticacion()) {
            if (conn == null) {
                throw new SQLException("Error: No se pudo conectar a la base de datos");
            }

            String query = "SELECT id_usuario, contrasenia, no_empleado, id_rol FROM usuario " +
                    "WHERE contrasenia = ? AND id_usuario = ?;";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBytes(1, password);
            ps.setString(2, usuario);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new UsuarioNoEncontradoException("Usuario no encontrado. El usuario y/o contraseña no coinciden.");
            }

            usuarioLogin.setIdUsuario(rs.getString("id_usuario"));
            usuarioLogin.setPassword(rs.getBytes("contrasenia"));
            usuarioLogin.setEmpleado(empleadoDAO.buscarUno(rs.getInt("no_empleado")));
            int idRol = rs.getInt("id_rol");
            switch (idRol) {
                case 1 -> usuarioLogin.setRol(Rol.CENTRAL);
                case 2 -> usuarioLogin.setRol(Rol.ENCARGADO);
                case 3 -> usuarioLogin.setRol(Rol.SALIDAS);
                case 4 -> usuarioLogin.setRol(Rol.SOLICITUDES);
            }
        }
        return usuarioLogin;
    }
}
