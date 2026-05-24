package uv.lis.controlalmacen.modelo.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import uv.lis.controlalmacen.modelo.dto.Item;

public class ItemDAO implements OperacionesCatalogoDAO<Item, Integer>{

    @Override
    public boolean registrar(Item item) throws SQLException, NullPointerException {
    
        return true;
    }

    @Override
    public boolean eliminar(Item item) throws SQLException, NullPointerException {
        return true;
    }

    @Override
    public boolean actualizar(Item item) throws SQLException, NullPointerException {
        return true;
    }

    @Override
    public List<Item> buscarTodos()throws SQLException, NullPointerException {
        List<Item> items = new ArrayList<>();
      
        return items;
    }

    @Override
    public Item buscarUno(Integer id) throws SQLException, NullPointerException {
        Item item = new Item();
        
        return item;
    }

}
