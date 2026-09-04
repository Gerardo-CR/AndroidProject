package com.itsp.android.innovacion2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itsp.android.innovacion2016.Lote;
import com.itsp.android.innovacion2016.R;

import java.util.ArrayList;


public class Adapter_lote extends ArrayAdapter<Lote> {
    ArrayList<Lote> items;
    Context context;

    public Adapter_lote(Context context, ArrayList<Lote> items) {
        super(context, R.layout.items_lote,items);
        this.context=context;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder holder;
        if(convertView==null){

            LayoutInflater inflater= LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.items_lote,parent,false);

            holder=new ViewHolder();

            holder.idlote=(TextView)convertView.findViewById(R.id.textViewidlote);
            holder.numero=(TextView)convertView.findViewById(R.id.textViewitem_numero);
            holder.entrada=(TextView)convertView.findViewById(R.id.textViewitems_fecha_entrada);
            holder.cantidad=(TextView)convertView.findViewById(R.id.textViewcantidad_lote);

            convertView.setTag(holder);


        }else{

            holder=(ViewHolder)convertView.getTag();

        }

        holder.idlote.setText(String.valueOf(items.get(position).getIdlote()));
        holder.numero.setText(String.valueOf(items.get(position).getNumero()));
        holder.entrada.setText(String.valueOf(items.get(position).getEntrada()));
        holder.cantidad.setText(String.valueOf(items.get(position).getCantidadlote()));




        return convertView;
    }

    static class ViewHolder{

        TextView idlote;
        TextView numero;
        TextView entrada;
        TextView cantidad;


    }


}
