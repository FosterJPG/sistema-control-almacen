/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

public class ItemAlmacenadoDAO implements OperacionesCatalogoDAO<ItemAlmacenado, String> {

    @Override
    public boolean registrar(ItemAlmacenado itemAlmacenado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "INSERT INTO almacena(id_item, no_sucursal, existencias, stock_min, stock_max) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, itemAlmacenado.getIdItem());
            sentencia.setInt(2, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
            sentencia.setInt(3, itemAlmacenado.getExistencias());
            sentencia.setInt(4, itemAlmacenado.getStockMin());
            sentencia.setInt(5, itemAlmacenado.getStockMax());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(ItemAlmacenado itemAlmacenado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        return darDeBaja(itemAlmacenado, "Baja de ítem en sucursal");
    }

    public boolean darDeBaja(ItemAlmacenado itemAlmacenado, String razon)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            if (estaDadoDeBaja(itemAlmacenado.getIdItem())) {
                throw new SQLException("El ítem ya se encuentra dado de baja en esta sucursal.");
            }

            String consulta = "{CALL registrar_baja_item(CURDATE(), ?, ?, ?)}";

            CallableStatement sentencia = conn.prepareCall(consulta);
            sentencia.setString(1, razon);
            sentencia.setString(2, itemAlmacenado.getIdItem());
            sentencia.setInt(3, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            sentencia.execute();
            return true;
        }
    }

    public boolean estaDadoDeBaja(String idItem)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT COUNT(*) AS total " +
                    "FROM vista_bitacora_bajas " +
                    "WHERE id_item = ? AND no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, idItem);
            sentencia.setInt(2, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("total") > 0;
            }
        }

