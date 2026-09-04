package com.itsp.android.innovacion2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;


public class Autenticar extends AppCompatActivity {

    ControlLogin ctlLogin;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logeo);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun){
            //show start activity
            startActivity(new Intent(Autenticar.this, Registro.class));
            Autenticar.this.finish();
        }

        ctlLogin = (ControlLogin) findViewById(R.id.CtlLogin);

        ctlLogin.setOnLoginListener(new ControlLogin.OnLoginListener() {
            @Override
            public void onLogin(String usuario, String password) {
                //Validamos el usuario y la contraseña

                if (usuario.equals("root") && password.equals("root")) {
                    Intent intent = new Intent(Autenticar.this, MainActivity.class);
                    startActivity(intent);

                    ctlLogin.setTxtPassword("");
                    ctlLogin.setTxtUsuario("");
                    Autenticar.this.finish();
                } else {
                    ctlLogin.setMensaje("Vuelva a intentarlo.");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == event.KEYCODE_BACK){

            Autenticar.this.finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
}
