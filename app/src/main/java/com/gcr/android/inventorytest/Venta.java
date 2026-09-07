package com.gcr.android.inventorytest;

import java.text.DecimalFormat;

public class Venta {

    static final DecimalFormat DEF = new DecimalFormat("$0.00");
    private int id;
    private String fecha;
    private String hora;
    private double total;
    private double pago;

    //Datos para ventas por cobrar********************************
    public static final int VENTA_CONTADO = 1;
    public static final int VENTA_CREDITO = 0;
    private int estatus = VENTA_CONTADO;
    private String plazo;




    public Venta(int id, String fecha, String hora, double total, double pago){
        this.id     = id;
        this.fecha  = fecha;
        this.hora   = hora;
        this.total  = total;
        this.pago   = pago;
    }

    public int getId() {
        return id;
    }

    public double getPago() {
        return pago;
    }

    public double getTotal() {
        return total;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public double getdeuda() {
        double aux = total - pago;
        if (aux >= 0)
            return aux;
        else
            return 0.00;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setPago(double pago) {
        this.pago = pago;
    }

    // //Datos para ventas por cobrar********************************

    public void setEstatus (int estatus){
        this.estatus = estatus;
    }

    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }


    public int getEstatus() {
        return estatus;
    }

    public String getPlazo() {
        return plazo;
    }
}
