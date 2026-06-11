package uv.lis.controlalmacen.modelo.dto;

public class Puesto {
    private Integer idPuesto;
    private String puesto;

    public Integer getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(Integer idPuesto) {
        this.idPuesto = idPuesto;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    @Override
    public String toString() {
        return puesto != null ? puesto : "";
    }
}
