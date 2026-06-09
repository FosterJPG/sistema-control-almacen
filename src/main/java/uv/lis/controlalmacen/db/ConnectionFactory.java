package uv.lis.controlalmacen.db;

import uv.lis.controlalmacen.modelo.dto.Rol;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase Factory para crear distintos tipos de conexiones de acuerdo al rol del usuario que realice la conexión
 */
public class ConnectionFactory {

    /**
     * Metodo del ConnectionFactory que crea una conexion sin recibir parametros, lo que indica que es la conexion que se
     * va a crear para la autenticacion, mandando directamente la ruta de user_auth.properties.
     * @return Conexion creada con el USER y PASSWORD de usuario_autenticacion
     * @throws SQLException
     */
    public static Connection crearParaAutenticacion() throws SQLException, IOException, ClassNotFoundException {
        Properties prop = CargadorCredenciales.cargarCredenciales("/config/user_auth.properties");

        Class.forName(ConfiguracionBD.getDriver());

        String user = prop.getProperty("db.user");
        String  password = prop.getProperty("db.password");
        String url = ConfiguracionBD.getUrl();

        return DriverManager.getConnection(url, user, password);
    }

    public static Connection crearParaRol(Rol rol) throws IOException, SQLException, ClassNotFoundException{
        String rutaProperties = determinarRutaProperties(rol);

        Properties prop = CargadorCredenciales.cargarCredenciales(rutaProperties);

        Class.forName(ConfiguracionBD.getDriver());

        String user = prop.getProperty("db.user");
        String password = prop.getProperty("db.password");
        String url = ConfiguracionBD.getUrl();

        return DriverManager.getConnection(url, user, password);
    }

    private static String determinarRutaProperties(Rol rolUsuario) {
        String rutaProperties = null;

        if (rolUsuario == Rol.CENTRAL) {
            rutaProperties = "/config/user_central.properties";
        }
        if (rolUsuario == Rol.ENCARGADO) {
            rutaProperties = "/config/user_sucursal.properties";
        }
        if (rolUsuario == Rol.SALIDAS) {
            rutaProperties = "/config/user_salidas.properties";
        }
        if (rolUsuario == Rol.SOLICITUDES) {
            rutaProperties = "/config/user_solicitudes.properties";
        }

        return rutaProperties;
    }
}
