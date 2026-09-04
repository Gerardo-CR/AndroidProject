package com.itsp.android.innovacion2016;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ControlLogin extends LinearLayout {

    EditText   txtUsuario;
    EditText  txtPassword;
    Button btnLogin;
    TextView lblMensaje;
    private OnLoginListener listener;

    public ControlLogin(Context context) {
        super(context);
        inicializar();
    }
    public ControlLogin(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    private void inicializar() {
        //Utilizamos el layout 'control_login' como interfaz del control
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.control_login, this, true);
        //Obtenemoslas referencias a los distintos control
        txtUsuario = (EditText)findViewById(R.id.TextUsuario);
        txtPassword = (EditText)findViewById(R.id.TextPassword);
        btnLogin = (Button)findViewById(R.id.btnAceptar);
        lblMensaje = (TextView)findViewById(R.id.Labelver);
        //Asociamos los eventos necesarios
        asignarEventos();
    }

    public void setMensaje(String msg)
    {
        lblMensaje.setText(msg);
    }
    public void setTxtPassword(String msg)
    {
        txtPassword.setText(msg);
    }
    public void setTxtUsuario(String msg)
    {
        txtUsuario.setText(msg);
    }

    public void setOnLoginListener(OnLoginListener l) {
        listener = l;
    }

    private void asignarEventos()
    {
        btnLogin.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                listener.onLogin(txtUsuario.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    public interface OnLoginListener {
        void onLogin(String usuario, String password);
    }
}
