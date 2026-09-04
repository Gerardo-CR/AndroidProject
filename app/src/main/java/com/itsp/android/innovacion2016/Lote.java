package com.itsp.android.innovacion2016;

public class Lote {

    private int idlote;
    private int numero;
    private String caducidad;
    private String entrada;
    private int cantidadlote;
    private int id_producto;


    public Lote(int id,int numero,String caducidad,String entrada,int cantidad){
        this.idlote = id;
        this.numero = numero;
        this.caducidad = caducidad;
        this.entrada = entrada;
        this.cantidadlote = cantidad;
    }

    public int getIdlote() {
        return idlote;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getId_producto() {
        return id_producto;
    }

    public int getNumero() {
        return numero;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public String getEntrada() {
        return entrada;
    }

    public int getCantidadlote() {
        return cantidadlote;
    }

}
