package com.itsp.android.innovacion2016;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
