package com.example.android.nasaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by Mitchell on 25/09/17.
 */

public class WebViewClass extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set view of the screen = webview. Then give webview information
        WebView webview = new WebView(this);
        setContentView(webview);
        //This line will load the apod pic itself
        webview.loadUrl("https://apod.nasa.gov/apod/astropix.html");

    }
}
