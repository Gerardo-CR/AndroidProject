package com.gcr.android.inventorytest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DetalleVentaActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> detalle;
    private DBSistema database;
    private TextView textDetalleFecha, textDetalleHora, textDetallePago, textDetalleTotal;
    private TextView prueba;
    private ListView listaDetalle;
    private AdapterDetalleVenta adapter;
    private String html;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_venta);

        detalle= new ArrayList<>();
        database = new DBSistema(this);
        try {
            database.setUpDatabase();
        }catch (Exception e){
            Log.e("Tablas",e.getMessage());
        }

        int idVenta = getIntent().getIntExtra("idVenta", 1);
        textDetalleFecha = (TextView)findViewById(R.id.textDetalleFecha);
        textDetalleHora = (TextView)findViewById(R.id.textDetalleHora);
        textDetallePago = (TextView)findViewById(R.id.textDetallePago);
        textDetalleTotal = (TextView)findViewById(R.id.textDetalleTotal);
        prueba = (TextView)findViewById(R.id.Prueba);
        listaDetalle = (ListView)findViewById(R.id.listDetalleVenta);

        detalle = database.selectDetalleVenta(idVenta);
        String fecha = String.valueOf(detalle.get(0).get("fecha"));
        String hora = String.valueOf(detalle.get(0).get("hora"));
        String total = String.valueOf(detalle.get(0).get("total"));
        String pago = String.valueOf(detalle.get(0).get("pago"));
        textDetalleFecha.setText(fecha);
        textDetalleHora.setText(hora);
        textDetalleTotal.setText(total);
        textDetallePago.setText(pago);

        adapter = new AdapterDetalleVenta(DetalleVentaActivity.this, detalle);
        listaDetalle.setAdapter(adapter);
        /*
        prueba.setText("");
        for (HashMap<String, Object> producto:  detalle) {
            String nombre = String.valueOf(producto.get("nombre"));
            String precio = String.valueOf(producto.get("precio"));
            String cantidad = String.valueOf(producto.get("cantidad"));

            //Log.d("Producto",String.valueOf(producto.get("nombre")));
            prueba.append(nombre+" "+precio+" "+cantidad+"\n");
        }
        prueba.append(String.valueOf(detalle.size()));
        */

        html = "<center><h1>"+ getResources().getString(R.string.app_name) + "</h1>\n" +
                "<h2>Detalle de Venta</h2>\n" +
                "<b>Fecha: </b>" + fecha + "<span style=\"padding-left:4em\"><b>Hora: </b>" + hora + "</span><br>\n" +
                "<b>Total: </b>" + total + "<span style=\"padding-left:4em\"><b>Pago: </b>" + total + "</span><br><br><br>\n" +
                "<table border=2>\n" +
                "<tr>\n" +
                "<td><b>Producto</b></td>\n" +
                "<td><b>Precio</b></td>\n" +
                "<td><b>Cantidad</b></td>\n" +
                "<td><b>Subtotal</b></td>\n" +
                "</tr>\n";
        //prueba.setText("");
        for (HashMap<String, Object> producto:  detalle) {
            String nombre = String.valueOf(producto.get("nombre"));
            String precio = String.valueOf(producto.get("precio"));
            String cantidad = String.valueOf(producto.get("cantidad"));
            String subtotal = String.valueOf(Double.parseDouble(precio)*Double.parseDouble(cantidad));

            String linea = "<tr>\n" +
                    "<td>"+nombre+"</td>\n" +
                    "<td>"+precio+"</td>\n" +
                    "<td>"+cantidad+"</td>\n" +
                    "<td>"+subtotal+"</td>\n" +
                    "</tr>\n";
            html += linea;
        }
        html += "</table></center>";
        //prueba.setText(html);
        //Log.d("HTML", html);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_venta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_send_email_venta) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String email = preferences.getString("mail_to", "");
            SendEmail sm = new SendEmail(DetalleVentaActivity.this, email, "Detalle de Venta", html);
            sm.execute();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}