        return false;
    }

    @Override
    public boolean actualizar(ItemAlmacenado itemAlmacenado)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "UPDATE almacena " +
                    "SET stock_min = ?, stock_max = ? " +
                    "WHERE id_item = ? AND no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, itemAlmacenado.getStockMin());
            sentencia.setInt(2, itemAlmacenado.getStockMax());
            sentencia.setString(3, itemAlmacenado.getIdItem());
            sentencia.setInt(4, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public List<ItemAlmacenado> buscarTodos()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<ItemAlmacenado> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, descripcion, codigo, partida_presupuestal, existencias, stock_max, stock_min " +
                    "FROM vista_items_almacenados WHERE no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
                itemAlmacenado.setIdItem(resultado.getString("id_item"));
                itemAlmacenado.setDescripcionItem(resultado.getString("descripcion"));
                itemAlmacenado.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                itemAlmacenado.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                itemAlmacenado.setExistencias(resultado.getInt("existencias"));
                itemAlmacenado.setStockMax(resultado.getInt("stock_max"));
                itemAlmacenado.setStockMin(resultado.getInt("stock_min"));
                lista.add(itemAlmacenado);
            }
        }

        return lista;
    }

    @Override
    public ItemAlmacenado buscarUno(String id)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        ItemAlmacenado itemBuscado = new ItemAlmacenado();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, descripcion, codigo, partida_presupuestal, existencias, stock_max, stock_min " +
                    "FROM vista_items_almacenados WHERE no_sucursal = ? AND id_item = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
            sentencia.setString(2, id);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                itemBuscado.setIdItem(resultado.getString("id_item"));
                itemBuscado.setDescripcionItem(resultado.getString("descripcion"));
                itemBuscado.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                itemBuscado.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                itemBuscado.setExistencias(resultado.getInt("existencias"));
                itemBuscado.setStockMax(resultado.getInt("stock_max"));
                itemBuscado.setStockMin(resultado.getInt("stock_min"));
            }
        }

        return itemBuscado;
    }

    public ItemAlmacenado buscarUnoIncluyendoBajas(String id)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        ItemAlmacenado itemBuscado = new ItemAlmacenado();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT " +
                    "i.id_item, " +
                    "i.descripcion, " +
                    "pp.codigo, " +
                    "pp.descripcion_partida AS partida_presupuestal, " +
                    "a.existencias, " +
                    "a.stock_max, " +
                    "a.stock_min " +
                    "FROM almacena a " +
                    "JOIN item i ON a.id_item = i.id_item " +
                    "JOIN partida_presupuestal pp ON i.codigo = pp.codigo " +
                    "WHERE a.no_sucursal = ? AND a.id_item = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
            sentencia.setString(2, id);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                itemBuscado.setIdItem(resultado.getString("id_item"));
                itemBuscado.setDescripcionItem(resultado.getString("descripcion"));
                itemBuscado.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                itemBuscado.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                itemBuscado.setExistencias(resultado.getInt("existencias"));
                itemBuscado.setStockMax(resultado.getInt("stock_max"));
                itemBuscado.setStockMin(resultado.getInt("stock_min"));
            }
        }

        return itemBuscado;
    }

    public List<ItemAlmacenado> buscarPorStock(String stock)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<ItemAlmacenado> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta;

            if ("Sobre el máximo".equals(stock)) {
                consulta = "SELECT id_item, item, codigo, partida_presupuestal, existencias, stock_min, stock_max " +
                        "FROM vista_stock_maximo WHERE no_sucursal = ?";
            } else {
                consulta = "SELECT id_item, item, codigo, partida_presupuestal, existencias, stock_min, stock_max " +
                        "FROM vista_stock_minimo WHERE no_sucursal = ?";
            }

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
                itemAlmacenado.setIdItem(resultado.getString("id_item"));
                itemAlmacenado.setDescripcionItem(resultado.getString("item"));
                itemAlmacenado.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                itemAlmacenado.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                itemAlmacenado.setExistencias(resultado.getInt("existencias"));
                itemAlmacenado.setStockMax(resultado.getInt("stock_max"));
                itemAlmacenado.setStockMin(resultado.getInt("stock_min"));
                lista.add(itemAlmacenado);
            }
        }

        return lista;
    }

    public List<ItemAlmacenado> buscarPorDescripcion(String descripcion)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<ItemAlmacenado> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, descripcion, codigo, partida_presupuestal, existencias, stock_max, stock_min " +
                    "FROM vista_items_almacenados " +
                    "WHERE no_sucursal = ? AND descripcion LIKE ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
            sentencia.setString(2, "%" + descripcion + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
                itemAlmacenado.setIdItem(resultado.getString("id_item"));
                itemAlmacenado.setDescripcionItem(resultado.getString("descripcion"));
                itemAlmacenado.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                itemAlmacenado.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                itemAlmacenado.setExistencias(resultado.getInt("existencias"));
                itemAlmacenado.setStockMax(resultado.getInt("stock_max"));
                itemAlmacenado.setStockMin(resultado.getInt("stock_min"));
                lista.add(itemAlmacenado);
            }
        }

        return lista;
    }

    public List<ItemAlmacenado> buscarPorPartida(Integer codigoPartida)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<ItemAlmacenado> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT id_item, descripcion, codigo, partida_presupuestal, existencias, stock_max, stock_min " +
                    "FROM vista_items_almacenados " +
                    "WHERE no_sucursal = ? AND codigo = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
            sentencia.setInt(2, codigoPartida);

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
                itemAlmacenado.setIdItem(resultado.getString("id_item"));
                itemAlmacenado.setDescripcionItem(resultado.getString("descripcion"));
                itemAlmacenado.setCodigoPartidaPresupuestal(resultado.getInt("codigo"));
                itemAlmacenado.setDescripcionPartida(resultado.getString("partida_presupuestal"));
                itemAlmacenado.setExistencias(resultado.getInt("existencias"));
                itemAlmacenado.setStockMax(resultado.getInt("stock_max"));
                itemAlmacenado.setStockMin(resultado.getInt("stock_min"));
                lista.add(itemAlmacenado);
            }
        }

        return lista;
    }

    public boolean existeEnSucursal(String idItem)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT COUNT(*) AS total " +
                    "FROM almacena " +
                    "WHERE id_item = ? AND no_sucursal = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, idItem);
            sentencia.setInt(2, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("total") > 0;
            }
        }

        return false;
    }
}