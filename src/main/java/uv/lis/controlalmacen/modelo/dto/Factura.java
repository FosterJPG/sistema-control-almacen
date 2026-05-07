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
}