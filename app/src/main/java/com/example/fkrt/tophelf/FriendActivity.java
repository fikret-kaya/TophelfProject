package com.example.fkrt.tophelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class FriendActivity extends AppCompatActivity {

    private Bundle bundle;
    private String friend_id, user_id,fbID;
    private SharedPreferences sharedPref;
    private boolean isFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isFB = sharedPref.getBoolean("isFB", false);
        user_id = sharedPref.getString("u_id", "N/A");
        fbID = sharedPref.getString("fbID", "N/A");
        bundle = getIntent().getExtras();
        friend_id = bundle.getString("friend_id");

        boolean b = false;
        try {
            b = new IsFriendConn().execute(friend_id,user_id).get();
            b = new GetFriendConn().execute(friend_id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    // add or remove friend from friend list
    public void onClick(View v) throws ExecutionException, InterruptedException {
        Button friendship = (Button) findViewById(R.id.friendship);
        String addRemove = "0";

        if(friendship.getText().equals("+ Follow")) {
            addRemove = "1";
            friendship.setText("Following");
        } else {
            addRemove = "0";
            friendship.setText("+ Follow");
        }
        boolean b = new AddRemoveFriendConn().execute(friend_id, user_id, addRemove).get();

    }

    //  Server connectıon
    class IsFriendConn extends AsyncTask<String, Void, Boolean>
    {
        private Button friendship;
        private String responseString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String friend_id = params[0];
            String user_id = params[1];

            try {
                URL url = new URL("http://"+getResources().getString(R.string.ip)+":3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "IsFriend");
                jsonParam.put("friend_id", friend_id);
                jsonParam.put("user_id", user_id);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                writer.flush();
                writer.close();
                os.close();

                int statusCode = conn.getResponseCode();
                InputStream is = null;

                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    responseString = responseString.substring(responseString.length()-3,responseString.length()-2);
                }
                else {
                    is = conn.getErrorStream();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            friendship = (Button) findViewById(R.id.friendship);

            if(responseString.equals("0")) {
                friendship.setText("+ Follow");
            } else if(responseString.equals("1")) {
                friendship.setText("Following");
            }

        }
    }

    //  Server connectıon
    class GetFriendConn extends AsyncTask<String, Void, Boolean>
    {
        private ProfilePictureView image;
        private TextView name;
        private TextView rating;

        private String username, email, images, ratings;
        private Bitmap decodedImage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String friend_id = params[0];

            try {
                URL url = new URL("http://"+getResources().getString(R.string.ip)+":3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "GetUser");
                jsonParam.put("friend_id", friend_id);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                writer.flush();
                writer.close();
                os.close();

                int statusCode = conn.getResponseCode();
                InputStream is = null;

                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line, responseString;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    responseString = responseString.substring(1, response.length() - 1);

                    jsonParam = new JSONObject(responseString);
                    username = jsonParam.getString("username");
                    email = jsonParam.getString("email");
                    images = jsonParam.getString("image");
                    ratings = jsonParam.getString("rating");

                    byte[] decodedString = Base64.decode(images.getBytes(), Base64.DEFAULT);


                    BitmapFactory.Options options;
                    try {
                        decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    } catch (OutOfMemoryError e) {
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 20;
                            decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length,options);
                        } catch(Exception ex) {
                        }
                    }
                    //decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    return true;
                }
                else {
                    is = conn.getErrorStream();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            image = (ProfilePictureView) findViewById(R.id.image);
            if( !isFB){
                image.setProfileId("10209196878817858");
            }else {
                image.setProfileId(fbID);
            }
            name = (TextView) findViewById(R.id.name);
            name.setText(username);
            rating = (TextView) findViewById(R.id.rating);
            rating.setText("Rating : " + ratings);
        }
    }

    //  Server connectıon
    class AddRemoveFriendConn extends AsyncTask<String, Void, Boolean>
    {
        private Button friendship;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String friend_id = params[0];
            String user_id = params[1];
            String addRemove = params[2];
            String JSONtype;

            if(addRemove.equals("0")) {
                JSONtype = "RemoveFriend";
            } else {
                JSONtype = "AddFriend";
            }

            try {
                URL url = new URL("http://"+getResources().getString(R.string.ip)+":3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", JSONtype);
                jsonParam.put("user_id", user_id);
                jsonParam.put("friend_id", friend_id);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                writer.flush();
                writer.close();
                os.close();

                int statusCode = conn.getResponseCode();
                InputStream is = null;

                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line, responseString;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    responseString = responseString.substring(1, response.length() - 1);

                    jsonParam = new JSONObject(responseString);
                    //username = jsonParam.getString("username");
                }
                else {
                    is = conn.getErrorStream();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
