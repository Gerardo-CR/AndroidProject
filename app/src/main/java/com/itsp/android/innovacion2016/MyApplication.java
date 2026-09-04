package com.itsp.android.innovacion2016;

import android.app.Application;

//import com.facebook.stetho.Stetho;

/**
 * Created by Agustin on 09/05/2016.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        /*
        Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                        Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this))
                .build();
        */
    }
}
