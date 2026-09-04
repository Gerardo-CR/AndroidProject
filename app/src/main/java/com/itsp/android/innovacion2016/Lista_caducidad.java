package com.itsp.android.innovacion2016;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class Lista_caducidad extends AppCompatActivity {
    DBSistema dbSistema;
    Adapter_lista_caducidad caducidad;
    ArrayList<Prolote> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caducidad);
        ListView cadu= (ListView)findViewById(R.id.listView_caducidad);

        dbSistema=new DBSistema(this);
        try {
            dbSistema.setUpDatabase();
        } catch (Exception e) {
            Log.e("Tablas", e.getMessage());
        }

        lista = dbSistema.prolot();
        caducidad=new Adapter_lista_caducidad(Lista_caducidad.this,lista);
        cadu.setAdapter(caducidad);
    }
}
