package com.itsp.android.innovacion2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterProductos extends ArrayAdapter<Producto> {
    ArrayList<Producto> items;
    Context context;
    public AdapterProductos(Context context, ArrayList<Producto> items){
        super(context, R.layout.item_producto, items);
        this.context=context;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_producto, parent, false);

            holder= new ViewHolder();
            holder.barcode = (TextView)convertView.findViewById(R.id.itmTextBarcode);
            holder.producto = (TextView)convertView.findViewById(R.id.itmTextProducto);
            holder.precio = (TextView)convertView.findViewById(R.id.itmTextPrecio);
            holder.id = (TextView)convertView.findViewById(R.id.itmTextIDProducto);
            holder.cantidad=(TextView)convertView.findViewById(R.id.itmTextCantidad);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.producto.setText(items.get(position).getNombre());
        holder.precio.setText(String.valueOf(items.get(position).getPrecio()));
        holder.barcode.setText(items.get(position).getBarcode());
        holder.id.setText(String.valueOf(items.get(position).getId()));
        holder.cantidad.setText(String.valueOf(items.get(position).getStock())+" "+items.get(position).getAliasMedida());

        return convertView;
    }

    static class ViewHolder{
        TextView producto;
        TextView barcode;
        TextView id;
        TextView precio;
        TextView cantidad;
    }
}
