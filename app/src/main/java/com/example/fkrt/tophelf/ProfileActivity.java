package com.example.fkrt.tophelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    private Intent intent;

    private RelativeLayout inner0;
    private ImageView profileImage;
    private SearchView searchView;
    private ListView votes;
    private ListView searchList;

    private SharedPreferences sharedPref;
    private String u_id, user_name;

    ArrayList<MyRating> myRatings;

    private String[] names, places, tags, comments, ratings;

    String[] temp = {"#ankara", "#antalya", "#adana", "#bursa", "#istanbul", "#izmir", "#mersin", "#malatya", "#rize", "#erzurum"};
    int[] images = {R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo,
            R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo};

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(view.getContext(), VoteActivity.class);
                startActivity(intent);
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        u_id = sharedPref.getString("u_id", "N/A");
        user_name = sharedPref.getString("name", "N/A");

        try {
            myRatings = new GetTimelineConn().execute(u_id).get();
            boolean b = new GetFriendConn().execute(u_id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        inner0 = (RelativeLayout) findViewById(R.id.inner0);
        profileImage = (ImageView) findViewById(R.id.image);

        names = new String[myRatings.size()];
        places = new String[myRatings.size()];
        tags = new String[myRatings.size()];
        comments = new String[myRatings.size()];
        ratings = new String[myRatings.size()];

        for(int i = 0; i < myRatings.size(); i++) {
            names[i] = user_name;
            places[i] = myRatings.get(i).getP_id();
            tags[i] = myRatings.get(i).getT_id();
            comments[i] = myRatings.get(i).getC_id();
            ratings[i] = myRatings.get(i).getRating();
        }

        votes = (ListView) findViewById(R.id.votes);
        ListRowAdapter listRowAdapter = new ListRowAdapter(this, images, names, places, tags, ratings);
        votes.setAdapter(listRowAdapter);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, temp);
        searchList = (ListView) findViewById(R.id.searchlist);
        searchView = (SearchView) findViewById(R.id.searchbox);
        searchList.setAdapter(arrayAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList.setVisibility(View.INVISIBLE);
                inner0.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText != null) {
                    searchList.setVisibility(View.VISIBLE);
                    inner0.setVisibility(View.INVISIBLE);
                } else {
                    searchList.setVisibility(View.INVISIBLE);
                    inner0.setVisibility(View.VISIBLE);
                }
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    //  Server connectıon
    class GetTimelineConn extends AsyncTask<String, Void, ArrayList<MyRating>>
    {
        ArrayList<MyRating> ratings = new ArrayList<MyRating>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MyRating> doInBackground(String... params) {
            String user_id = params[0];

            try {
                URL url = new URL("http://139.179.211.124:3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "GetPlacename");
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
                    String line, responseString;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    //responseString =responseString.substring(1, response.length() - 1);

                    JSONArray jsonarray = new JSONArray(responseString);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonParam = jsonarray.getJSONObject(i);
                        ratings.add(new MyRating(jsonParam.getString("placename"), jsonParam.getString("tagname"),
                                jsonParam.getString("content"), jsonParam.getString("rating")));
                    }

                    return ratings;
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
            return ratings;
        }

        @Override
        protected void onPostExecute(ArrayList<MyRating> ratings) {
            super.onPostExecute(ratings);
        }
    }

    //  Server connectıon
    class GetFriendConn extends AsyncTask<String, Void, Boolean>
    {
        private ImageView image;
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
            String user_id = params[0];

            try {
                URL url = new URL("http://139.179.211.124:3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
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
                jsonParam.put("friend_id", user_id);

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

                    byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                    decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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

            image = (ImageView) findViewById(R.id.image);
            image.setImageBitmap(decodedImage);
            name = (TextView) findViewById(R.id.name);
            name.setText(username);
            rating = (TextView) findViewById(R.id.rating);
            rating.setText("Rating : " + ratings);
        }
    }
}
