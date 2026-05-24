package uv.lis.controlalmacen.modelo.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface OperacionesCatalogoDAO <T,Y>{
    boolean registrar(T entidad) throws SQLException, NullPointerException, ClassNotFoundException;

    boolean eliminar(T entidad) throws SQLException, NullPointerException, ClassNotFoundException;

    boolean actualizar(T entidad) throws SQLException, NullPointerException, ClassNotFoundException;

    List<T> buscarTodos() throws SQLException, NullPointerException, ClassNotFoundException;

    T buscarUno(Y id) throws SQLException, NullPointerException, IOException, ClassNotFoundException;
}