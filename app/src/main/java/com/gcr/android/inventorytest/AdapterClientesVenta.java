package com.gcr.android.inventorytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import static com.gcr.android.inventorytest.Venta.DEF;



public  class AdapterClientesVenta extends ArrayAdapter<VentaCliente> {

    private ArrayList<VentaCliente> ventaClientes;
    private Context context;

    public AdapterClientesVenta(Context context, ArrayList<VentaCliente> ventaClientes) {
        super(context, R.layout.item_cliente_venta, ventaClientes);
        this.ventaClientes = ventaClientes;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater= LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.item_cliente_venta, parent, false);

            holder = new ViewHolder();

            holder.fecha = (TextView) convertView.findViewById(R.id.txtfecha);
            holder.cliente = (TextView) convertView.findViewById(R.id.txtcliente);
            holder.total = (TextView) convertView.findViewById(R.id.txttotal);
            holder.deuda = (TextView) convertView.findViewById(R.id.txtdeuda);
            holder.pago = (TextView) convertView.findViewById(R.id.txtpago);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fecha.setText(ventaClientes.get(position).getFecha() + ' ' + ventaClientes.get(position).getHora());
        holder.cliente.setText(ventaClientes.get(position).getNombreCliente());
        holder.total.setText(DEF.format(ventaClientes.get(position).getTotal()));
        holder.deuda.setText(DEF.format(ventaClientes.get(position).getdeuda()));
        holder.pago.setText(DEF.format(ventaClientes.get(position).getPago()));

        return convertView;
    }

    private class ViewHolder {
        TextView fecha;
        TextView cliente;
        TextView total;
        TextView deuda;
        TextView pago;
    }
}
