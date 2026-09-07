package com.gcr.android.inventorytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterProductosVenta extends ArrayAdapter<Producto> {
    ArrayList<Producto> items;
    Context context;
    public AdapterProductosVenta(Context context, ArrayList<Producto> items){
        super(context, R.layout.item_producto_venta, items);
        this.context=context;
        this.items=items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_producto_venta, parent, false);

            holder= new ViewHolder();
            holder.barcode = (TextView)convertView.findViewById(R.id.itmTextBarcodeVenta);
            holder.producto = (TextView)convertView.findViewById(R.id.itmTextProductoVenta);
            holder.precio = (TextView)convertView.findViewById(R.id.itmTextPrecioVenta);
            holder.id = (TextView)convertView.findViewById(R.id.itmTextIDProductoVenta);
            holder.cantidad = (TextView)convertView.findViewById(R.id.itmTextCantidadVenta);
            holder.subtotal = (TextView)convertView.findViewById(R.id.itmTextSubtotalVenta);
            holder.aumentar = (Button)convertView.findViewById(R.id.itmBtnAumentarCaVenta);
            holder.reducir = (Button)convertView.findViewById(R.id.itmBtnReducirCaVenta);
            holder.aumentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PuntoVentaActivity.actualizarLista(holder.barcode.getText().toString(), true);
                }
            });
            holder.reducir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PuntoVentaActivity.actualizarLista(holder.barcode.getText().toString(), false);
                }
            });
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        double subtotal=items.get(position).getPrecio()*items.get(position).getCantidad();
        holder.producto.setText(items.get(position).getNombre());
        holder.precio.setText(String.valueOf(items.get(position).getPrecio()));
        holder.barcode.setText(items.get(position).getBarcode());
        holder.id.setText(String.valueOf(items.get(position).getId()));
        holder.cantidad.setText(String.valueOf(items.get(position).getCantidad()));
        holder.subtotal.setText(String.valueOf(subtotal));

        return convertView;
    }

    static class ViewHolder{
        TextView producto;
        TextView barcode;
        TextView id;
        TextView precio;
        TextView cantidad;
        TextView subtotal;
        Button aumentar;
        Button reducir;
    }
}
