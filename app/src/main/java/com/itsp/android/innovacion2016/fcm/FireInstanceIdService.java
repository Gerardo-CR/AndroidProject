package com.itsp.android.innovacion2016.fcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


public class FireInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIdService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        saveRegistrationToPreferences(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String server = preferences.getString("webserver","www.proyectositsp.thats.im");
        String url = "http://"+ server +"/Innovacion2016/registerDevice.php";
        RequestQueue queue = Volley.newRequestQueue(FireInstanceIdService.this);
        StringRequest objectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("True")){
                            Toast.makeText(FireInstanceIdService.this, "Exito al enviar el ID al servidor", Toast.LENGTH_SHORT).show();
                            Log.d("Hola","Exito");
                        }else{
                            Toast.makeText(FireInstanceIdService.this, "Error al enviar el ID al servidor", Toast.LENGTH_SHORT).show();
                            Log.d("Hola","Mamo");
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

    private void saveRegistrationToPreferences(String token){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        // Save to SharedPreferences
        editor.putString("registration_id", token);
        editor.apply();
    }
}
