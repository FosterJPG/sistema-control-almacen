package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO implements OperacionesCatalogoDAO<Factura, String>{
    @Override
    public boolean registrar(Factura factura) throws SQLException, NullPointerException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean eliminar(Factura factura) throws SQLException, NullPointerException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean actualizar(Factura factura) throws SQLException, NullPointerException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Factura> buscarTodos() throws SQLException, NullPointerException, ClassNotFoundException, IOException {
        List<Factura> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            String query = "SELECT folio, fecha_factura, rfc, razon_social, telefono, no_sucursal FROM vista_facturas_lista " +
                    "WHERE no_sucursal = ? ";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Factura factura = new Factura();
                factura.setFolio(rs.getString("folio"));
                factura.setFecha(rs.getDate("fecha_factura"));
                factura.setRfc(rs.getString("rfc"));
                factura.setRazonSocial(rs.getString("razon_social"));
                factura.setTelefono(rs.getString("telefono"));
                factura.setNoSucursal(rs.getInt("no_sucursal"));
                lista.add(factura);
            }
            // TODO exception en caso de lista vacia
        }

        return lista;
    }

    @Override
    public Factura buscarUno(String folio) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        return null;
    }
}
