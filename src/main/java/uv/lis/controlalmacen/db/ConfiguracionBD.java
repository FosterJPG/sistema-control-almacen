package uv.lis.controlalmacen.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para cargar las propiedades de la base de datos. Estas se cargan desde el bloque static
 * lo que significa que se van a cargar con la primera llamada a alguno de sus métodos.
 */
public class ConfiguracionBD {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfiguracionBD.class.getResourceAsStream("/config/database.properties")){

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfiguracionBD() {
    }

    /**
     * Obtener la propiedad de 'driver' que se cargó en el static de la clase.
     * Esta propiedad se utiliza para posteriormente cargar el driver de JDBC y realizar la conexión
     *
     * @return propiedad 'driver' del properties de la BD
     */
    public static String getDriver() {
        return properties.getProperty("db.driver");
    }

    /**
     * Obtener la propiedad de 'host' que se cargó en el static de la clase
     * @return propiedad 'host' del properties de la BD
     */
    public static String getHost() {
        return properties.getProperty("db.host");
    }

    /**
     * Obtener la propiedad de 'port' que se cargó en el static de la clase
     * @return propiedad 'port' del properties de la BD
     */
    public static String getPort() {
        return properties.getProperty("db.port");
    }

    /**
     * Obtener la propiedad 'name' que se cargó en el static de la clase
     * @return propiedad 'name' del properties de la BD
     */
    public static String getDatabase() {
        return properties.getProperty("db.name");
    }

    /**
     * Metodo para construir la URL a enviar al driver
     * @return la URL, que se forma concatenando el HOST, PORT y NAME del propiertes
     */
    public static String getUrl() {
        return "jdbc:mysql://"
                + getHost()
                + ":"
                + getPort()
                + "/"
                + getDatabase()
                + "?useTimezone=true&serverTimezone=UTC";
    }

}
