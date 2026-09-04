package com.itsp.android.innovacion2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterVentas extends BaseExpandableListAdapter {
    private ArrayList<String> itemsHeader;
    private HashMap<String, ArrayList<Venta>> itemsChild;
    private Context context;

    public AdapterVentas(Context context, ArrayList<String> itemsHeader, HashMap<String, ArrayList<Venta>> itemsChild){
        this.context = context;
        this.itemsHeader = itemsHeader;
        this.itemsChild = itemsChild;
    }

    @Override
    public int getGroupCount() {
        return itemsHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemsChild.get(itemsHeader.get(groupPosition)).size();
    }

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
        ViewHolderGroup holderGroup;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_venta_header, parent, false);

            holderGroup= new ViewHolderGroup();
            holderGroup.fecha = (TextView)convertView.findViewById(R.id.itmTextFechaHeaderVenta);
            convertView.setTag(holderGroup);
        }else{
            holderGroup = (ViewHolderGroup) convertView.getTag();
        }

        holderGroup.fecha.setText((String)getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild holderChild;
        Venta venta= (Venta) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_venta_child, parent, false);

            holderChild= new ViewHolderChild();
            holderChild.fecha = (TextView)convertView.findViewById(R.id.itmTextFechaChildVenta);
            holderChild.hora = (TextView)convertView.findViewById(R.id.itmTextHoraChildVenta);
            holderChild.id = (TextView)convertView.findViewById(R.id.itmTextIDChildVenta);
            holderChild.total = (TextView)convertView.findViewById(R.id.itmTextTotalChildVenta);
            holderChild.pago = (TextView)convertView.findViewById(R.id.itmTextPagoChildVenta);
            convertView.setTag(holderChild);
        }else{
            holderChild = (ViewHolderChild)convertView.getTag();
        }
        holderChild.fecha.setText(venta.getFecha());
        holderChild.hora.setText(venta.getHora());
        holderChild.id.setText(String.valueOf(venta.getId()));
        holderChild.total.setText(String.valueOf(venta.getTotal()));
        holderChild.pago.setText(String.valueOf(venta.getPago()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolderChild{
        TextView fecha;
        TextView hora;
        TextView id;
        TextView total;
        TextView pago;
    }
    static class ViewHolderGroup{
        TextView fecha;
    }
}
