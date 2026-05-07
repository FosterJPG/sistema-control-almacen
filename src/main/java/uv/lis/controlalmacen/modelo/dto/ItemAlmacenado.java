package uv.lis.controlalmacen.modelo.dto;

public class ItemAlmacenado {
    private String descripcionItem;
    private String idItem;
    private Integer existencias;
    private Integer stockMin;
    private Integer stockMax;
    private String descricionPartida;

    public String getDescricionPartida() {
        return descricionPartida;
    }

    public void setDescricionPartida(String descricionPartida) {
        this.descricionPartida = descricionPartida;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public Integer getStockMax() {
        return stockMax;
    }

    public void setStockMax(Integer stockMax) {
        this.stockMax = stockMax;
    }

    public Integer getStockMin() {
        return stockMin;
    }

    public void setStockMin(Integer stockMin) {
        this.stockMin = stockMin;
    }
}
