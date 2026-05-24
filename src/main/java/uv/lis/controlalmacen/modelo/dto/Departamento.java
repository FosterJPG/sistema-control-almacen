package uv.lis.controlalmacen.modelo.dto;

public class Departamento {
    private Integer idDepto;
    private String descripcion;
    private Integer noSucursal;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(Integer idDepto) {
        this.idDepto = idDepto;
    }

    public Integer getNoSucursal() {
        return noSucursal;
    }

    public void setNoSucursal(Integer noSucursal) {
        this.noSucursal = noSucursal;
    }
}
