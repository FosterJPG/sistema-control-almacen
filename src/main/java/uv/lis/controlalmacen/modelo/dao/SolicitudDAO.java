package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.modelo.dto.Solicitud;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    /**
     * Registra una solicitud completa usando la tabla temporal + SP registrar_solicitud.
     * Todo ocurre en la MISMA conexión porque temp_items_solicitud es de sesión.
     */
    public boolean registrar(List<DetallesSolicitud> detalles, Date fecha, int noEmpleado, int noSucursal)
            throws SQLException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer conexión con la base de datos");
            }

            // 1. Recrear la tabla temporal con el tipo correcto en esta sesión JDBC.
            //    La tabla es TEMPORARY (por sesión), no existe al abrir una conexión nueva.
            //    El SQL original la define con id_item INT, pero los IDs son VARCHAR(5) — se corrige aquí.
            try (PreparedStatement drop = conn.prepareStatement(
                    "DROP TEMPORARY TABLE IF EXISTS temp_items_solicitud")) {
                drop.executeUpdate();
            }
            try (PreparedStatement create = conn.prepareStatement(
                    "CREATE TEMPORARY TABLE temp_items_solicitud (" +
                    "id_item VARCHAR(5) PRIMARY KEY, " +
                    "cantidad INT NOT NULL, " +
                    "uso VARCHAR(150) NOT NULL)")) {
                create.executeUpdate();
            }

            // 2. Insertar cada ítem en la tabla temporal (misma conexión que el CALL)
            String insertTemp = "INSERT INTO temp_items_solicitud(id_item, cantidad, uso) VALUES(?, ?, ?)";
            try (PreparedStatement psTemp = conn.prepareStatement(insertTemp)) {
                for (DetallesSolicitud detalle : detalles) {
                    psTemp.setString(1, detalle.getIdItem());
                    psTemp.setInt(2, detalle.getCantidad());
                    psTemp.setString(3, detalle.getUso());
                    psTemp.executeUpdate();
                }
            }

            // 3. Llamar al SP — realiza la transacción: INSERT solicitud + detalles, con ROLLBACK en error
            try (CallableStatement cs = conn.prepareCall("{CALL registrar_solicitud(?, ?, ?)}")) {
                cs.setDate(1, fecha);
                cs.setInt(2, noEmpleado);
                cs.setInt(3, noSucursal);
                cs.execute();
            }

            return true;
        }
    }

    public List<Solicitud> buscarPendientes(int noSucursal) throws SQLException, IOException, ClassNotFoundException {
        List<Solicitud> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException("No se pudo conectar a la base de datos");
            String sql = "SELECT no_solicitud, no_empleado, nombre, paterno, no_sucursal, fecha " +
                         "FROM vista_solicitudes_pendientes WHERE no_sucursal = ? ORDER BY fecha DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, noSucursal);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Solicitud s = new Solicitud();
                    s.setNoSolicitud(rs.getInt("no_solicitud"));
                    s.setNoEmpleado(rs.getInt("no_empleado"));
                    s.setNombreEmpleado(rs.getString("nombre"));
                    s.setPaternoEmpleado(rs.getString("paterno"));
                    s.setNoSucursal(rs.getInt("no_sucursal"));
                    s.setFechaSolicitud(rs.getDate("fecha"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    public List<DetallesSolicitud> buscarDetalle(int noSolicitud, int noSucursal)
            throws SQLException, IOException, ClassNotFoundException {
        List<DetallesSolicitud> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException("No se pudo conectar a la base de datos");
            String sql = "SELECT vsd.id_item, vsd.descripcion, vsd.cantidad, vsd.uso, " +
                         "IFNULL(via.existencias, 0) AS existencias " +
                         "FROM vista_solicitud_detalle vsd " +
                         "LEFT JOIN vista_items_almacenados via " +
                         "  ON vsd.id_item = via.id_item AND via.no_sucursal = ? " +
                         "WHERE vsd.no_solicitud = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, noSucursal);
                ps.setInt(2, noSolicitud);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    DetallesSolicitud d = new DetallesSolicitud();
                    d.setIdItem(rs.getString("id_item"));
                    d.setDescripcionItem(rs.getString("descripcion"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setUso(rs.getString("uso"));
                    d.setExistencias(rs.getInt("existencias"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }

    public void aprobar(int noSolicitud) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException("No se pudo conectar a la base de datos");
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE solicitud SET aprobada = TRUE WHERE no_solicitud = ?")) {
                ps.setInt(1, noSolicitud);
                ps.executeUpdate();
            }
        }
    }
}
