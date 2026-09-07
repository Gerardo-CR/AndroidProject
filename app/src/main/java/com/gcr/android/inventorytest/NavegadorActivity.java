package com.gcr.android.inventorytest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;

/**
 * Created by Gerardo Castillo on 03/06/2017.
 */

public class NavegadorActivity extends AppCompatActivity implements OnClickListener{

    private WebView webView;
    private ProgressBar progressBar;
    private String url = " ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navegador);
        Button boton1=(Button) findViewById(R.id.button1);
        boton1.setOnClickListener(this);
        Button boton2=(Button) findViewById(R.id.button2);
        boton2.setOnClickListener(this);
        Button boton3=(Button) findViewById(R.id.button3);
        boton3.setOnClickListener(this);

        //recuperar la URL enviada por la actividad principal
        Intent intent = this.getIntent();
        url = intent.getStringExtra("url");

        webView = (WebView) findViewById(R.id.webView);

        webView.setWebViewClient(new Client());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webView.loadUrl(url);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                NavegadorActivity.this.setProgress(progress * 1000);
                progressBar.incrementProgressBy(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    class Client extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){

            return(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id == R.id.button1)
            finish();
        else if (id == R.id.button2)
            webView.loadUrl(url);
        else if (id == R.id.button3)
            webView.goForward();
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack())
        {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}