package com.gcr.android.inventorytest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class ModificarProductoActivity extends AppCompatActivity {
    private DBSistema database;
    private EditText editProductoUpdate;
    private EditText editBarcodeUpdate;
    private EditText editPrecioUpdate;
    private EditText editReordenUpdate;
    private EditText editStockUpdate;
    private Button btnUpdateProducto;

    String uno;
    String dos;

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private static final String TAG = "ProductCapture";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private Spinner unidad;
    private String medida;
    private int idmedida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);

        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        database = new DBSistema(this);
        try {
            database.setUpDatabase(this);
        }catch (Exception e){
            Log.e("Tablas",e.getMessage());
        }

        Intent intentInicial = getIntent();
        final int id = intentInicial.getIntExtra("id", 0);
        Producto aux = database.selectProducto(id);
        editBarcodeUpdate= (EditText)findViewById(R.id.editBarcodeUpdate);
        editPrecioUpdate= (EditText)findViewById(R.id.editPrecioUpdate);
        editProductoUpdate= (EditText)findViewById(R.id.editProductoUpdate);
        editReordenUpdate = (EditText)findViewById(R.id.editReordenUpdate);
        editStockUpdate = (EditText)findViewById(R.id.editStockUpdate);


        final ArrayList<Medida> datos = database.selectMedidas();
        unidad = (Spinner) findViewById(R.id.modificar_medida);
        ArrayAdapter<Medida> adapterUnidad = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, datos);
        adapterUnidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidad.setAdapter(adapterUnidad);
        int seleccion = 0;
        for(int i = 0; i < datos.size(); i++) {
            if (aux.getIdMedida() == datos.get(i).getId()){
                seleccion = i;
            }
        }
        unidad.setSelection(seleccion);
        unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idmedida = ((Medida) parent.getItemAtPosition(position)).getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ModificarProductoActivity.this,"noo", Toast.LENGTH_SHORT).show();
            }
        });

        editBarcodeUpdate.setText(intentInicial.getStringExtra("barcode"));
        editProductoUpdate.setText(intentInicial.getStringExtra("nombre"));
        editPrecioUpdate.setText(String.valueOf(intentInicial.getDoubleExtra("precio", 0.0)));
        editReordenUpdate.setText(String.valueOf(aux.getReorden()));
        editStockUpdate.setText(String.valueOf(aux.getStock()));

        btnUpdateProducto = (Button)findViewById(R.id.btnModificarProducto);
        btnUpdateProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barcode = editBarcodeUpdate.getText().toString().trim();
                String producto = editProductoUpdate.getText().toString().trim();
                String p=editPrecioUpdate.getText().toString().trim();
                String re = editReordenUpdate.getText().toString().trim();
                String st = editStockUpdate.getText().toString().trim();

                if(!barcode.equals("") && !producto.equals("") && !p.equals("")){
                    double precio = Double.parseDouble(p);
                    double reorden = Double.parseDouble(re);
                    double stock = Double.parseDouble(st);
                    int result = database.updateProducto( id, producto, barcode, precio, reorden, stock, idmedida);
                    if (result>0){
                        Intent intent = new Intent(ModificarProductoActivity.this, ListadoProductosActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(ModificarProductoActivity.this,"Error al modificar los datos",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ModificarProductoActivity.this,"Rellene todos los campos",Toast.LENGTH_SHORT).show();
                }
            }
        });

        autoFocus = (CompoundButton) findViewById(R.id.compAutoFocusUpdate);
        useFlash = (CompoundButton) findViewById(R.id.compUseFlashUpdate);
        autoFocus.setChecked(true);

        mPreview = (CameraSourcePreview) findViewById(R.id.cameraSourcePreviewUpdate);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.barcodeGraphicOverlayUpdate);

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
    }

    private void checkCameraPermission(){
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus.isChecked(), useFlash.isChecked());
        } else {
            requestCameraPermission();
        }
    }

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

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

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
                        editBarcodeUpdate.setText(rawValue);
                    }
                });
            }
        });

        if (!barcodeDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

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

    private void startCameraSource() throws SecurityException {
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

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

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
            case KeyEvent.KEYCODE_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    Intent intent = new Intent(ModificarProductoActivity.this, ListadoProductosActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}