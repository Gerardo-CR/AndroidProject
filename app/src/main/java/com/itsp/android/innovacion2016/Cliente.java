package com.itsp.android.innovacion2016;


import android.database.Cursor;

/**
 * Created by Gerardo Castillo on 13/09/2017.
 */

public class Cliente {

    private int id;
    private String Nombre;
    private String Apellidos;
    private String Telefono;
    private String Correo;
    private String Direccion;

    public Cliente(int id, String Nombre, String Apellidos) {
        this.id = id;
        this.Nombre = Nombre;
        this.Apellidos = Apellidos;
    }

    public Cliente(String Nombre, String Apellidos){
        this(0, Nombre, Apellidos);
    }

    public Cliente (String Nombre){
        this(Nombre, "");
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.Apellidos = apellidos;
    }

    public void setCorreo(String Correo){
        this.Correo = Correo;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }

    public void setDireccion(String Direccion){
        this.Direccion = Direccion;
    }

    public int getId(){
        return id;
    }

    public String getNombre(){
        return Nombre;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public String getTelefono() {
        return Telefono;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getDireccion() {
        return Direccion;
    }

    @Override
    public String toString(){
        return (Nombre + ' ' + Apellidos).trim();
    }
}
