package com.itsp.android.innovacion2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class AdapterProductosVendidos extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> itemsHeader;
    private HashMap<String,ArrayList<ProductosVendidos>> itemsChild;

    public AdapterProductosVendidos(Context context,ArrayList<String> itemsHeader,HashMap<String,ArrayList<ProductosVendidos>> itemsChild ){

        this.context=context;
        this.itemsHeader=itemsHeader;
        this.itemsChild=itemsChild;
    }




    @Override
    public int getGroupCount() {
        return itemsHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemsChild.get(itemsHeader.get(groupPosition)).size();}

    @Override
    public Object getGroup(int groupPosition) {
        return itemsHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemsChild.get(itemsHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

     ViewHolderGroup HolderGroup;

        if(convertView==null){

            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.item_venta_header,parent,false);

            HolderGroup = new ViewHolderGroup();
            HolderGroup.fecha=(TextView)convertView.findViewById(R.id.itmTextFechaHeaderVenta);

            convertView.setTag(HolderGroup);

        }else{

            HolderGroup=(ViewHolderGroup)convertView.getTag();

        }
        HolderGroup.fecha.setText((String)getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderChild holderChild;
        ProductosVendidos producto=(ProductosVendidos)getChild(groupPosition,childPosition);
        if(convertView==null){

            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.item_productos_vendidos,parent,false);

            holderChild=new ViewHolderChild();
            holderChild.producto=(TextView)convertView.findViewById(R.id.Tproducto);
            holderChild.cantidad=(TextView)convertView.findViewById(R.id.Tcantidad);

            convertView.setTag(holderChild);


        }
        else{

            holderChild=(ViewHolderChild)convertView.getTag();
        }

        holderChild.producto.setText((String)producto.getNombre());
        holderChild.cantidad.setText(String.valueOf(producto.getCantidad()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {return true;}

    static class ViewHolderGroup{

        TextView fecha;
    }


    static class ViewHolderChild{

        TextView producto;
        TextView cantidad;


    }
}
