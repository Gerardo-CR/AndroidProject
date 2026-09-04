package com.itsp.android.innovacion2016;

public class Producto {

    private String nombre;
    private String barcode;
    private int id;
    private double precio;
    private double cantidad = 1.0;
    private double stock;
    private double reorden;
    private Medida medida;

    public Producto(String nombre, String barcode, int id, double precio)
    {
        this.barcode = barcode;
        this.nombre = nombre;
        this.id = id;
        this.precio = precio;

    }

    public double getPrecio() {
        return precio;
    }

    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public void setStock(double stock){
        this.stock= stock;
    }

    public double getStock() {
        return stock;
    }

    public void setReorden(double reorden) {
        this.reorden = reorden;
    }

    public double getReorden() {
        return reorden;
    }

    public void setMedida(Medida medida) {
        this.medida = medida;
    }

    public String getAliasMedida(){
        return medida.getAlias();
    }

    public int getIdMedida() {
        return medida.getId();
    }
}
