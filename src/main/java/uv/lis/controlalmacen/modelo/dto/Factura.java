package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;
import java.util.List;

public class Factura {
    private String folio;
    private Date fecha;
    private String rfc;
    private Integer noSucursal;

    private String razonSocial;
    private String telefono;
    private List<DetallesFactura> detallesFactura;

    public List<DetallesFactura> getDetallesFactura() {
        return detallesFactura;
    }

    public void setDetallesFactura(List<DetallesFactura> detallesFactura) {
        this.detallesFactura = detallesFactura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getNoSucursal() {
        return noSucursal;
    }

    public void setNoSucursal(Integer noSucursal) {
        this.noSucursal = noSucursal;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}