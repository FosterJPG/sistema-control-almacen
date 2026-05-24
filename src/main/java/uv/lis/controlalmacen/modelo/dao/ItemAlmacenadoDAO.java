/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.ItemAlmacenado;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author macol
 */
public class ItemAlmacenadoDAO implements OperacionesCatalogoDAO<ItemAlmacenado,String>{

    @Override
    public boolean registrar(ItemAlmacenado itemAlmacenado) throws SQLException, NullPointerException, ClassNotFoundException {
        return true;
    }

    @Override
    public boolean eliminar(ItemAlmacenado itemAlmacenado) throws SQLException, NullPointerException, ClassNotFoundException {
        return true;
    }

    @Override
    public boolean actualizar(ItemAlmacenado itemAlmacenado) throws SQLException, NullPointerException, ClassNotFoundException {
        return true;
    }

    @Override
    public List<ItemAlmacenado> buscarTodos() throws SQLException, NullPointerException, ClassNotFoundException, IOException {
        List<ItemAlmacenado> lista = new ArrayList<>();

        try(Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())){
            if(conn != null) {
                String consulta = "SELECT id_item, descripcion, existencias, stock_max, stock_min "
                        + "FROM vista_items_almacenados WHERE no_sucursal = ?;";
                PreparedStatement sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

                ResultSet resultado = sentencia.executeQuery();
                while (resultado.next()) {
                    ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
                    itemAlmacenado.setIdItem(resultado.getString("id_item"));
                    itemAlmacenado.setDescripcionItem(resultado.getString("descripcion"));
                    itemAlmacenado.setExistencias(resultado.getInt("existencias"));
                    itemAlmacenado.setStockMax(resultado.getInt("stock_max"));
                    itemAlmacenado.setStockMin(resultado.getInt("stock_min"));
                    lista.add(itemAlmacenado);
                }
                return lista;
            }
            throw new SQLException("No hay conexión con el almacenamiento de información");
        }
    }

    @Override
    public ItemAlmacenado buscarUno(String id) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        // PARA OBTENER EL NUMERO DE SUCURSAL     ps.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

        return null;
    }


    public List<ItemAlmacenado> buscarPorStock(String stock) throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        //Pregunta si fue "Sobre el máximo" o "Menor que el mínimo" para saber a que vista llamar
        // vista_stock_minimo
        // vista_stock_maximo

        // PARA OBTENER EL NUMERO DE SUCURSAL     ps.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());
        List<ItemAlmacenado> lista = new ArrayList<>();
        String consulta;

        if("Sobre el máximo".equals(stock)){
            consulta = "SELECT id_item, item, existencias, stock_min, stock_max " +
                    "FROM vista_stock_maximo WHERE no_sucursal = ?;";
        }else{
            consulta = "SELECT id_item, item, existencias, stock_min, stock_max " +
                    "FROM vista_stock_minimo WHERE no_sucursal = ?;";
        }

        try(Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())){
            if(conn != null) {
                PreparedStatement sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, Sesion.getUsuarioActual().getEmpleado().getDepartamento().getSucursal().getNoSucursal());

                ResultSet resultado = sentencia.executeQuery();
                while (resultado.next()) {
                    ItemAlmacenado itemAlmacenado = new ItemAlmacenado();
                    itemAlmacenado.setIdItem(resultado.getString("id_item"));
                    itemAlmacenado.setDescripcionItem(resultado.getString("item"));
                    itemAlmacenado.setExistencias(resultado.getInt("existencias"));
                    itemAlmacenado.setStockMax(resultado.getInt("stock_max"));
                    itemAlmacenado.setStockMin(resultado.getInt("stock_min"));
                    lista.add(itemAlmacenado);
                }
                return lista;
            }
            throw new SQLException("No hay conexión con el almacenamiento de información");
        }
    }
}
