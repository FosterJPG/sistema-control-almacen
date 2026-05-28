package uv.lis.controlalmacen.logica;

import uv.lis.controlalmacen.modelo.dto.Rol;

public class CargadorEscenas {

    public static String cargarEscenarSegunRol(Rol rolUsuario) {
        String rutaMenu = null;

        if (rolUsuario == Rol.CENTRAL) {
            rutaMenu = "MenuPrincipalCentral";
        }
        if (rolUsuario == Rol.ENCARGADO) {
            rutaMenu = "MenuPrincipalEncargado";
        }
        if (rolUsuario == Rol.SALIDAS) {
            rutaMenu = "MenuPrincipalSalidas";
        }
        if (rolUsuario == Rol.SOLICITUDES) {
            rutaMenu = "MenuPrincipalDepartamento";
        }

        return rutaMenu;
    }
}
