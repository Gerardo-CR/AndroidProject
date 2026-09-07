package com.gcr.android.inventorytest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gcr.android.inventorytest.Servicios.Servicio_Ventas_Credito;

public class DetalleClienteActivity extends AppCompatActivity implements View.OnClickListener {

    private Cliente cliente;
    private DBSistema dbSistema;
    private ImageButton llamar, enviaramail;
    private TextView nombre, direccion, email, telefono;

    private Intent intents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cliente);
        intents = new Intent(this, Servicio_Ventas_Credito.class);
        int idcliente;

        try {
            idcliente = getIntent().getIntExtra("idcliente", 0);
        } catch (Exception e) {
            idcliente = 0;
        }

        nombre = (TextView) findViewById(R.id.tv_nombrecliente);
        direccion = (TextView) findViewById(R.id.tv_direccion);
        email = (TextView) findViewById(R.id.tv_email);
        telefono = (TextView) findViewById(R.id.tv_telefono);
        llamar = (ImageButton) findViewById(R.id.ibtn_llamar);
        enviaramail = (ImageButton) findViewById(R.id.ibtn_email);
        llamar.setOnClickListener(this);
        enviaramail.setOnClickListener(this);

        dbSistema = new DBSistema(this);
        try {
            dbSistema.setUpDatabase();
        } catch (Exception e) {
            Log.e("Tablas", e.getMessage());
        }

        cliente = dbSistema.selectCliente(idcliente);
        llamar.setEnabled(cliente.getTelefono() != null);
        enviaramail.setEnabled(cliente.getCorreo() != null);

        nombre.setText(cliente.toString());
        if (cliente.getDireccion() != null)
            direccion.setText(cliente.getDireccion());
        else
            direccion.setText("...");
        if (cliente.getCorreo() != null)
            email.setText(cliente.getCorreo());
        else
            email.setText("...");
        if (cliente.getTelefono() != null)
            telefono.setText(cliente.getTelefono());
        else
            telefono.setText("...");
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.ibtn_llamar) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cliente.getTelefono()));
            startActivity(intent);
            //startService(intents);
        } else if (id == R.id.ibtn_email) {
            Intent intent1 = new Intent(Intent.ACTION_SEND);
            intent1.setType("message/rfc822");
            intent1.putExtra(Intent.EXTRA_EMAIL, new String[]{cliente.getCorreo()});
            startActivity(intent1);
            //3stopService(intents);
        }
    }
}
