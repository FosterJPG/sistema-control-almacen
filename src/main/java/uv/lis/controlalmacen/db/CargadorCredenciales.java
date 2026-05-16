package uv.lis.controlalmacen.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para cargar las credenciales desde cualquier lugar que se quiera hacer una conexion
 */
public class CargadorCredenciales {

    /**
     * Metodo genérico que recibe una ruta de propiedades de usuario y retorna un objeto Properties
     * @param ruta
     * @return properties de la ruta enviada
     */
    public static Properties cargarCredenciales(String ruta) throws IOException {
        Properties prop = new Properties();
        try (InputStream input = CargadorCredenciales.class.getResourceAsStream(ruta)){
            prop.load(input);
        }
        return prop;
    }
}
