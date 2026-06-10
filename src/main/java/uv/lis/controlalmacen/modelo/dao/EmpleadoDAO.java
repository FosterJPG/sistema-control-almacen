package uv.lis.controlalmacen.modelo.dao;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.Departamento;
import uv.lis.controlalmacen.modelo.dto.Empleado;
import uv.lis.controlalmacen.modelo.dto.Puesto;
import uv.lis.controlalmacen.modelo.dto.Sucursal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmpleadoDAO implements OperacionesCatalogoDAO<Empleado, Integer>{
    @Override
    public boolean registrar(Empleado empleado) throws SQLException, NullPointerException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean eliminar(Empleado empleado) throws SQLException, NullPointerException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean actualizar(Empleado empleado) throws SQLException, NullPointerException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Empleado> buscarTodos() throws SQLException, NullPointerException, IOException, ClassNotFoundException {
        return List.of();
    }

        @Override
        public Empleado buscarUno(Integer id) throws SQLException, NullPointerException, IOException, ClassNotFoundException {

            Empleado empleado = new Empleado();

            try (Connection conn = ConnectionFactory.crearParaAutenticacion()){
                if (conn == null) {
                    throw new SQLException("Error: No se pudo conectar a la base de datos");
                }

                String query = "SELECT no_empleado, id_puesto, puesto, nombre, paterno, materno, direccion, correo_electronico, " +
                        "telefono, id_depto, departamento, no_sucursal, sucursal FROM vista_datos_empleados WHERE no_empleado = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    Sucursal sucursal = new Sucursal();
                    sucursal.setNoSucursal(rs.getInt("no_sucursal"));
                    sucursal.setNombre(rs.getString("sucursal"));

                    Puesto puesto = new Puesto();
                    puesto.setIdPuesto(rs.getInt("id_puesto"));
                    puesto.setPuesto(rs.getString("puesto"));

                    Departamento departamento = new Departamento();
                    departamento.setIdDepto(rs.getInt("id_depto"));
                    departamento.setDescripcion(rs.getString("departamento"));
                    departamento.setSucursal(sucursal);

                    empleado.setNoEmpleado(rs.getInt("no_empleado"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setPaterno(rs.getString("paterno"));
                    empleado.setMaterno(rs.getString("materno"));
                    empleado.setDireccion(rs.getString("direccion"));
                    empleado.setCorreoElectronico(rs.getString("correo_electronico"));
                    empleado.setTelefono(rs.getString("telefono"));

                    empleado.setPuesto(puesto);
                    empleado.setDepartamento(departamento);
                }
            }

            return empleado;
        }
}
