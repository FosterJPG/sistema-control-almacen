package uv.lis.controlalmacen.modelo.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface OperacionesCatalogoDAO <T,Y>{
    boolean registrar(T entidad) throws SQLException, NullPointerException, IOException, ClassNotFoundException;

    boolean eliminar(T entidad) throws SQLException, NullPointerException, IOException, ClassNotFoundException;

    boolean actualizar(T entidad) throws SQLException, NullPointerException, IOException, ClassNotFoundException;

    List<T> buscarTodos() throws SQLException, NullPointerException, IOException, ClassNotFoundException;

    T buscarUno(Y id) throws SQLException, NullPointerException, IOException, ClassNotFoundException;
}