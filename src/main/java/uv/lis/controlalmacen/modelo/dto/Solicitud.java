package uv.lis.controlalmacen.modelo.dto;

import java.util.Date;
import java.util.List;

public class Solicitud {
    private Integer noSolicitud;
    private Date fechaSolicitud;
    private Integer noEmpleado;
    private Integer noSucursal;

    private String nombreEmpleado;
    private String paternoEmpleado;
    private String maternoEmpleado;
    private String descripcionDepto;
    private List<DetallesSolicitud> detallesSolicitud;


}
