package uv.lis.controlalmacen.logica;

import uv.lis.controlalmacen.excepciones.UsuarioNoEncontradoException;
import uv.lis.controlalmacen.modelo.dao.AutenticacionDAO;
import uv.lis.controlalmacen.modelo.dto.Usuario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Autenticador {

    /**
     * Metodo que se llama para el inicio de sesión. Este primero hashea la contraseña ingresada en la interfaz y llama al metodo
     * para realizar la consulta e iniciar sesión
     * @param usuario
     * @param password
     * @return un objeto de tipo {@code Usuario}, el cual es el usuario recuperado para iniciar sesión.
     * Retorna {@code null} si no se lanzó alguna de las excepciones
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Usuario iniciarSesion(String usuario, String password) throws NoSuchAlgorithmException,
            SQLException, IOException, ClassNotFoundException, NullPointerException {

        try {
            byte[] passwordHashed = hashearPassword(password);
            Usuario usuarioLogin = AutenticacionDAO.autenticarUsuario(usuario, passwordHashed);
            return usuarioLogin;
        } catch (UsuarioNoEncontradoException ex) {
            throw ex;
        }
    }

    private static byte[] hashearPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}
