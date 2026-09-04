package com.itsp.android.innovacion2016;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditarClienteActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edt_nombre, edt_apellidos, edt_direccion, edt_telefono, edt_correo;
    private Button btnaceptar;
    private int idcliente;
    private DBSistema dbSistema;
    private Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);

        try{
            idcliente = getIntent().getIntExtra("idcliente", 0);
        }catch (Exception e){
            idcliente = 0;
        }

        dbSistema = new DBSistema(this);
        try {
            dbSistema.setUpDatabase();
        } catch (Exception e) {
            Log.e("Tablas ", e.getMessage());
        }

        edt_nombre = (EditText) findViewById(R.id.edt_nombre);
        edt_apellidos = (EditText) findViewById(R.id.edt_apellidos);
        edt_direccion = (EditText) findViewById(R.id.edt_direccion);//null
        edt_telefono = (EditText) findViewById(R.id.edt_telefono);//null
        edt_correo = (EditText) findViewById(R.id.edt_correo);//null
        btnaceptar = (Button) findViewById(R.id.btnguardarcliente);
        btnaceptar.setOnClickListener(this);

        cliente = dbSistema.selectCliente(idcliente);

        edt_nombre.setText(cliente.getNombre());
        edt_apellidos.setText(cliente.getApellidos());
        edt_direccion.setText(cliente.getDireccion());
        edt_telefono.setText(cliente.getTelefono());
        edt_correo.setText(cliente.getCorreo());
    }

    @Override
    public void onClick(View v) {
        guardar();
    }

    public void guardar() {
        String nombre, apellidos, direccion, telefono, correo;
        nombre = edt_nombre.getText().toString().trim();
        apellidos = edt_apellidos.getText().toString().trim();
        direccion = edt_direccion.getText().toString().trim();
        telefono = edt_telefono.getText().toString().trim();
        correo = edt_correo.getText().toString().trim();

        if (!nombre.isEmpty() && !apellidos.isEmpty()) {
            cliente.setNombre(nombre);
            cliente.setApellidos(apellidos);
            if (!direccion.isEmpty()){
                cliente.setDireccion(direccion);
            } else {
                cliente.setDireccion(null);
            }
            if (!telefono.isEmpty()){
                cliente.setTelefono(telefono);
            } else {
                cliente.setTelefono(null);
            }
            if (!correo.isEmpty()){
                cliente.setCorreo(correo);
            } else {
                cliente.setCorreo(null);
            }

            int result = dbSistema.updateCliente(idcliente, cliente);

            if (result != -1){
                btnaceptar.setEnabled(false);
                EditarClienteActivity.this.finish();
                Toast.makeText(EditarClienteActivity.this, "Datos guardados corectamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditarClienteActivity.this, "¡Error al guardar los datos!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditarClienteActivity.this, "¡Rellene los campos requeridos!\n(Nombre, Apellidos)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void showDialog() {
        Dialog dialogo;

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        EditarClienteActivity.this.finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        Builder builder = new Builder(this);
        builder = builder.setIcon(R.mipmap.ic_launcher);
        builder = builder.setTitle(getResources().getString(R.string.str_salir));
        builder = builder.setPositiveButton("Aceptar", listener);
        builder = builder.setNegativeButton("Cancelar", listener);
        dialogo = builder.create();

        dialogo.show();
    }
}
