/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uv.lis.controlalmacen.modelo.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import uv.lis.controlalmacen.modelo.dto.PartidaPresupuestal;

/**
 *
 * @author macol
 */
public class PartidaPresupuestalDAO implements OperacionesCatalogoDAO<PartidaPresupuestal,Integer> {

    @Override
    public boolean registrar(PartidaPresupuestal partidaPresupuestal) throws SQLException, NullPointerException{
        return true;
    }

    @Override
    public boolean eliminar(PartidaPresupuestal partidaPresupuestal) throws SQLException, NullPointerException {
        return true;
    }

    @Override
    public boolean actualizar(PartidaPresupuestal partidaPresupuestal) throws SQLException, NullPointerException {
        return true;
    }

    @Override
    public List<PartidaPresupuestal> buscarTodos() throws SQLException, NullPointerException {
        List<PartidaPresupuestal> partida = new ArrayList<>();
        
        return partida;
    }

    @Override
    public PartidaPresupuestal buscarUno(Integer id) throws SQLException, NullPointerException {
        PartidaPresupuestal partidaPresupuestal = new PartidaPresupuestal();
        
        return partidaPresupuestal;
    }

 


    
}
