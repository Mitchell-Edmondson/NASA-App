package com.example.android.nasaapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Mitchell on 25/09/17.
 */


public class JSON extends AppCompatActivity
{
    //Global object because getTwitter needs to have access to this jsonObject
    JSONObject jsonObject;
    //Global object for facebook
    CallbackManager callbackManager = CallbackManager.Factory.create();

    public void getTwitter(View view)
    {
        Intent intent = new Intent(this, myTwitter.class);
        //Go to SQL table and get the most recent entry
        //Pass to to Twitter class
        Storage storage = new Storage(this);
        intent.putExtra("Photographer", storage.recieveData());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsonlayout);


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App codeMi
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }


    @Override
    protected void onStart() {

        super.onStart();
        String myurl = "https://api.nasa.gov/planetary/apod?api_key=qkH6u6R6hmdBK1yuUOhhlSFyfIg7oltCcVp5ldFS";

        //Makes the string into a uri
        //we can then pass that uri into a url, which we can connect to
        //connecting to a url must be done in an asynctask or other thread

        Uri myuri = Uri.parse(myurl);
        try {
            URL mynewurl = new URL(myuri.toString());

            //adding the .get makes the asynctask able to return the JSONObject
            jsonObject = new MyTask().execute(mynewurl).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Function call to get either the Image or Video that NASA has put up
        getImageorVideo(jsonObject);

        //Function call to get and display the photographer's name (if available)
        getPhotographerName(jsonObject);
    }

    //Working on this trying to get the ability to share the picture to facebook
    public void FB_Share(View view)
    {
        Intent intent = new Intent(this, facebookShare.class);
        try {
            intent.putExtra("Media", jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startActivity(intent);
/*
        ImageView imagea = (ImageView) findViewById(R.id.image);
        Bitmap image = ((BitmapDrawable) imagea.getDrawable()).getBitmap();
        //Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

       // ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
       // shareButton.setShareContent(content);
        shareDialog.show(content, null);
*/
    }



    //Opens up the youtube app to play the video
    public void playYoutube(View view)
    {
        String videourl = null;
        try {
            videourl = jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Still need work to get it to play the video
        Uri uri = Uri.parse(videourl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void getPhotographerName(JSONObject jsonObject)
    {
        //Get the id's for the textview and button to change their message
        TextView textView = (TextView) findViewById(R.id.photographer);
        Button button = (Button) findViewById(R.id.twitterbutton);

        String photographerName = null;
        Storage database = new Storage(this);

        //There is a photographer, so store it in database
        if(jsonObject.has("copyright")) {
            //Accessing database
            try {
                database.insertdata(jsonObject.getString("copyright"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Read from database to get photographer's name
            photographerName = database.recieveData();
            textView.setText("Photographer: " + photographerName);
            button.setText("See who tweeted about " + photographerName);
        }
        else {
            //Read from database to get the previous photographer's name
            photographerName = database.recieveData();
            textView.setText("Photographer's name not given");
            button.setText("Search previous photographer, " + photographerName + ", on Twitter.");
        }
    }

    private void getImageorVideo(JSONObject jsonObject)
    {
        String ImageOrVideoMedia = null;
        //Try to get the media type (image or video)

        /* Sometimes NASA release a video for their picture of the day*/

        try {
            ImageOrVideoMedia = jsonObject.getString("media_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Check to see if it is an image or a video
        if(ImageOrVideoMedia.equals("image")) {
            ImageView image = (ImageView) findViewById(R.id.image);
            //Parse JSONObject to get actual url
            try {
                String imageurl = jsonObject.getString("url");
                //Make it into a uri
                Uri uri = Uri.parse(imageurl);
                //Now make it a connectable URL
                URL url = new URL(uri.toString());
                //Load the picture on the screen
                Picasso.with(this).load(String.valueOf(url)).into(image);
            } catch(IOException e){
                e.printStackTrace();
            }catch(JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            setContentView(R.layout.jsonvideolayout);
        }

        //Set the title of the picture
        TextView textViewA = (TextView) findViewById(R.id.title);
        try {
            textViewA.setText("Title: " + jsonObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Takes a url, doesnt need to update anything, returns a JSONObject
    private class MyTask extends AsyncTask<URL, Void, JSONObject>
    {
        String data;
        @Override
        protected JSONObject doInBackground(URL... params) {
            URL myurl = params[0];;
            JSONObject json = null;
            try {
                //creating a connection
                HttpURLConnection myconnect = (HttpURLConnection) myurl.openConnection();
                //getting an input stream
                InputStream stream = myconnect.getInputStream();
                //creating a buffer to get the data
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String line ="";
                data = line;
                //getting the json from the url
                while(line!= null)
                {
                    line = buffer.readLine();
                    if(line != null){
                    data = data + line;
                    }
                }
                /* So now we have the json in a string "data"
                we can take the passed JSONObject and put the string
                into it. This creates a filled in JSONObject we can parse */
                json = new JSONObject(data);
            } catch (IOException e) {
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }
            return json;
        }
    }
}