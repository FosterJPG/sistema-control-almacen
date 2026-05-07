package uv.lis.controlalmacen.modelo.dto;

public class Sucursal {
    private Integer noSucursal;
    private String nombre;
    private String direccion;
    private String telefono;

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getNoSucursal() {
        return noSucursal;
    }

    public void setNoSucursal(Integer noSucursal) {
        this.noSucursal = noSucursal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
