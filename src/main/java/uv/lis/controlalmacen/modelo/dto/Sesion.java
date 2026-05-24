package uv.lis.controlalmacen.modelo.dto;

public class Sesion {

    private static Usuario usuarioActual;

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public  static void setUsuarioActual(Usuario usuarioActual) {
        Sesion.usuarioActual = usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
