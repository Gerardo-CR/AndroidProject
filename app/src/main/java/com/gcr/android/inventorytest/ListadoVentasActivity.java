package com.gcr.android.inventorytest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListadoVentasActivity extends AppCompatActivity {
    DBSistema database;
    ExpandableListView lista;
    AdapterVentas adapter;
    private HashMap<String, ArrayList<Venta>> childData;
    ArrayList<Venta> listVentas;
    ArrayList<String> listFechas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_ventas);

        childData = new HashMap<>();
        listFechas = new ArrayList<>();
        database = new DBSistema(this);
        try {
            database.setUpDatabase();
        }catch (Exception e){
            Log.e("Tablas",e.getMessage());
        }

        lista = (ExpandableListView)findViewById(R.id.expListVentas);
        listVentas = database.selectVentas();

        for (Venta v: listVentas) {
            String fecha = v.getFecha();
            if(!listFechas.contains(fecha))
                listFechas.add(fecha);
        }

        for (String f: listFechas) {
            ArrayList<Venta> aux = new ArrayList<>();
            for (Venta v: listVentas) {
                if(f.equals(v.getFecha()))
                    aux.add(v);
            }
            childData.put(f, aux);
        }
        adapter = new AdapterVentas(ListadoVentasActivity.this, listFechas, childData);
        lista.setAdapter(adapter);
        lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int idVenta = childData.get(listFechas.get(groupPosition)).get(childPosition).getId();
                Intent intent = new Intent(ListadoVentasActivity.this, DetalleVentaActivity.class);
                intent.putExtra("idVenta",idVenta);
                startActivity(intent);
                return false;
            }
        });
    }
}
