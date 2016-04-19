package com.example.fkrt.tophelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

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
    private ProfilePictureView profileImage;
    private SearchView searchView;
    private ListView votes;
    private ListView searchList;

    private SharedPreferences sharedPref;
    private String u_id, user_name,fbID;
    private boolean isFB;

    ArrayList<Relation> relations;

    private String[] names, places, tags, comments, ratings, relationTimes, emails;

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
        isFB = sharedPref.getBoolean("isFB", false);
        fbID = sharedPref.getString("fbID", "N/A");

        try {
            relations = new GetTimelineConn().execute(u_id).get();
            boolean b = new GetFriendConn().execute(u_id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        inner0 = (RelativeLayout) findViewById(R.id.inner0);
        profileImage = (ProfilePictureView) findViewById(R.id.image);

        names = new String[relations.size()];
        places = new String[relations.size()];
        tags = new String[relations.size()];
        comments = new String[relations.size()];
        ratings = new String[relations.size()];
        relationTimes = new String[relations.size()];
        emails = new String[relations.size()];


        for(int i = 0; i < relations.size(); i++) {
            names[i] = user_name;
            places[i] = relations.get(i).getP_id();
            tags[i] = relations.get(i).getT_id();
            comments[i] = relations.get(i).getC_id();
            ratings[i] = relations.get(i).getRating();
            relationTimes[i] = relations.get(i).getRelationTime();
            emails[i] = relations.get(i).getEmail();
        }


        votes = (ListView) findViewById(R.id.votes);
        ListRowAdapter listRowAdapter = new ListRowAdapter(this, images, names, places, tags, ratings, relationTimes,emails);
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

                if (query.charAt(0) == '@') {
                    String f_id = null;
                    try {
                        f_id = new GetFriendIdConn().execute(query.substring(1)).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(getApplicationContext(), FriendActivity.class);
                    intent.putExtra("friend_id", f_id);
                    startActivity(intent);
                }

                return true;
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
    class GetTimelineConn extends AsyncTask<String, Void, ArrayList<Relation>>
    {
        ArrayList<Relation> relation = new ArrayList<Relation>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Relation> doInBackground(String... params) {
            String user_id = params[0];

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
                jsonParam.put("type", "GetRelation");
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
                        relation.add(new Relation(jsonParam.getString("username"), jsonParam.getString("placename"), jsonParam.getString("tagname"),
                                jsonParam.getString("content"), jsonParam.getString("rating"), jsonParam.getString("relationtime"), jsonParam.getString("email")));
                    }

                    return relation;
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
            return relation;
        }

        @Override
        protected void onPostExecute(ArrayList<Relation> relation) {
            super.onPostExecute(relation);
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
            String user_id = params[0];

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
    class GetFriendIdConn extends AsyncTask<String, Void, String> {
        int f_id = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String friend_name = params[0];

            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip) + ":3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "GetUserId");
                jsonParam.put("username", friend_name);

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
                    is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line, responseString;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    responseString = responseString.substring(1, response.length() - 1);

                    jsonParam = new JSONObject(responseString);
                    f_id = Integer.parseInt(jsonParam.getString("u_id"));

                } else {
                    is = conn.getErrorStream();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Integer.toString(f_id);
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
        }
    }
}
