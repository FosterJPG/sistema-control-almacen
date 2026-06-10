package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.Kardex;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.Constantes;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KardexDAO {
    public static List<Kardex> buscarKardexItem(String idItem, int noSucursal) throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<Kardex> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, no_sucursal, folio, fecha_factura, costo_unitario, costo_promedio "
                    + "FROM vista_kardex_item WHERE id_item = ?  AND no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, idItem);
            sentencia.setInt(2, noSucursal);

            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                Kardex kardex = new Kardex();
                kardex.setFolioFactura(resultado.getString("folio"));
                kardex.setCostoPromedio(resultado.getDouble("costo_promedio"));
                kardex.setCostoUnitario(resultado.getDouble("costo_unitario"));
                kardex.setFechaFactura(resultado.getDate("fecha_factura"));
                lista.add(kardex);
            }
        }
        return lista;
    }
}
