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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class ProductCaptureActivity extends AppCompatActivity {
    private DBSistema database;
    private EditText editProducto;
    private EditText editBarcode;
    private EditText editPrecio;
    private EditText editReorden;
    //private EditText editStock;
    private Button btnInsertarProducto;
    private Spinner unidad;
    private ImageButton agregarmedida;

    //MainActivity*************************************
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    //MainActivity*************************************

    private int idmedida;
    private Dialog dialogmedida;

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
        setContentView(R.layout.activity_product_capture);

        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        database = new DBSistema(this);
        try {
            database.setUpDatabase();
        }catch (Exception e){
            Log.e("Tablas",e.getMessage());
        }
        agregarmedida = (ImageButton) findViewById(R.id.imgb_agregar);
        final ArrayList<Medida> datos = database.selectMedidas();
        unidad= (Spinner)findViewById(R.id.spinnerUnidad);
        ArrayAdapter<Medida> adapterUnidad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
		adapterUnidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidad.setAdapter(adapterUnidad);

        unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idmedida = ((Medida) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ProductCaptureActivity.this,"noo", Toast.LENGTH_SHORT).show();
            }
        });
        agregarmedida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogmedida = getDialogmedida();
                dialogmedida.show();
            }
        });
        editBarcode= (EditText)findViewById(R.id.editBarcode);
        editPrecio= (EditText)findViewById(R.id.editPrecio);
        editProducto= (EditText)findViewById(R.id.editProducto);
        editReorden = (EditText)findViewById(R.id.editReorden);

       // editStock = (EditText)findViewById(R.id.editStock);
        btnInsertarProducto = (Button)findViewById(R.id.btnInsertarProducto);
        btnInsertarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barcode = editBarcode.getText().toString().trim();
                String producto = editProducto.getText().toString().trim();
                String p=editPrecio.getText().toString().trim();
                String re = editReorden.getText().toString().trim();
               // String st = editStock.getText().toString().trim();
                if(!barcode.equals("") && !producto.equals("") && !p.equals("") && !re.equals("")){
                    double precio = Double.parseDouble(p);
                    double reorden = Double.parseDouble(re);
                   //double stock = Double.parseDouble(st);
                    long result = database.insertProducto(producto,barcode,precio, reorden, 0.0, idmedida);
                    if (result!=-1){
                        Toast.makeText(ProductCaptureActivity.this, "Exito al insertar el producto", Toast.LENGTH_SHORT).show();
                        editBarcode.setText("");
                        editPrecio.setText("");
                        editProducto.setText("");
                        editReorden.setText("");
                    }else {
                        Toast.makeText(ProductCaptureActivity.this,"Error al insertar el producto",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProductCaptureActivity.this,"Rellene todos los campos",Toast.LENGTH_SHORT).show();
                }
            }
        });

        autoFocus = (CompoundButton) findViewById(R.id.compAutoFocus);
        useFlash = (CompoundButton) findViewById(R.id.compUseFlash);
        autoFocus.setChecked(true);


        mPreview = (CameraSourcePreview) findViewById(R.id.cameraSourcePreview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.barcodeGraphicOverlay);

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
    //Dialogo para agregar madidas
    private Dialog getDialogmedida() {
        LayoutInflater inflater = LayoutInflater.from(ProductCaptureActivity.this);
        View layout = inflater.inflate(R.layout.dialog_add_medida, null);
        //formulario
        final EditText edt_medida = (EditText) layout.findViewById(R.id.edt_medida);
        final EditText edt_abr = (EditText) layout.findViewById(R.id.edt_abr);
        final Button btnacept = (Button) layout.findViewById(R.id.btn_insertarmedida);
        btnacept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medida, abr;
                medida = edt_medida.getText().toString().trim();
                abr = edt_abr.getText().toString().trim();

                if (!medida.isEmpty() && !abr.isEmpty()){
                    long result = database.insertMedida(new Medida(medida, abr));
                    if (result != -1){
                        Toast.makeText(ProductCaptureActivity.this, "Exito al insertar", Toast.LENGTH_SHORT).show();
                        dialogmedida.dismiss();
                        ArrayAdapter<Medida> adapter = new ArrayAdapter<>(ProductCaptureActivity.this,
                                android.R.layout.simple_spinner_item, database.selectMedidas());
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        unidad.setAdapter(adapter);
                    } else {
                        Toast.makeText(ProductCaptureActivity.this,"Error al insertar",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductCaptureActivity.this,"Rellene todos los campos",Toast.LENGTH_SHORT).show();
                }
            }
        });
        final AlertDialog.Builder midialogo = new AlertDialog.Builder(ProductCaptureActivity.this);
        midialogo.setView(layout);
        return midialogo.create();
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
                        editBarcode.setText(rawValue);
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
}