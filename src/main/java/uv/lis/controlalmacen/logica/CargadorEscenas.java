package uv.lis.controlalmacen.logica;

import uv.lis.controlalmacen.modelo.dto.Rol;

public class CargadorEscenas {

    public static String cargarEscenarSegunRol(Rol rolUsuario) {
        String rutaMenu = null;

        if (rolUsuario == Rol.CENTRAL) {
            rutaMenu = "/fxml/MenuPrincipalCentral.fxml";
        }
        if (rolUsuario == Rol.ENCARGADO) {
            rutaMenu = "/fxml/MenuPrincipalEncargado.fxml";
        }
        if (rolUsuario == Rol.SALIDAS) {
            rutaMenu = "/fxml/MenuPrincipalSalidas.fxml";
        }
        if (rolUsuario == Rol.SOLICITUDES) {
            rutaMenu = "/fxml/MenuPrincipalSolicitudes.fxml";
        }

        System.out.println(rutaMenu);
        return rutaMenu;
    }
}
