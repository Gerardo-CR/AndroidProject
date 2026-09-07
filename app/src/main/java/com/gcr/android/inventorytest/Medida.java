package com.gcr.android.inventorytest;

/**
 * Created by Gerardo Castillo on 01/01/2018.
 */

public class Medida {

    private int id;
    private String medida;
    private String alias;

    public Medida(int id, String medida, String alias) {
        this.id = id;
        this.medida = medida;
        this.alias = alias;
    }

    public Medida(String medida, String alias) {
        this(0, medida, alias);
    }

    public int getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString()
    {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }
}
