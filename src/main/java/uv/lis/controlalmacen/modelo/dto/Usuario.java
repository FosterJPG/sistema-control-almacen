package uv.lis.controlalmacen.modelo.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Usuario {
    private String idUsuario;
    private byte[] password;
    private Date fechaRegistro;
    private Empleado empleado;
    private Integer idRol;
    private String descripcionRol;
    private Rol rol;

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaRegistroTexto() {
        if (fechaRegistro == null) {
            return "";
        }

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.format(fechaRegistro);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getDescripcionRol() {
        return descripcionRol;
    }

    public void setDescripcionRol(String descripcionRol) {
        this.descripcionRol = descripcionRol;
    }

    public String getNombreCompletoEmpleado() {
        if (empleado == null) {
            return "";
        }

        String materno = empleado.getMaterno();

        if (materno == null) {
            materno = "";
        }

        return empleado.getNombre() + " " + empleado.getPaterno() + " " + materno;
    }

    public String getCorreoEmpleado() {
        if (empleado == null || empleado.getCorreoElectronico() == null) {
            return "";
        }

        return empleado.getCorreoElectronico();
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}