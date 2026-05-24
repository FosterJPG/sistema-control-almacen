package uv.lis.controlalmacen.modelo.dto;

public class Item {

    private String idItem;
    private String descripcionItem;
    private String descripcionPartida;
    private int codigoPartidaPresupuestal;

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public String getDescripcionPartida() {
        return descripcionPartida;
    }

    public void setDescripcionPartida(String descripcionPartida) {
        this.descripcionPartida = descripcionPartida;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public int getCodigoPartidaPresupuestal() {
        return codigoPartidaPresupuestal;
    }

    public void setCodigoPartidaPresupuestal(int idPartidaPresupuestal) {
        this.codigoPartidaPresupuestal = idPartidaPresupuestal;
    }
    
}
