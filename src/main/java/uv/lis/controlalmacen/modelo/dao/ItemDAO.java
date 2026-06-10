package uv.lis.controlalmacen.modelo.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Item;
import uv.lis.controlalmacen.modelo.dto.Sesion;
import uv.lis.controlalmacen.utilidades.Constantes;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class ItemDAO implements OperacionesCatalogoDAO<Item, String>{

    @Override
    public boolean registrar(Item item)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException{

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "INSERT INTO item(id_item, descripcion, codigo) VALUES (?, ?, ?)";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, item.getIdItem());
            sentencia.setString(2, item.getDescripcionItem());
            sentencia.setInt(3, item.getCodigoPartidaPresupuestal());

            return sentencia.executeUpdate() > 0;

        }
    }

    @Override
    public boolean eliminar(Item item) throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "DELETE FROM item WHERE id_item = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, item.getIdItem());

            return sentencia.executeUpdate() > 0;

        }
    }

    @Override
    public boolean actualizar(Item item) throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "UPDATE item SET descripcion = ?, codigo = ? WHERE id_item = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, item.getDescripcionItem());
            sentencia.setInt(2, item.getCodigoPartidaPresupuestal());
            sentencia.setString(3, item.getIdItem());

            return sentencia.executeUpdate() > 0;

        }
    }

    @Override
    public List<Item> buscarTodos() throws SQLException, NullPointerException, ClassNotFoundException, IOException {

        List<Item> items = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, item, codigo, partida_presupuestal " +
                    "FROM vista_items_catalogo ORDER BY id_item";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Item item = new Item();
                item.setIdItem(resultado.getString("id_item"));
                item.setDescripcionItem(resultado.getString("item"));
                item.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                item.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                items.add(item);
            }
        }

        return items;
    }

    @Override
    public Item buscarUno(String idItem)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        Item item = new Item();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, item, codigo, partida_presupuestal " +
                    "FROM vista_items_catalogo WHERE id_item = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, idItem);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                item.setIdItem(resultado.getString("id_item"));
                item.setDescripcionItem(resultado.getString("item"));
                item.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                item.setDescripcionPartida(resultado.getString("partida_presupuestal"));
            }
        }

        return item;
    }
}
