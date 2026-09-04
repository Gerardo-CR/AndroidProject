package com.itsp.android.innovacion2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.itsp.android.innovacion2016.ProductosVendidos;
import com.itsp.android.innovacion2016.R;
import com.itsp.android.innovacion2016.Venta;

import java.util.ArrayList;
import java.util.HashMap;


public class AdapterVentasDiarias extends ArrayAdapter<Venta> {

    ArrayList<Venta> items;
    Context context;

    public AdapterVentasDiarias(Context context,ArrayList<Venta> items) {
        super(context, R.layout.item_producto, items);
        this.context=context;
        this.items=items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView==null){

            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.item_ventas_diarias,parent,false);

            holder=new ViewHolder();

            holder.fecha=(TextView)convertView.findViewById(R.id.fechav);
            holder.total=(TextView)convertView.findViewById(R.id.totalv);


            convertView.setTag(holder);

        }else{

            holder=(ViewHolder)convertView.getTag();


        }

        holder.fecha.setText(items.get(position).getFecha());
        holder.total.setText(String.valueOf(items.get(position).getTotal()));

        return convertView;


    }


    static class ViewHolder{

        TextView fecha;
        TextView total;


    }
}
