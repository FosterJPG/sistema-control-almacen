package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.DetallesSolicitud;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}
