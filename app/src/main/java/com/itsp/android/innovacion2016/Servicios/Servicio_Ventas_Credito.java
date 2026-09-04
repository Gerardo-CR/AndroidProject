package com.itsp.android.innovacion2016.Servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gerardo Castillo on 15/01/2018.
 */

public class Servicio_Ventas_Credito extends Service {

    private Timer timer;
    private TimerTask task;

    @Override
    public void onCreate(){
        timer = new Timer();
        task = new Ejecucion();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
        try {
            timer.schedule(task, 0, 5000);
        } catch (Exception ignored) {}
        return START_STICKY;
    }

    @Override
    public  void onDestroy() {
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class Ejecucion extends TimerTask {


        @Override
        public void run() {
            System.err.println("Servicio iniciado" + ' ' + timer.toString());
        }
    }
}