package uv.lis.controlalmacen.modelo.dto;

public class PartidaPresupuestal {
    private Integer codigo;
    private String descripcionPartida;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcionPartida() {
        return descripcionPartida;
    }

    public void setDescripcionPartida(String descripcionPartida) {
        this.descripcionPartida = descripcionPartida;
    }

    @Override
    public String toString() {
        return descripcionPartida;
    }
}
