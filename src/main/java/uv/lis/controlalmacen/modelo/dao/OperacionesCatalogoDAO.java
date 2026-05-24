package uv.lis.controlalmacen.modelo.dao;

import java.sql.SQLException;
import java.util.List;

public interface OperacionesCatalogoDAO <T,Y>{
    boolean registrar(T entidad) throws SQLException, NullPointerException;

    boolean eliminar(T entidad) throws SQLException, NullPointerException;

    boolean actualizar(T entidad) throws SQLException, NullPointerException;

    List<T> buscarTodos() throws SQLException, NullPointerException;
    
    T buscarUno(Y id) throws SQLException, NullPointerException;
}