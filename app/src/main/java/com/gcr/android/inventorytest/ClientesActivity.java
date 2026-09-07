package com.gcr.android.inventorytest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ClientesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DBSistema dbSistema;
    private ListView listView;
    private AdapterClientesVenta clientesVenta;
    private ArrayList<VentaCliente> clientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        dbSistema = new DBSistema(this);
        try{
            dbSistema.setUpDatabase();
        }catch (Exception e){
            Log.e("Tablas", e.getMessage());
        }

        listView = (ListView) findViewById(R.id.lista);
        clientes = dbSistema.ventas_por_cliente();
        clientesVenta = new AdapterClientesVenta(this, clientes);
        listView.setAdapter(clientesVenta);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);

        clientes.contains("hola");

        //startService(new Intent(ClientesActivity.this, Servicio_Ventas_Credito.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        int viewId = v.getId();
        if (viewId == R.id.lista) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(((VentaCliente) listView.getAdapter().getItem(info.position)).getNombreCliente());
            inflater.inflate(R.menu.menu_ventasclientes, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int idcliente = ((VentaCliente) listView.getAdapter().getItem(info.position)).getIdCliente();

        int itemId = item.getItemId();
        if (itemId == R.id.nav_editarcliente) {
            Intent intent1 = new Intent(ClientesActivity.this, EditarClienteActivity.class);
            intent1.putExtra("idcliente", idcliente);
            startActivity(intent1);
            return true;
        } else if (itemId == R.id.nav_vercliente) {
            Intent intent = new Intent(ClientesActivity.this, DetalleClienteActivity.class);
            intent.putExtra("idcliente", idcliente);
            startActivity(intent);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ClientesActivity.this, DetalleVentaCreditoActivity.class);
        intent.putExtra("idventa", ((VentaCliente) parent.getItemAtPosition(position)).getId());
        intent.putExtra("idcliente", ( (VentaCliente) parent.getItemAtPosition(position)).getIdCliente());
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clientes = dbSistema.ventas_por_cliente();
        clientesVenta = new AdapterClientesVenta(this, clientes);
        listView.setAdapter(clientesVenta);
    }
}
