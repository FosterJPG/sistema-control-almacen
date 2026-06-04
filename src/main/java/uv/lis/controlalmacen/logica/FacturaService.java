package uv.lis.controlalmacen.logica;

import uv.lis.controlalmacen.modelo.dao.FacturaDAO;
import uv.lis.controlalmacen.modelo.dto.DetallesFactura;
import uv.lis.controlalmacen.modelo.dto.Factura;

import java.util.List;

public class FacturaService {
    FacturaDAO facturaDAO = new FacturaDAO();

    public static boolean guardarFactura(Factura factura){
        //return facturaDAO.registrar(factura);
        return true;
    }

    public static boolean guardarDetalles(List<DetallesFactura> detallesFactura){
        //return facturaDAO.registrar(factura);
        return true;
    }
}
