package uv.lis.controlalmacen.modelo.dto;

public class ItemAlmacenado {

    private String idItem;
    private String descripcionItem;
    private Integer codigoPartidaPresupuestal;
    private String descripcionPartida;
    private Integer existencias;
    private Integer stockMin;
    private Integer stockMax;

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public Integer getCodigoPartidaPresupuestal() {
        return codigoPartidaPresupuestal;
    }

    public void setCodigoPartidaPresupuestal(Integer codigoPartidaPresupuestal) {
        this.codigoPartidaPresupuestal = codigoPartidaPresupuestal;
    }

    public String getDescripcionPartida() {
        return descripcionPartida;
    }

    public void setDescripcionPartida(String descripcionPartida) {
        this.descripcionPartida = descripcionPartida;
    }

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public Integer getStockMin() {
        return stockMin;
    }

    public void setStockMin(Integer stockMin) {
        this.stockMin = stockMin;
    }

    public Integer getStockMax() {
        return stockMax;
    }

    public void setStockMax(Integer stockMax) {
        this.stockMax = stockMax;
    }
}