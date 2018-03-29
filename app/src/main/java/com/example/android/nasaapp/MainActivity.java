package com.example.android.nasaapp;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity  {

    //This is a global variable to check if the background has been changed
    boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Starting the JSON class for going to the nasa url that
    //contains the JSON, then reading in that JSON and finding the url
    //from there
    public void getAPODJSON(View view)
    {
        //Intent json = new Intent(this, JSON.class);
        Intent json = new Intent(this,loadingScreen.class);
        startActivity(json);
    }

    //Starting the webview class for going to nasa url
    // to see the pic
    public void getAPODWebView(View view)
    {
        //Create an intent in WebView class
        Intent webview = new Intent(this, WebViewClass.class);
        startActivity(webview);
    }

    //This is for changing the background
    public void changeBackground1(View view)
    {
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.mainconstraint);
        if(!changed) {
            constraintLayout.setBackgroundResource(R.drawable.other_image);
            changed = true;
        }
        else{
            constraintLayout.setBackgroundResource(R.drawable.image);
            changed = false;
        }
    }

    //Starting the broswer
    public void getAPODBrowser(View view)
    {
        //ACTION_VIEW takes ftakes the default browser application
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apod.nasa.gov/apod/astropix.html"));
        startActivity(browser);
    }
}
