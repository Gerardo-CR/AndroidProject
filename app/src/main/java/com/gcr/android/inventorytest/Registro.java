package com.gcr.android.inventorytest;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

public class Registro extends AppCompatActivity {
    Button aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        aceptar = findViewById(R.id.registrar_button);
        aceptar.setOnClickListener(v -> {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
            startActivity(new Intent(Registro.this, Autenticar.class));
            Registro.this.finish();
        });
    }

}
