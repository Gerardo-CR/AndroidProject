package com.gcr.android.inventorytest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.gcr.android.inventorytest.Servicios.Recibir_Fecha_Caducidad;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int intervalo=(1000*60)*10;


    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //NavigationDrawer********************************
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Termina NaivgationDrawer*************************



        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent itent= new Intent(getApplicationContext(),Recibir_Fecha_Caducidad.class);

        PendingIntent pending = PendingIntent.getBroadcast(this,Recibir_Fecha_Caducidad.codigo, itent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervalo, pending);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.exit)
                    .setPositiveButton(R.string.close_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.close_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_selling) {
            Intent intent = new Intent(MainActivity.this, PuntoVentaActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_groceries) {
            Intent intent = new Intent(MainActivity.this, ListadoProductosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_capture) {
            Intent intent = new Intent(MainActivity.this, ProductCaptureActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sales) {
            Intent intent = new Intent(MainActivity.this, ListadoVentasActivity.class);
            startActivity(intent);
        } else if (id==R.id.nav_statistics){
            Intent intent=new Intent(MainActivity.this,Estadisticas.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_fcm) {
            Intent intent = new Intent(MainActivity.this, FCMActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_caducidad){
            Intent intent = new Intent(MainActivity.this, Lista_caducidad.class);
            startActivity(intent);
        }else if(id==R.id.nav_fact){
            Intent intent= new Intent(MainActivity.this,NavegadorActivity.class);
            intent.putExtra("url", "https://www.siat.sat.gob.mx/PTSC/");
            startActivity(intent);
        }else if (id == R.id.nav_clientes){
            Intent intent = new Intent(MainActivity.this, ClientesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}