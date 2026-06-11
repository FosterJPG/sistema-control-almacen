package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;

public class DetallesSolicitud {

    private String idItem;
    private String descripcionItem;
    private Integer cantidad;
    private String uso;

    private Integer cantidadEntregar;
    private Integer existencias;
    private Integer codigoPartida;
    private String descripcionPartida;

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public Integer getExistencias() { return existencias; }
    public void setExistencias(Integer existencias) { this.existencias = existencias; }

    public Integer getCodigoPartida() { return codigoPartida; }
    public void setCodigoPartida(Integer codigoPartida) { this.codigoPartida = codigoPartida; }

    public String getDescripcionPartida() { return descripcionPartida; }
    public void setDescripcionPartida(String descripcionPartida) { this.descripcionPartida = descripcionPartida; }

    public Integer getCantidadEntregar() {
        return cantidadEntregar;
    }

    public void setCantidadEntregar(Integer cantidadEntregar) {
        this.cantidadEntregar = cantidadEntregar;
    }
}
