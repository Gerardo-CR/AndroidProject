package com.gcr.android.inventorytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class AdapterDetalleVenta extends ArrayAdapter<HashMap<String, Object>> {
    ArrayList<HashMap<String, Object>> items;
    Context context;
    public AdapterDetalleVenta(Context context, ArrayList<HashMap<String, Object>> items){
        super(context, R.layout.item_detalle_venta, items);
        this.context=context;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_detalle_venta, parent, false);

            holder= new ViewHolder();
            holder.subtotal = (TextView)convertView.findViewById(R.id.itmTextSubtotalDetalle);
            holder.producto = (TextView)convertView.findViewById(R.id.itmTextProductoDetalle);
            holder.precio = (TextView)convertView.findViewById(R.id.itmTextPrecioDetalle);
            holder.cantidad = (TextView)convertView.findViewById(R.id.itmTextCantidadDetalle);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        String nombre = String.valueOf(items.get(position).get("nombre"));
        String precio = String.valueOf(items.get(position).get("precio"));
        String cantidad = String.valueOf(items.get(position).get("cantidad"));
        String subtotal = String.valueOf(Double.parseDouble(precio)*Double.parseDouble(cantidad));

        holder.producto.setText(nombre);
        holder.precio.setText(precio);
        holder.cantidad.setText(cantidad);
        holder.subtotal.setText(subtotal);

        return convertView;
    }

    static class ViewHolder{
        TextView producto;
        TextView subtotal;
        TextView cantidad;
        TextView precio;
    }
}
