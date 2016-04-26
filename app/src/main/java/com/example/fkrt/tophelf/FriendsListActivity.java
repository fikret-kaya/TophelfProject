package com.example.fkrt.tophelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

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

public class FriendsListActivity extends AppCompatActivity {

    private Intent intent;
    private Bundle bundle;
    private ListView friendList;
    private String user_name;
    private String u_id;
    private String fbID;
    private boolean isFB;
    private SharedPreferences sharedPref;

    private String[] names, ids, emails;

    ArrayList<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //shared pref
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isFB = sharedPref.getBoolean("isFB", false);
        user_name = sharedPref.getString("name", "N/A");
        fbID = sharedPref.getString("fbID", "N/A");
        u_id = sharedPref.getString("u_id", "N/A");

        setTitle("Friend List");

        try {
            friends = new GetFriendsConn().execute(u_id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        names = new String[friends.size()];
        ids = new String[friends.size()];
        emails = new String[friends.size()];

        for(int i = 0; i < friends.size(); i++) {
            names[i] = friends.get(i).getName();
            ids[i] = friends.get(i).getId();
            emails[i] = friends.get(i).getEmail();
        }

        friendList = (ListView) findViewById(R.id.friendsList);
        ListFriendRowAdapter listFriendRowAdapter = new ListFriendRowAdapter(this, names, ids, emails);
        friendList.setAdapter(listFriendRowAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(view.getContext(), VoteActivity.class);
                startActivity(intent);
            }
        });
    }

    //  Server connectıon
    class GetFriendsConn extends AsyncTask<String, Void, ArrayList<Friend>>
    {
        ArrayList<Friend> friends= new ArrayList<Friend>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Friend> doInBackground(String... params) {
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
                jsonParam.put("type", "GetMyFriends");
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
                        friends.add(new Friend(jsonParam.getString("username"), jsonParam.getString("u_id"), jsonParam.getString("email")));
                    }

                    conn.disconnect();

                    return friends;
                }
                else {
                    is = conn.getErrorStream();
                }
                conn.disconnect();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return friends;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            super.onPostExecute(friends);
        }
    }

}
