package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;

public class Solicitud {
    private Integer noSolicitud;
    private Date fechaSolicitud;
    private Integer noEmpleado;
    private Integer noSucursal;

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Integer getNoEmpleado() {
        return noEmpleado;
    }

    public void setNoEmpleado(Integer noEmpleado) {
        this.noEmpleado = noEmpleado;
    }

    public Integer getNoSolicitud() {
        return noSolicitud;
    }

    public void setNoSolicitud(Integer noSolicitud) {
        this.noSolicitud = noSolicitud;
    }

    public Integer getNoSucursal() {
        return noSucursal;
    }

    public void setNoSucursal(Integer noSucursal) {
        this.noSucursal = noSucursal;
    }
}
