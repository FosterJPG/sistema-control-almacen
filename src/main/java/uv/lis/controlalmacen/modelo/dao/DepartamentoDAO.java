package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class DepartamentoDAO {

    public List<Departamento> buscarTodos() throws SQLException, IOException, ClassNotFoundException {
        return buscarPorSucursal(null);
    }

    public List<Departamento> buscarPorSucursal(Integer noSucursal) throws SQLException, IOException, ClassNotFoundException {
        List<Departamento> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) throw new SQLException(MSJ_SIN_CONEXION);
            String sql = noSucursal == null
                    ? "SELECT id_depto, descripcion, no_sucursal FROM departamento ORDER BY descripcion"
                    : "SELECT id_depto, descripcion, no_sucursal FROM departamento WHERE no_sucursal = ? ORDER BY descripcion";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (noSucursal != null) ps.setInt(1, noSucursal);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Departamento d = new Departamento();
                    d.setIdDepto(rs.getInt("id_depto"));
                    d.setDescripcion(rs.getString("descripcion"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }
}
