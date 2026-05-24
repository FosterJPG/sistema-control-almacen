package uv.lis.controlalmacen.controladores;

import uv.lis.controlalmacen.modelo.dto.Empleado;

public abstract class MenuController {
    public Empleado empleado;

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    abstract void cargarDatos();
}
