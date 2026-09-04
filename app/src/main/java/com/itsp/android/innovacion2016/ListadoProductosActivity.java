package com.itsp.android.innovacion2016;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class ListadoProductosActivity extends AppCompatActivity {
    private ListView lista;
    private DBSistema database;
    private AdapterProductos adapter;
    private ArrayList<Producto> listaProductos;
    private TextView valor;
    //private int dia,mes,ano;
   // private TextView verf;


    private Producto producto;
    private ArrayList<Lote> lotes;
    private Button agregar, agregarfecha,detalles;
    private EditText cantidad;
    private TextView verf, num, nombrep;
    int dia, mes, ano;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_productos);
android.support.v7.app.ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        lista = (ListView) findViewById(R.id.listProductos);
        database = new DBSistema(this);
        try {
            database.setUpDatabase(this);
        } catch (Exception e) {
            Log.e("Tablas", e.getMessage());
        }

        listaProductos = database.selectProductos();
        adapter = new AdapterProductos(this, listaProductos);
        lista.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        valor = (TextView) findViewById(R.id.tvalor);
        final float total;
        total = database.valor();
        if (total == 0) {

            valor.setText("");
        } else {
            valor.setText("Valor de inventario: $" + total);
        }

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                final Producto aux = listaProductos.get(position);
                new AlertDialog.Builder(ListadoProductosActivity.this)
                        .setTitle("¿Qué desea hacer?")


                        .setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(ListadoProductosActivity.this, ModificarProductoActivity.class);
                                intent.putExtra("nombre", aux.getNombre());
                                intent.putExtra("id", aux.getId());
                                intent.putExtra("barcode", aux.getBarcode());
                                intent.putExtra("precio", aux.getPrecio());
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int result = database.deleteProducto(aux.getId());
                                if (result > 0) {
                                    Toast.makeText(ListadoProductosActivity.this, "Producto eliminado con exito", Toast.LENGTH_SHORT).show();
                                    listaProductos.remove(position);
                                    adapter.notifyDataSetChanged();
                                    float total = database.valor();
                                    if (total == 0) {

                                        valor.setText("");
                                    } else {
                                        valor.setText("Valor de inventario: $" + total);
                                    }

                                } else {
                                    Toast.makeText(ListadoProductosActivity.this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })



                        .setNeutralButton("Añadir", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                LayoutInflater inflater = LayoutInflater.from(ListadoProductosActivity.this);
                                View layout = inflater.inflate(R.layout.dialog_lote, null);


                                agregarfecha = (Button) layout.findViewById(R.id.buttonAF);
                                verf = (TextView) layout.findViewById(R.id.textViewVerFecha);
                                num = (TextView) layout.findViewById(R.id.textViewNumeroLote);
                                cantidad = (EditText) layout.findViewById(R.id.editTextCantidad);
                                nombrep = (TextView) layout.findViewById(R.id.textViewNombrePro);
                                detalles = (Button)layout.findViewById(R.id.ButtonDetalles);

                                int numero = 0;


                                numero = database.SelectNumeroLote(aux.getId());
                                num.setText(String.valueOf(numero + 1));


                                final CheckBox fecha = (CheckBox) layout.findViewById(R.id.checkBoxfechaC);
                                producto = database.selectProducto(aux.getId());
                                nombrep.setText(producto.getNombre());//nombre del producto


                                fecha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                        if (isChecked == false) {

                                            agregarfecha.setVisibility(View.INVISIBLE);
                                            verf.setVisibility(View.INVISIBLE);
                                            verf.setText("");

                                        } else {

                                            agregarfecha.setVisibility(View.VISIBLE);
                                            verf.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });


                                agregarfecha.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Calendar c = Calendar.getInstance();
                                        dia = c.get(Calendar.DAY_OF_MONTH);
                                        mes = c.get(Calendar.MONTH);
                                        ano = c.get(Calendar.YEAR);


                                        final DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoProductosActivity.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                                if ((monthOfYear + 1) < 10 && dayOfMonth < 10) {
                                                    verf.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);


                                                } else if ((monthOfYear + 1) < 10) {
                                                    verf.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                                                } else if (dayOfMonth < 10) {
                                                    verf.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                                                } else {

                                                    verf.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);


                                                }


                                            }
                                        }, ano, mes, dia);

                                        datePickerDialog.show();

                                    }
                                });

                                detalles.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        Intent intent = new Intent(ListadoProductosActivity.this, Listado_lotes.class);

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("id", aux.getId());
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }
                                });


                                final int finalNumero = numero;
                                new AlertDialog.Builder(ListadoProductosActivity.this)
                                        .setView(layout)
                                        .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String cant = cantidad.getText().toString();


                                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                String[] entrada = format.format(new Date()).split(" ");//fecha entrada

                                                if (!cant.equals("")) {
                                                    double cantid = Double.parseDouble(cant);//cantidad del lote

                                                    String caducidad;

                                                    caducidad = verf.getText().toString();

                                                    long respuesta = database.InsertarLote(finalNumero + 1, cantid, caducidad, entrada[0], aux.getId());

                                                    if (respuesta != -1) {


                                                        double stock = database.SelectStock(aux.getId());
                                                        stock = stock + cantid;
                                                        database.updatestock(aux.getId(), stock);

                                                        Toast.makeText(ListadoProductosActivity.this, "Exito al insertar", Toast.LENGTH_SHORT).show();
                                                        ListadoProductosActivity.this.finish();
                                                        Intent itent = new Intent(ListadoProductosActivity.this, ListadoProductosActivity.class);
                                                        startActivity(itent);
                                                        //verf.setText("");
                                                        //cantidad.setText("");


                                                    }

                                                } else
                                                    Toast.makeText(ListadoProductosActivity.this, "Dijite una cantidad", Toast.LENGTH_SHORT).show();
                                            }
                                        })

                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })


                                        .show();


                            }


                        })


                        .show();
            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_productos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_producto:
Intent intent= new Intent(ListadoProductosActivity.this,ProductCaptureActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






}
