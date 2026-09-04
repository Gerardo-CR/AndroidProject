package com.itsp.android.innovacion2016;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.itsp.android.innovacion2016.Venta.DEF;
import static com.itsp.android.innovacion2016.Venta.VENTA_CONTADO;



public class DetalleVentaCreditoActivity extends AppCompatActivity implements View.OnClickListener {

    private int idventa, idcliente;
    private DBSistema dbSistema;
    private Venta venta;
    private Cliente cliente;

    private TextView tvfecha, tvfecha2, tvdiasr, tvcliente, tvdireccion, tvtotal, tvdeuda, tvpago;
    private LinearLayout tabla;
    private ImageButton llamar, email;
    private Button agregarpago, finalizarventa;
    private Dialog dlgpago, dlg_eliminarpago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ventacredito);

        try {
            idventa = getIntent().getIntExtra("idventa", 1);
        } catch (Exception e) {
            idventa = 1;
        }

        try {
            idcliente = getIntent().getIntExtra("idcliente", 1);
        } catch (Exception e) {
            idcliente = 1;
        }
        dbSistema = new DBSistema(this);
        try {
            dbSistema.setUpDatabase();
        } catch (Exception er) {
            Toast.makeText(this, "Error:\n" + er, Toast.LENGTH_SHORT).show();
        }

        venta = dbSistema.selectVentaCredito(idventa);
        cliente = dbSistema.selectCliente(idcliente);
        ArrayList<ProductosVendidos> productos = dbSistema.selectVendidos(idventa);

        tvfecha = (TextView) findViewById(R.id.txtfecha);
        tvfecha2 = (TextView) findViewById(R.id.txtfechaplazo);
        tvdiasr = (TextView) findViewById(R.id.txtdias);
        tvcliente = (TextView) findViewById(R.id.txtcliente);
        tvdireccion = (TextView) findViewById(R.id.txtdireccion);
        tvtotal = (TextView) findViewById(R.id.txttotal);
        tvdeuda = (TextView) findViewById(R.id.txtdeuda);
        tvpago = (TextView) findViewById(R.id.txtpago);

        llamar = (ImageButton) findViewById(R.id.btnllamar);
        email = (ImageButton) findViewById(R.id.btnenviaremail);
        agregarpago = (Button) findViewById(R.id.btnagregar);
        finalizarventa = (Button) findViewById(R.id.btnfinalizar);

        llamar.setOnClickListener(this);
        email.setOnClickListener(this);
        agregarpago.setOnClickListener(this);
        finalizarventa.setOnClickListener(this);

        tvfecha.setText(venta.getFecha() + ' ' + venta.getHora());
        tvfecha2.setText(venta.getPlazo());
        tvdiasr.setText(getString(R.string.str_diasrestantes) + ' ' + String.valueOf(dbSistema.dias_res(venta.getPlazo())));
        tvcliente.setText(cliente.toString());
        tvdireccion.setText((cliente.getDireccion() != null) ? cliente.getDireccion() : "...");
        tvtotal.setText(DEF.format(venta.getTotal()));
        tvdeuda.setText(DEF.format(venta.getdeuda()));
        tvpago.setText(DEF.format(venta.getPago()));
        tabla = (LinearLayout) findViewById(R.id.lista_productos);

        if (cliente.getTelefono() == null)
            llamar.setVisibility(View.INVISIBLE);

        if (cliente.getCorreo() == null)
            email.setVisibility(View.INVISIBLE);

        for (ProductosVendidos pv : productos) {
            FilaProducto aux = new FilaProducto(this);
            aux.setDatos(pv);
            tabla.addView(aux);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ventacredito, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_eliminarpago:
                dlg_eliminarpago = eliminarPago();
                dlg_eliminarpago.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnllamar:
                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cliente.getTelefono()));
                startActivity(intent1);
                break;

            case R.id.btnenviaremail:
                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("message/rfc822");
                intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{cliente.getCorreo()});
                startActivity(intent2);
                break;

            case R.id.btnagregar:
                dlgpago = agregarPago();
                dlgpago.show();
                break;

            case R.id.btnfinalizar:
                showDialogfinalizar();
                break;
        }
    }

    private Dialog agregarPago() {

        final double[] pago = new double[1];
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_add_pago, null);
        final EditText edtpago = (EditText) layout.findViewById(R.id.edt_pago);
        final Button btnaceptar = (Button) layout.findViewById(R.id.btnaceptarpago);

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strpago = edtpago.getText().toString().trim();
                if (!strpago.isEmpty()) {//verificar que el pago no este vacio
                    pago[0] = Double.parseDouble(strpago);
                    if (!(pago[0] > venta.getdeuda()) && !(pago[0] == 0)) {//Verificar que el pago ingresado no sea mayor a la deuda, ni igual a cero
                        if (pago[0] < venta.getdeuda()) {//Si el pago es menor a la deuda, actulizar el pago de la venta
                            venta.setPago(venta.getPago() + pago[0]);//actualizar el pago en la clase pago
                            boolean res = dbSistema.updateVentapago(idventa, venta.getPago());//actualizar el pago en la base de datos
                            if (res) {
                                Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_pagoactulizado), Toast.LENGTH_SHORT).show();
                                tvpago.setText(DEF.format(venta.getPago()));//actualizar el pago en la vista
                                tvdeuda.setText(DEF.format(venta.getdeuda()));//actualizar la deuda en la vista
                                dlgpago.dismiss();
                            } else {
                                Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_erroractulizarpago), Toast.LENGTH_SHORT).show();
                            }
                        } else if (pago[0] == venta.getdeuda()) {//Si el pago es igual a la deuda, actualizar la venta completa y finalizar venta credito
                            venta.setPago(venta.getPago() + pago[0]);//actualizar el pago en la clase pago
                            venta.setEstatus(VENTA_CONTADO);//canbiar la venta a Venta Contado
                            boolean res = dbSistema.updateVentaCredito(idventa, venta);//actulizar la venta en la base de datos
                            if (res) {
                                Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_ventaactualizada), Toast.LENGTH_SHORT).show();
                                tvpago.setText(DEF.format(venta.getPago()));//actualizar el pago en la vista
                                tvdeuda.setText(DEF.format(venta.getdeuda()));//actualizar la deuda en la vista
                                dlgpago.dismiss();
                                agregarpago.setEnabled(false);
                                finalizarventa.setEnabled(false);
                            } else {
                                Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_erroractulizarventa), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_pagonomayor), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_agregueunpago), Toast.LENGTH_SHORT).show();
                }
            }
        });
        final AlertDialog.Builder midialogo = new AlertDialog.Builder(this);
        midialogo.setView(layout);
        return midialogo.create();
    }

    private void showDialogfinalizar() {
        Dialog dialog;
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        venta.setPago(venta.getPago() + venta.getdeuda());//actulizar el pago en la clase venta
                        venta.setEstatus(VENTA_CONTADO);//actulizar el estado de la venta
                        boolean res = dbSistema.updateVentaCredito(idventa, venta);//actulizar la venta en la base de datos
                        if (res) {
                            dialog.dismiss();
                            Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_ventaactualizada), Toast.LENGTH_SHORT).show();
                            tvpago.setText(DEF.format(venta.getPago()));//actualizar el pago en la vista
                            tvdeuda.setText(DEF.format(venta.getdeuda()));//actualizar la deuda en la vista
                            agregarpago.setEnabled(false);
                            finalizarventa.setEnabled(false);
                        } else {
                            Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_erroractulizarventa), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder = builder.setMessage(getString(R.string.str_confirmarventafin));
        builder = builder.setPositiveButton(getString(R.string.close_positive), listener);
        builder = builder.setNegativeButton(getString(R.string.close_negative), listener);
        builder = builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    private Dialog eliminarPago() {

        final double[] pago = new double[1];
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_add_pago, null);
        final TextView labelpago = (TextView) layout.findViewById(R.id.tv_labelpago);
        final EditText edtpago = (EditText) layout.findViewById(R.id.edt_pago);
        final Button btnaceptar = (Button) layout.findViewById(R.id.btnaceptarpago);
        labelpago.setText(getString(R.string.str_eliminar_pago));

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strpago = edtpago.getText().toString().trim();
                if (!strpago.isEmpty()) {
                    pago[0] = Double.parseDouble(strpago);
                    if (!(pago[0] > venta.getPago()) && !(pago[0] == 0)) {//Verificar que el pago no sea mayor al pago actual, no sea igual a cero
                        venta.setPago(venta.getPago() - pago[0]);//actualizar el pago en la clase pago
                        boolean res = dbSistema.updateVentapago(idventa, venta.getPago());//actualizar el pago en la base de datos
                        if (res) {
                            Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_pagoactulizado), Toast.LENGTH_SHORT).show();
                            tvpago.setText(DEF.format(venta.getPago()));//actualizar el pago en la vista
                            tvdeuda.setText(DEF.format(venta.getdeuda()));//actualizar la deuda en la vista
                            dlg_eliminarpago.dismiss();
                        } else {
                            Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_erroractulizarpago), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DetalleVentaCreditoActivity.this, getString(R.string.str_pagonomayor2), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetalleVentaCreditoActivity.this, getResources().getString(R.string.str_agregueunpago), Toast.LENGTH_SHORT).show();
                }
            }
        });
        final AlertDialog.Builder midialogo = new AlertDialog.Builder(this);
        midialogo.setView(layout);
        return midialogo.create();
    }
}

