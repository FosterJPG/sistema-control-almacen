package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Factura;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public List<Factura> buscarPorFecha(LocalDate fechaInicial, LocalDate fechaFinal)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException {

        List<Factura> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException("Error: No se pudo conectar a la base de datos");
            }

            String consulta = "SELECT folio, fecha_factura, rfc, razon_social, telefono, no_sucursal FROM vista_facturas_lista " +
                    "WHERE fecha_factura BETWEEN ? AND ? AND no_sucursal = ?";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setDate(1, Date.valueOf(fechaInicial));
            ps.setDate(2, Date.valueOf(fechaFinal));
            ps.setInt(3, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

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

        }
        return lista;
    }

    @Override
    public Factura buscarUno(String folio) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        return null;
    }

    public List<Factura> buscarPorPartidaPresupuestal(String partidaBuscar)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException{
        List<Factura> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException("Error: No se pudo conectar a la base de datos");
            }

            String consulta = "SELECT folio, fecha_factura, p.rfc, p.razon_social, p.telefono, no_sucursal FROM vista_facturas_partida vf " +
                    "JOIN proveedor p ON vf.rfc = p.rfc WHERE partida_presupuestal = ? AND no_sucursal = ? ";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, partidaBuscar);
            ps.setInt(2, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Factura factura = new Factura();
                factura.setFolio(rs.getString("folio"));
                factura.setFecha(rs.getDate("fecha_factura"));
                factura.setRfc(rs.getString("p.rfc"));
                factura.setRazonSocial(rs.getString("p.razon_social"));
                factura.setTelefono(rs.getString("p.telefono"));
                factura.setNoSucursal(rs.getInt("no_sucursal"));
                lista.add(factura);
            }

        }

        return lista;
    }

    public List<Factura> buscarPorPartidaYFecha(String partidaBuscar, LocalDate value, LocalDate value1)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException{
        List<Factura> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException("Error: No se pudo conectar a la base de datos");
            }

            String consulta = "SELECT folio, fecha_factura, rfc, razon_social, telefono, no_sucursal FROM vista_facturas_lista " +
                    "WHERE fecha_factura BETWEEN ? AND ? AND no_sucursal = ?";

        }

        return lista;
    }
}
