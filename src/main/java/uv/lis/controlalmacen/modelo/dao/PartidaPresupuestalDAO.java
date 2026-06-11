/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uv.lis.controlalmacen.modelo.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uv.lis.controlalmacen.db.ConnectionFactory;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;
import uv.lis.controlalmacen.modelo.dto.Sesion;

import static uv.lis.controlalmacen.utilidades.Constantes.MSJ_SIN_CONEXION;

/**
 *
 * @author macol
 */
public class PartidaPresupuestalDAO implements OperacionesCatalogoDAO<PartidaPresupuestal,Integer> {
    @Override
    public boolean registrar(PartidaPresupuestal partidaPresupuestal) throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "INSERT INTO partida_presupuestal(descripcion_partida) VALUES (?)";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, partidaPresupuestal.getDescripcionPartida());

            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(PartidaPresupuestal partidaPresupuestal) throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "DELETE FROM partida_presupuestal WHERE codigo = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, partidaPresupuestal.getCodigo());

            return sentencia.executeUpdate() > 0;

        }
    }

    @Override
    public boolean actualizar(PartidaPresupuestal partidaPresupuestal)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "UPDATE partida_presupuestal SET descripcion_partida = ? WHERE codigo = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, partidaPresupuestal.getDescripcionPartida());
            sentencia.setInt(2, partidaPresupuestal.getCodigo());

            return sentencia.executeUpdate() > 0;

        }
    }

    @Override
    public List<PartidaPresupuestal> buscarTodos()
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<PartidaPresupuestal> partidas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT codigo, descripcion_partida FROM partida_presupuestal ORDER BY codigo";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                PartidaPresupuestal partidaPresupuestal = new PartidaPresupuestal();
                partidaPresupuestal.setCodigo(resultado.getInt("codigo"));
                partidaPresupuestal.setDescripcionPartida(resultado.getString("descripcion_partida"));
                partidas.add(partidaPresupuestal);
            }
        }

        return partidas;
    }

    @Override
    public PartidaPresupuestal buscarUno(Integer codigo)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        PartidaPresupuestal partidaPresupuestal = new PartidaPresupuestal();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT codigo, descripcion_partida FROM partida_presupuestal WHERE codigo = ?";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, codigo);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                partidaPresupuestal.setCodigo(resultado.getInt("codigo"));
                partidaPresupuestal.setDescripcionPartida(resultado.getString("descripcion_partida"));
            }
        }

        return partidaPresupuestal;
    }

    public List<PartidaPresupuestal> buscarPorNombre(String nombre)
            throws SQLException, NullPointerException, IOException, ClassNotFoundException {

        List<PartidaPresupuestal> partidasPresupuestales = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.getUsuarioActual().getRol())) {
            if (conn == null) {
                throw new SQLException(MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT codigo, descripcion_partida " +
                    "FROM partida_presupuestal " +
                    "WHERE descripcion_partida LIKE ? " +
                    "ORDER BY descripcion_partida";

            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, "%" + nombre + "%");

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                PartidaPresupuestal partidaPresupuestal = new PartidaPresupuestal();
                partidaPresupuestal.setCodigo(resultado.getInt("codigo"));
                partidaPresupuestal.setDescripcionPartida(resultado.getString("descripcion_partida"));
                partidasPresupuestales.add(partidaPresupuestal);
            }
        }

        return partidasPresupuestales;
    }
}
