package com.gcr.android.inventorytest;

import android.content.SharedPreferences;
import android.os.Environment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class FCMActivity extends AppCompatActivity {
    Button btnRegistrar, btnDesregistrar;
    Button btnPullDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnDesregistrar = (Button) findViewById(R.id.btnDesregistrar);
        btnPullDB = (Button) findViewById(R.id.btnPullDatabase);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();
                            Log.d("FCM", "Token: " + token);
                            Toast.makeText(FCMActivity.this, "Token: " + token, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        btnDesregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRegistrationFromServerAndClient();
            }
        });

        btnPullDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    if (sd.canWrite()) {
                        String currentDBPath = "/data/data/" + getPackageName() + "/databases/miscelanea.db";
                        String backupDBPath = "backupname.db";

                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                    }
                } catch (Exception ignored) { }
            }
        });
    }

    public void deleteRegistrationFromServerAndClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseMessaging.getInstance().deleteToken();
                
                SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(FCMActivity.this);
                final String token = preferences.getString("registration_id", null);
                SharedPreferences.Editor editor = preferences.edit();
                String server = preferences.getString("webserver","www.proyectositsp.thats.im");
                editor.putString("registration_id", "");
                editor.apply();

                String url = "http://"+ server +"/Innovacion2016/deleteDevice.php";
                RequestQueue queue = Volley.newRequestQueue(FCMActivity.this);
                StringRequest objectRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("True")){
                                    Toast.makeText(FCMActivity.this, "Exito al eliminar el ID al servidor", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(FCMActivity.this, "Error al eliminar el ID al servidor", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String>  params = new HashMap<>();
                        params.put("registration_id", token);
                        return params;
                    }
                };
                queue.add(objectRequest);
            }
        }).start();
    }
}
