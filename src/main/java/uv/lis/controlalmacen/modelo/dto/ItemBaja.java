package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;

public class ItemBaja {
    private String idItem;
    private Date fechaBaja;
    private String razon;
    private Integer existenciasRestantes;
    private Integer descripcionItem;

    public Integer getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(Integer descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public Integer getExistenciasRestantes() {
        return existenciasRestantes;
    }

    public void setExistenciasRestantes(Integer existenciasRestantes) {
        this.existenciasRestantes = existenciasRestantes;
    }

    public Date getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }
}
