package com.gcr.android.inventorytest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.gcr.android.inventorytest.Venta.DEF;

/**
 * Created by Gerardo Castillo on 06/01/2018.
 */

public class FilaProducto extends LinearLayout {

    private TextView nombre;
    private TextView precio;
    private TextView cantidad;
    private TextView subtotal;

    public FilaProducto(Context context) {
        super(context);
        inicializar();
    }

    public FilaProducto(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inicializar();
    }

    public FilaProducto(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    private void inicializar() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_filaproductos, this, true);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        setLayoutParams(params);

        nombre = (TextView) findViewById(R.id.txt_producto);
        precio = (TextView) findViewById(R.id.txt_precio);
        cantidad = (TextView) findViewById(R.id.txt_cantidad);
        subtotal = (TextView) findViewById(R.id.txt_subtotal);

    }

    public void setDatos(ProductosVendidos productos) {
        nombre.setText(productos.getNombre());
        precio.setText(DEF.format(productos.getPrecio()));
        cantidad.setText(String.valueOf(productos.getCantidad()));
        subtotal.setText(DEF.format(productos.getPrecio() * productos.getCantidad()));
    }
}
