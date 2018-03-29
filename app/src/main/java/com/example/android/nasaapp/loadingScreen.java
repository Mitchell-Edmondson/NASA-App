package com.example.android.nasaapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * Created by Mitchell on 17/12/17.
 */

public class loadingScreen extends Activity
{
    private final int WAIT_TIME = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        findViewById(R.id.mainSpinner1).setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //Simulating a long running task

	  /* Create an Intent that will start the ProfileData-Activity. */
                Intent mainIntent = new Intent(loadingScreen.this,JSON.class);
                loadingScreen.this.startActivity(mainIntent);
                loadingScreen.this.finish();
            }
        }, WAIT_TIME);
    }
}