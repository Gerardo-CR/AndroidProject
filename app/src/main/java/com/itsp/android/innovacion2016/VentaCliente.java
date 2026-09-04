package com.itsp.android.innovacion2016;

/**
 * Created by Gerardo Castillo on 08/11/2017.
 */

public class VentaCliente extends Venta {

    private Cliente cliente;

    public VentaCliente(int id, String fecha, String hora, double total, double pago, Cliente cliente) {
        super(id, fecha, hora, total, pago);
        this.cliente = cliente;
    }

    public String getNombreCliente() {
        return cliente.toString();
    }

    public int getIdCliente() {
        return cliente.getId();
    }
}