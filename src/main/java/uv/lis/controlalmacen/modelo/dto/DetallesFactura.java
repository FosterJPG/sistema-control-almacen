package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;

public class DetallesFactura {

    private String idItem;
    private String descripcion;
    private Integer cantidad;
    private Double costoUnitario;
    private String descripcionPartida;

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(Double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcionPartida() {
        return descripcionPartida;
    }

    public void setDescripcionPartida(String descripcionPartida) {
        this.descripcionPartida = descripcionPartida;
    }
}
