package com.gcr.android.inventorytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gerardo Castillo on 22/05/2017.
 */
public class Adapter_lista_caducidad extends ArrayAdapter<Prolote> {

    ArrayList<Prolote> items;
    Context context;

    public Adapter_lista_caducidad(Context context, ArrayList<Prolote> items) {
        super(context, R.layout.item_caducidad,items);
        this.items=items;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){

            LayoutInflater inflater= LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.item_caducidad,parent,false);

            holder=new ViewHolder();

            holder.nombre=(TextView) convertView.findViewById(R.id.nombre_caducidad);
            holder.nombre.setSelected(true);
            holder.numero=(TextView)convertView.findViewById(R.id.numero_caducidad);
            holder.restantes=(TextView)convertView.findViewById(R.id.dias_caducidad);
            holder.id=(TextView)convertView.findViewById(R.id.caducidad_id);

            convertView.setTag(holder);


        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.numero.setText(String.valueOf(items.get(position).getNumero()));
        holder.nombre.setText(String.valueOf(items.get(position).getNombre()));
        holder.restantes.setText(String.valueOf(items.get(position).getDiasres()));
        holder.id.setText(String.valueOf(items.get(position).getId_pro()));

        return convertView;
    }

    static class ViewHolder{
        TextView numero;
        TextView nombre;
        TextView restantes;
        TextView id;
    }
}
