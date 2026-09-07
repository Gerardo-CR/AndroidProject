package com.gcr.android.inventorytest.Servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.gcr.android.inventorytest.DBSistema;
import com.gcr.android.inventorytest.MainActivity;
import com.gcr.android.inventorytest.Prolote;

import java.util.ArrayList;

/**
 * Created by Gerardo Castillo on 28/04/2017.
 */
public class Recibir_Fecha_Caducidad extends BroadcastReceiver {
    public static  int codigo=1234;
    private DBSistema dbSistema;
    ArrayList<Prolote> lotescaducidad;
    ArrayList productos;


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, Servicio_Fecha_Caducidad.class);
        context.startService(i);

        //procesar
        dbSistema=new DBSistema(context);

        try {

            dbSistema.setUpDatabase(context);

        }catch (Exception e){

            Log.e("tablas",e.getMessage());

        }

        lotescaducidad= dbSistema.prolot();
productos=new ArrayList();
        for(Prolote pr:lotescaducidad){

            String nombre=String.valueOf(pr.nombre);
            if(pr.diasres <5){

if(!productos.contains(nombre)){

    //codigo+=1;
    productos.add(nombre);

}


            }


        }



    }


    public  void notificacion(Context context,String texo){

        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Context contexto = context.getApplicationContext();
        Intent notIntent = new Intent(contexto,MainActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, notIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);
        builder.setContentIntent(contIntent)

                .setTicker("Esta a apunto de caducar ")
                .setContentTitle("a")
                .setContentTitle("")
                .setContentText(texo)
                .setContentInfo("Info")
                .setLargeIcon(BitmapFactory.decodeResource(contexto.getResources(), android.R.drawable.ic_notification_overlay))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setAutoCancel(true); //Cuando se pulsa la notificación ésta desaparece

        Notification notif = new NotificationCompat.BigTextStyle(builder)
                .bigText("funciona")
                .setBigContentTitle("ejemplo")
                .setSummaryText("Resumen de tareas")
                .build();





//AutoCancel: cuando se pulsa la notificaión ésta desaparece
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
//Añadir sonido, vibración y luces
notif.defaults |= Notification.DEFAULT_SOUND;
//notif.defaults |= Notification.DEFAULT_VIBRATE;
//notif.defaults |= Notification.DEFAULT_LIGHTS;
        //Enviar notificación
        notManager.notify(codigo, notif);



    }

}



