package uv.lis.controlalmacen.modelo.dao;

public interface OperacionesCatalogoDAO <T>{
    boolean registrar(T entidad);

    boolean eliminar(T entidad);

    boolean actualizar(T entidad);

    T buscar();
}