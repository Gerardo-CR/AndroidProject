package com.itsp.android.innovacion2016;

public class ProductosVendidos {

    private String nombre;
    private String fecha;
    private int cantidad;
    private double precio;

    public ProductosVendidos(String nombre, int cantidad, String fecha){

        this.nombre=nombre;
        this.cantidad=cantidad;
        this.fecha=fecha;

    }

    public ProductosVendidos(String nombre, int cantidad, String fecha, double precio) {
        this(nombre, cantidad, fecha);
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public double getPrecio() {

        return precio;
    }
}
