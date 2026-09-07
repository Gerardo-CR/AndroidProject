package com.gcr.android.inventorytest;

public class Prolote {

    private int id_pro;
    private int diasres;
    private String nombre;
    private int numero;

    public  Prolote(int id,String nombre,int numero,int diasres){

        this.id_pro=id;
        this.numero=numero;
        this.nombre=nombre;
        this.diasres=diasres;
    }

    public int getId_pro() {
        return id_pro;
    }

    public int getDiasres() {
        return diasres;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }
}
