package com.gcr.android.inventorytest.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gcr.android.inventorytest.R;

import java.util.HashMap;
import java.util.Map;

public class FireBaseNotificationManager {

    public static void sendFcmNotification(final String nombre, final double stock , Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String server = preferences.getString("webserver","www.proyectositsp.thats.im");
        String url = "http://"+ server +"/Innovacion2016/notificar.php";
        final String tienda = preferences.getString("storeName","ITSP");
        final String token = preferences.getString("registration_id", null);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest objectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*
                        if(response.equals("True")){
                            Toast.makeText(FireInstanceIdService.this, "Exito al enviar el ID al servidor", Toast.LENGTH_SHORT).show();
                            Log.d("Hola","Exito");
                        }else{
                            Toast.makeText(FireInstanceIdService.this, "Error al enviar el ID al servidor", Toast.LENGTH_SHORT).show();
                            Log.d("Hola","Mamo");
                        }
                        */
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
                params.put("nombre", nombre);
                params.put("storeName", tienda);
                params.put("stock", String.valueOf(stock));
                return params;
            }
        };
        queue.add(objectRequest);
    }
    public static void sendAndroidNotificacion(String nombre, double stock, Context context, Class nom){
        Intent intent = new Intent(context, nom);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Se acaba: "+ nombre)
                .setContentText("Stock: " + stock)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
