package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;
import java.util.List;

public class Solicitud {
    private Integer noSolicitud;
    private Date fechaSolicitud;
    private Integer noEmpleado;
    private Integer noSucursal;

    private String nombreEmpleado;
    private String paternoEmpleado;
    private String maternoEmpleado;
    private String descripcionDepto;
    private List<DetallesSolicitud> detallesSolicitud;

    public Integer getNoSolicitud() { return noSolicitud; }
    public void setNoSolicitud(Integer noSolicitud) { this.noSolicitud = noSolicitud; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public Integer getNoEmpleado() { return noEmpleado; }
    public void setNoEmpleado(Integer noEmpleado) { this.noEmpleado = noEmpleado; }

    public Integer getNoSucursal() { return noSucursal; }
    public void setNoSucursal(Integer noSucursal) { this.noSucursal = noSucursal; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getPaternoEmpleado() { return paternoEmpleado; }
    public void setPaternoEmpleado(String paternoEmpleado) { this.paternoEmpleado = paternoEmpleado; }

    public String getMaternoEmpleado() { return maternoEmpleado; }
    public void setMaternoEmpleado(String maternoEmpleado) { this.maternoEmpleado = maternoEmpleado; }

    public String getDescripcionDepto() { return descripcionDepto; }
    public void setDescripcionDepto(String descripcionDepto) { this.descripcionDepto = descripcionDepto; }

    public List<DetallesSolicitud> getDetallesSolicitud() { return detallesSolicitud; }
    public void setDetallesSolicitud(List<DetallesSolicitud> detallesSolicitud) { this.detallesSolicitud = detallesSolicitud; }

    public String getNombreCompleto() {
        String n = nombreEmpleado != null ? nombreEmpleado : "";
        String p = paternoEmpleado != null ? paternoEmpleado : "";
        return (n + " " + p).trim();
    }
}
