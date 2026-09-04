package com.itsp.android.innovacion2016;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.itsp.android.innovacion2016.operaciones.FunGen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.itsp.android.innovacion2016.Venta.DEF;

public class PuntoVentaActivity extends AppCompatActivity {

    private ListView lista;
    private static ArrayList<String> listCodigos;
    private static DBSistema database;
    private static ArrayList<Producto> listProductos;

    private static AdapterProductosVenta adapter;
    private Button btnCobrarVenta;
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private static TextView textTotal;
    private static double total;
    private static MediaPlayer mediaPlayer;
    //cuadro de dialogo para completar las venta
    private AlertDialog dialogoventa;
    private ArrayList<Cliente> listClientes;
    private ArrayAdapter<Cliente> adapterCliente;

    //Barcode******************************************
    private static final String TAG = "ProductCapture";
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;


    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    //Barcode**********************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto_venta);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listCodigos = new ArrayList<>();
        total = 0;
        autoFocus = (CompoundButton) findViewById(R.id.compAutoFocusVenta);
        useFlash = (CompoundButton) findViewById(R.id.compUseFlashVenta);
        autoFocus.setChecked(true);
        mediaPlayer = MediaPlayer.create(this, R.raw.fx_beep);

        mPreview = (CameraSourcePreview) findViewById(R.id.cameraSourcePreviewVenta);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.barcodeGraphicOverlayVenta);

        checkCameraPermission();

        autoFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPreview != null) {
                    mPreview.stop();
                }
                checkCameraPermission();
                startCameraSource();
            }
        });

        useFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPreview != null) {
                    mPreview.stop();
                }
                checkCameraPermission();
                startCameraSource();
            }
        });

        lista = (ListView)findViewById(R.id.listProductosVenta);
        database = new DBSistema(this);
        try {
            database.setUpDatabase();
        }catch (Exception e){
            Log.e("Tablas", e.getMessage());
        }



        listProductos = new ArrayList<>();
        adapter = new AdapterProductosVenta(this, listProductos);
        lista.setAdapter(adapter);

        textTotal= (TextView)findViewById(R.id.textTotalVenta);
        btnCobrarVenta = (Button)findViewById(R.id.btnCobrarVenta);
        btnCobrarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoventa = dialogventa();
                dialogoventa.show();
            }
        });
    }

    private String selectFecha(){

        int dia, mes, anio;
        final String[] fecha = {""};

        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);


        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                if ((monthOfYear + 1) < 10 && dayOfMonth < 10) {
                    fecha[0] = (year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                } else if ((monthOfYear + 1) < 10) {
                    fecha[0] = (year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                } else if (dayOfMonth < 10) {
                    fecha[0] = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                } else {
                    fecha[0] = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                }


            }
        }, anio, mes, dia);
        datePickerDialog.show();

        return fecha[0];
    }

    private AlertDialog dialogventa(){
        LayoutInflater inflater = LayoutInflater.from(PuntoVentaActivity.this);
        View layout = inflater.inflate(R.layout.dialog_venta, null);
        //Formulario
        final EditText txtPago = (EditText) layout.findViewById(R.id.txtpago);
        final RadioGroup tipoventa = (RadioGroup) layout.findViewById(R.id.tipoventa);
        final View contDatos = layout.findViewById(R.id.contenedorcliente);
        final AutoCompleteTextView txtCliente  = (AutoCompleteTextView) layout.findViewById(R.id.ac_cliente);
        final EditText txtPlazo = (EditText) layout.findViewById(R.id.txtplazo);
        final TextView txtTotal = (TextView) layout.findViewById(R.id.txttotal);
        final Button terminar = (Button) layout.findViewById(R.id.btn_guardarcliente);
        final ImageButton cancelar = (ImageButton) layout.findViewById(R.id.btn_cancelar);

        listClientes = database.selectClientes();
        adapterCliente = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listClientes);
        txtCliente.setThreshold(3);//Caracteres a escribir en el autocomplete
        txtCliente.setAdapter(adapterCliente);//Adaptador del autocomplete
        final int[] seleccion = {-1};
        txtCliente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seleccion[0] = position;
                txtCliente.setEnabled(false);
            }
        });
        contDatos.setVisibility(View.GONE);
        final int[] tipo_de_venta = {Venta.VENTA_CONTADO};//1 para ventas a contado y 0 para ventas a credito
        txtTotal.setText(DEF.format(total));
        tipoventa.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radiocontado){
                    tipo_de_venta[0] = Venta.VENTA_CONTADO;
                    contDatos.setVisibility(View.GONE);
                }else if (checkedId == R.id.radiocredito){
                    tipo_de_venta[0] = Venta.VENTA_CREDITO;
                    contDatos.setVisibility(View.VISIBLE);
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCliente.setText("");
                seleccion[0] = -1;
                if (!txtCliente.isEnabled())
                    txtCliente.setEnabled(true);
            }
        });

        txtPlazo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dia, mes, anio;
                Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                anio = c.get(Calendar.YEAR);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(PuntoVentaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if ((monthOfYear + 1) < 10 && dayOfMonth < 10) {
                            txtPlazo.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                        } else if ((monthOfYear + 1) < 10) {
                            txtPlazo.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                        } else if (dayOfMonth < 10) {
                            txtPlazo.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                        } else {
                            txtPlazo.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pago = txtPago.getText().toString().trim();
                if (!pago.isEmpty()) {
                    final double pagoCantidad = FunGen.ConvertDoubleStr(pago);
                    if (tipo_de_venta[0] == Venta.VENTA_CREDITO) {
                        String nombreCliente = txtCliente.getText().toString().trim();
                        String txplazo = txtPlazo.getText().toString().trim();
                        if (!nombreCliente.isEmpty() && !txplazo.isEmpty()) {
                            if(total != 0) {
                                Cliente cliente;
                                ListAdapter listAdapter = txtCliente.getAdapter();
                                if (seleccion[0] != -1 && listAdapter.getCount() != 0){
                                    cliente = (Cliente) listAdapter.getItem(seleccion[0]);
                                } else {
                                    cliente = new Cliente(nombreCliente);
                                }
                                vender(pagoCantidad, cliente, txplazo, tipo_de_venta[0]);
                            }else{
                                Toast.makeText(PuntoVentaActivity.this, "No ha agregado ningún producto a la venta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PuntoVentaActivity.this, "Ingrese el nombre del cliente y el plazo de pago", Toast.LENGTH_SHORT).show();
                        }
                    } else if (tipo_de_venta[0] == Venta.VENTA_CONTADO) {
                        if (total != 0) {
                            if (pagoCantidad >= total) {
                                vender(pagoCantidad, null, null, tipo_de_venta[0]);
                            } else {
                                Toast.makeText(PuntoVentaActivity.this, "Cantidad de pago insuficiente", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PuntoVentaActivity.this, "No ha agregado ningún producto a la venta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(PuntoVentaActivity.this, "Introduzca la cantidad del pago", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final AlertDialog.Builder midialogo = new AlertDialog.Builder(PuntoVentaActivity.this);
        midialogo.setView(layout);

        return midialogo.create();
    }

    private void vender(double pagoCantidad, Cliente nombrecliente, String plazo ,int tipoventa){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String[] fecha = format.format(new Date()).split(" ");
        try {
            database.insertProductoVenta(listProductos, fecha, total, pagoCantidad, tipoventa, nombrecliente, plazo);
            Toast.makeText(PuntoVentaActivity.this, "Venta completada con exito", Toast.LENGTH_LONG).show();
            double cambio = 0;
            if (tipoventa == Venta.VENTA_CREDITO)
                cambio = 0;
            else if (tipoventa == Venta.VENTA_CONTADO)
                cambio = pagoCantidad - total;

            Snackbar.make(btnCobrarVenta, "Cambio: " + DEF.format(cambio), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { }
                    }).show();

            listProductos.clear();
            listCodigos.clear();
            adapter.notifyDataSetChanged();
            total = 0;
            textTotal.setText(DEF.format(total));
            dialogoventa.dismiss();
        }catch (Exception ignored){}
    }

    public static void actualizarLista(String rawValue, boolean aumentar){
        Producto producto = database.selectProducto(rawValue);
        if (producto != null){
            if(listCodigos.contains(producto.getBarcode())){
                int index = listCodigos.indexOf(producto.getBarcode());
                double cantidad = listProductos.get(index).getCantidad();
                Producto aux = listProductos.get(index);
                listProductos.remove(index);
                if(aumentar){
                    cantidad = cantidad+1.0;
                }else{
                    cantidad = cantidad-1.0;
                }
                if(cantidad>0){
                    aux.setCantidad(cantidad);
                    listProductos.add(index,aux);
                }else{
                    listCodigos.remove(index);
                }
            }else{
                listCodigos.add(producto.getBarcode());
                listProductos.add(producto);
                mediaPlayer.start();
            }
            adapter.notifyDataSetChanged();
            if (aumentar){
                total += producto.getPrecio();
            }else{
                total -= producto.getPrecio();
            }
            if(total<0)
                total=0;
            textTotal.setText(DEF.format(total));
        }
    }

    public static void actualizarLista(String rawValue, boolean aumentar, double cantidadDecimal){
        Producto producto = database.selectProducto(rawValue);
        if (producto != null){
            if(listCodigos.contains(producto.getBarcode())){
                int index = listCodigos.indexOf(producto.getBarcode());
                double cantidad = listProductos.get(index).getCantidad();
                Producto aux = listProductos.get(index);
                listProductos.remove(index);
                if(aumentar){
                    cantidad = cantidad + 1.0;
                }else{
                    cantidad = cantidad - 1.0;
                }
                if(cantidad > 0){
                    aux.setCantidad(cantidad);
                    listProductos.add(index,aux);
                }else{
                    listCodigos.remove(index);
                }
            }else{
                listCodigos.add(producto.getBarcode());
                producto.setCantidad(cantidadDecimal);
                listProductos.add(producto);
                mediaPlayer.start();
            }
            adapter.notifyDataSetChanged();
            if (aumentar){
                total+=(cantidadDecimal*producto.getPrecio());
            }else{
                total-=producto.getPrecio();
            }
            textTotal.setText(DEF.format(total));
        }
    }

    // Check for the camera permission before accessing the camera.  If the
    // permission is not granted yet, request permission.
    private void checkCameraPermission(){
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus.isChecked(), useFlash.isChecked());
        } else {
            requestCameraPermission();
        }
    }
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        barcodeFactory.setOnNewBarcodeListener(new BarcodeTrackerFactory.OnNewBarcodeListener() {
            @Override
            public void onNewItem(Barcode item) {
                Log.d("BarcodeFound", "Found new barcode! " + item.rawValue);
                final String rawValue = item.rawValue;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actualizarLista(rawValue, true);
                    }
                });
            }
        });

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            //boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            //boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            boolean autofocus = autoFocus.isChecked();
            boolean useflash = autoFocus.isChecked();
            createCameraSource(autofocus,useflash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    mCameraSource.doZoom(1.2f);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    mCameraSource.doZoom(0.6f);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_punto_venta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_product:
                LayoutInflater inflater = LayoutInflater.from(PuntoVentaActivity.this);
                View layout = inflater.inflate(R.layout.dialog_add_producto, null);
                final EditText editCod = (EditText) layout.findViewById(R.id.dialogTextCodigo);
                final EditText editCan = (EditText) layout.findViewById(R.id.dialogTextCantidad);

                new AlertDialog.Builder(PuntoVentaActivity.this)
                        .setView(layout)
                        .setTitle("Agregar producto")
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String codigo = editCod.getText().toString().trim();
                                String can = editCan.getText().toString();

                                if (!codigo.equals("")&& !can.equals("")){
                                    double cantidad = FunGen.ConvertDoubleStr(can);
                                    actualizarLista(codigo, true, cantidad);}
                                else{
                                    Toast.makeText(PuntoVentaActivity.this,"Rellene todos los campos",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogUpdate(int id, String nombre, String barcode, double precio){
        LayoutInflater inflater = LayoutInflater.from(PuntoVentaActivity.this);
        View layout = inflater.inflate(R.layout.dialog_add_producto, null);
        final EditText editCod = (EditText) layout.findViewById(R.id.dialogTextCodigo);
        final EditText editCan = (EditText) layout.findViewById(R.id.dialogTextCantidad);

        new AlertDialog.Builder(PuntoVentaActivity.this)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        double cantidadDecimal = FunGen.ConvertDoubleStr(editCan.getText().toString());
                        actualizarLista(editCod.getText().toString(), true, cantidadDecimal);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == event.KEYCODE_BACK){
            new AlertDialog.Builder(PuntoVentaActivity.this)
            .setTitle("¿Desea salir?")
            .setPositiveButton(R.string.close_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PuntoVentaActivity.this.finish();
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
}