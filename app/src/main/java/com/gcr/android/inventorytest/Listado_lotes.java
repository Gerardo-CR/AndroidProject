package com.gcr.android.inventorytest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Listado_lotes extends AppCompatActivity {

    private Adapter_lote adapterLote;
    private ListView listalotes;
    private Producto producto;
    private ArrayList<Lote> lotes;
    private Button agregar, agregarfecha;
    private EditText cantidad;
    private TextView verf, num, nombrep;
    int dia, mes, ano;
    int id;


    DBSistema dbSistema;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_lote);

        dbSistema = new DBSistema(this);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");

        try {
            dbSistema.setUpDatabase(this);
        } catch (Exception e) {
            Log.e("Tablas", e.getMessage());
        }

        agregar = (Button) findViewById(R.id.buttonAgregarLote);
        listalotes=(ListView)findViewById(R.id.listViewVerlotes);

        lotes = dbSistema.SelectLote(id);
        adapterLote=new Adapter_lote(this,lotes);
        listalotes.setAdapter(adapterLote);



        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


    }
}
