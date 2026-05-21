package uv.lis.controlalmacen.logica;

import uv.lis.controlalmacen.modelo.dto.Rol;

public class CargadorEscenas {

    public static String cargarEscenarSegunRol(Rol rolUsuario) {
        String rutaMenu = null;

        if (rolUsuario == Rol.CENTRAL) {
            //rutaMenu = "/fxml/MenuPrincipalCentral.fxml";
            rutaMenu = "MenuPrincipalCentral";
        }
        if (rolUsuario == Rol.ENCARGADO) {
            //rutaMenu = "/fxml/MenuPrincipalEncargado.fxml";
            rutaMenu = "MenuPrincipalEncargado";
        }
        if (rolUsuario == Rol.SALIDAS) {
            //rutaMenu = "/fxml/MenuPrincipalSalidas.fxml";
            rutaMenu = "MenuPrincipalSalidas";
        }
        if (rolUsuario == Rol.SOLICITUDES) {
            //rutaMenu = "/fxml/MenuPrincipalSolicitudes.fxml";
            rutaMenu = "MenuPrincipalSolicitudes";
        }

        return rutaMenu;
    }
}
