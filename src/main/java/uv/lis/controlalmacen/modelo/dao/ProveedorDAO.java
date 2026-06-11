package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Proveedor;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;

public class ProveedorDAO {

    public static Proveedor buscarUno(String rfc)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException {

        Proveedor p = new Proveedor();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT rfc, razon_social, domicilio_fiscal, telefono FROM proveedor WHERE rfc = ?";
            PreparedStatement ps  = conn.prepareStatement(consulta);
            ps.setString(1, rfc);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p.setRazonSocial(rs.getString("razon_social"));
                p.setDomicilioFiscal(rs.getString("domicilio_fiscal"));
                p.setTelefono(rs.getString("telefono"));
                p.setRfc(rs.getString("rfc"));
            }
        }

        return p;
    }

    public static boolean existeRFC(String rfc)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException{

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT rfc FROM proveedor WHERE rfc = ?";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, rfc);
            ResultSet rs = ps.executeQuery();

            if  (rs.next()) {
                return true;
            }
        }

        return false;
    }

    public static boolean registrarProveedor(Proveedor proveedor)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException{

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "INSERT INTO proveedor (rfc, razon_social, domicilio_fiscal, telefono) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, proveedor.getRfc());
            ps.setString(2, proveedor.getRazonSocial());
            ps.setString(3, proveedor.getDomicilioFiscal());
            ps.setString(4, proveedor.getTelefono());
            int resultado = ps.executeUpdate();


            if  (resultado > 0) {
                return true;
            }
        }

        return false;
    }
}
