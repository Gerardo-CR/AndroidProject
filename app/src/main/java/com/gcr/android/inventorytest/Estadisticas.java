package com.gcr.android.inventorytest;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.HashMap;


public class Estadisticas extends AppCompatActivity {
    
    private  TabHost tabHost;
    ExpandableListView expandable;
    private AdapterVentasDiarias adapterentasDiarias;
    ListView lista;
    DBSistema database;
    private HashMap<String,ArrayList<ProductosVendidos>> ProductosChild;
   // private HashMap<String,ArrayList<Venta>> to;
    ArrayList<ProductosVendidos> vendidos;
    ArrayList<String>listafecha;
    ArrayList<String> fecha2;
    ArrayList<String>lisfecha;
    AdapterProductosVendidos adapter;
    //AdapterVentasDiarias adapterv;
    //AdapterVentasDiarias VentasD;
    ArrayList<Venta> ventas;
    ArrayList<String> producto;
    int cantidad=0;
    Double total=0.0;
    Venta vv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estadisticas);

        Resources res=getResources();
       tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec= tabHost.newTabSpec("uno");
        spec.setIndicator("Productos vendidos", res.getDrawable(R.drawable.ic_menu_gallery));
        spec.setContent(R.id.linearLayout);
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("dos");
        spec.setContent(R.id.linearLayout2);
        spec.setIndicator("Ventas diarias", res.getDrawable(R.drawable.ic_menu_manage));
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);

        ///////////////////////////////////////////////////////////////////////////////////////////
        database=new DBSistema(this);
        ProductosChild=new HashMap<>();
        listafecha=new ArrayList<>();
        lisfecha=new ArrayList<>();
        fecha2=new ArrayList<>();
        expandable=(ExpandableListView)findViewById(R.id.ExpandableProductosVendidos);
        lista=(ListView)findViewById(R.id.listViewVentasD);

        producto=new ArrayList<>();
        ventas= new ArrayList<>();

        try {
            database.setUpDatabase(this);
        } catch(Exception e) {
            Log.e("Tablas",e.getMessage());
        }


        vendidos = database.selectVendidos();

        for(ProductosVendidos pv : vendidos){

            String fcha = pv.getFecha();
            if(!listafecha.contains(fcha)) {
                listafecha.add(fcha);
            }
        }

        for (String f: listafecha) {
            ArrayList<ProductosVendidos> aux = new ArrayList<>();
            for (ProductosVendidos v: vendidos ) {
                if(f.equals(v.getFecha()))  {
                    aux.add(v);
                }
            }

            ArrayList<ProductosVendidos> producV = new ArrayList<>();
            ArrayList<ProductosVendidos> productosV = new ArrayList<>();

            for (ProductosVendidos prov :aux ) {

                String nombre=prov.getNombre();

                if(!producto.contains(nombre))

                {
                    producto.add(nombre);
                    producV.add(prov);

                }
            }

            for(ProductosVendidos pro:producV){

                for(ProductosVendidos pp:aux){

                    if(pro.getNombre().equals(pp.getNombre())){

cantidad=cantidad+pp.getCantidad();

                    }


                }

pro.setCantidad(cantidad);
                cantidad=0;
                productosV.add(pro);
            }






            ProductosChild.put(f, productosV);
            producto.clear();
        }

      adapter=new AdapterProductosVendidos(Estadisticas.this,listafecha,ProductosChild);
        expandable.setAdapter(adapter);

        ///inicio de ventas diarias

        ventas = database.selectVentasContado();

        for(Venta v : ventas){
            String fcha = v.getFecha();
            if(!fecha2.contains(fcha)) {
                fecha2.add(fcha);
            }
        }



        ArrayList<Double> t=new ArrayList<>();

        for(String f:fecha2){
             total=0.0;
            for(Venta v:ventas){

                if(f.equals(v.getFecha())){

                    total=total + v.getTotal();


                }


            }

            System.out.println("#######################.... "+total);
            t.add(total);
        }


        ArrayList<Venta> ven=new ArrayList<>();
        ArrayList f=new ArrayList();
        for (Venta v:ventas){

            String fecha=v.getFecha();

            if(!f.contains(fecha)){
                f.add(fecha);
                v.setTotal(t.remove(0));
                ven.add(v);

            }

        }



        adapterentasDiarias=new AdapterVentasDiarias(this,ven);
        lista.setAdapter(adapterentasDiarias);

        //aqui va el adapter

    }
}
