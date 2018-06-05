package com.example.android.nasaapp;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;



/**
 * Created by Mitchell on 14/10/17.
 */

public class myTwitter extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_layout);
        //Getting the string passed from the intent
        //that started this activity
        Intent in = getIntent();
        //bundle is an object that can hold the data that needs
        //to be transferred between activities
        Bundle bundle = in.getExtras();
        String photographer = bundle.getString("Photographer");

        //Attempt to get a list of users who tweeted using the photographers name
        //Convert it into a json string
        String json = null;
        try {
            QueryResult local = new getJSON().execute(photographer).get();
            //Have a list of status
            List<Status> tweets = local.getTweets();
             Gson gson = new Gson();
             json = gson.toJson(tweets);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
         e.printStackTrace();
        }

        //JSON recieved is in one big array
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //GO through the array and find the jsonObjects,
        //call DisplayUser on that object
        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DisplayUser(jsonObject);
        }
    }

    //Display the user's information, including time of tweet,
    //profile picture, username and location in profile
    private void DisplayUser(JSONObject jsonObject)
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mylinearlayout);
        ImageView imageView = new ImageView(this);
        TextView textView = new TextView(this);
        linearLayout.addView(imageView);
        linearLayout.addView(textView);

        try {

            String profilePic = getProfilePic(jsonObject);
            Uri uri = Uri.parse(profilePic);
            URL url = new URL(uri.toString());
            Picasso.with(this).load(String.valueOf(url)).into(imageView);

            textView.setText(" " + getUser(jsonObject) + " is located in: " +
                    getLocation(jsonObject) + " and created this tweet at " + jsonObject.getString("createdAt") + "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //Returns the url for the users profile picture
    private String getProfilePic(JSONObject jsonObject)
    {
        String imageurl = null;
        try {
            JSONObject user = jsonObject.getJSONObject("user");
            imageurl = user.getString("profileImageUrlHttps");
            return imageurl;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageurl;
    }

    //Return the user's location they have on their profile
    private String getLocation(JSONObject jsonObject)
    {
        String location = null;
        try {
            JSONObject user = jsonObject.getJSONObject("user");
            //If there is a value for location, return it
            location = user.getString("location");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(location.equals(""))
        {
            return "N/A";
        }
        return location;
    }

    //Return the user's username
    private String getUser(JSONObject jsonObject)
    {
        String userName = null;

        try {
            JSONObject user = jsonObject.getJSONObject("user");
            userName = user.getString("screenName");;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userName;
    }

    //Pass a string to asynctask that holds the photographer to search for
    //Return the JSONObject as a string
    private class getJSON extends AsyncTask<String, Void, QueryResult>
    {
        @Override
        protected QueryResult doInBackground(String... params)
        {
            //initialize photographer
            String photographer = (String) params[0];

            ConfigurationBuilder builder = new ConfigurationBuilder();

            builder.setOAuthConsumerKey("it1fbf6TOK893o4iZC8FyWdN4")
                    .setOAuthConsumerSecret("BOnuxr0MPL6F8ZSPyzfrXn5aIyFwqaizxPBImoLgSsPhVXL7k9")
                    .setOAuthAccessToken("919300178565148672-FLnzz8iLx6hfsjwz0j6gYPn4GYGJoLE")
                    .setOAuthAccessTokenSecret("W3tGfsm1zVRMKn2DW5VIXfvA8aQ3Bu8xbP4uRaVyplcgq");

            builder.setJSONStoreEnabled(true);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance();
            Query query = new Query(photographer);
            QueryResult result = null;
            try {
                result = twitter.search(query);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return result;
        }

/*
        This whole chunk is code that can write the json to a file
        on the phone itself

         @Override
        protected void onPostExecute(QueryResult result)
        {
            List<twitter4j.Status> tweets = null;
            tweets = result.getTweets();
            Gson gson = new Gson();
            String json = gson.toJson(tweets);

            File root = new File(Environment.getExternalStorageDirectory(), "AndroidTest");
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, "photo");
            FileWriter writer = null;
            try {
                writer = new FileWriter(gpxfile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                writer.append(json);
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

*/

    }
}
