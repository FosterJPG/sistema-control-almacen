package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Puesto;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class PuestoDAO {

    public List<Puesto> buscarTodos() throws SQLException, IOException, ClassNotFoundException {
        List<Puesto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id_puesto, puesto FROM puesto ORDER BY puesto")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Puesto p = new Puesto();
                    p.setIdPuesto(rs.getInt("id_puesto"));
                    p.setPuesto(rs.getString("puesto"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}
