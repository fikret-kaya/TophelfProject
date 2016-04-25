package com.example.fkrt.tophelf;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.playlog.internal.LogEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class VoteActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private Intent intent;
    private double latitude, longitude;
    private SharedPreferences sharedPref;
    private String u_id;
    private String fbID;
    private boolean isFB;
    private EditText place,tag,comment;
    private RatingBar rating;
    private String placeRate;
    private ProgressDialog progress;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isFB = sharedPref.getBoolean("isFB", false);
        fbID = sharedPref.getString("fbID", "N/A");
        u_id = sharedPref.getString("u_id", "N/A");

        setTitle("Vote");

        place = (EditText) findViewById(R.id.placeInfo);
        tag = (EditText) findViewById(R.id.tagInfo);
        comment = (EditText) findViewById(R.id.editText);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                placeRate = String.valueOf(rating);
            }
        });

        LocationManager lm = (LocationManager) getSystemService(getBaseContext().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null)
            myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (myLocation == null)
            myLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        latitude = myLocation.getLatitude();
        longitude = myLocation.getLongitude();
    }

    // vote
    public void onClick(View v) throws ExecutionException, InterruptedException {
        Toast.makeText(getApplicationContext(), "Thanks Your For Your Contribution", Toast.LENGTH_LONG).show();

        vote();
        //boolean b = new Voteconn().execute(u_id,latitude+ "",longitude+"",place.getText().toString(),tag.getText().toString(),comment.getText().toString(), placeRate).get();

       // intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void vote() {
        progress = ProgressDialog.show(this, "Please wait !",
                "Updating...", true);
        new Thread() {
            public void run() {
                try {
                    //Your upgrade method !
                    boolean b = new Voteconn().execute(u_id,latitude+ "",longitude+"",place.getText().toString(),tag.getText().toString(),comment.getText().toString(), placeRate).get();
                } catch (Exception e) {
                }
                progress.dismiss();
            }
        }.start();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_votesComments) {
            intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_helpfeedback) {

        } else if (id == R.id.nav_logout) {
            if (isFB) {
                LoginManager.getInstance().logOut();
            } else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isLogin", false);
                editor.commit();
            }
            intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class Voteconn extends AsyncTask<Object, Void, Boolean>
    {
        private int p_id, t_id, c_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress = ProgressDialog.show(context, "Voting", "Loading...", true, false);
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            String u_id = (String) params[0];
            String latitude = (String) params[1];
            String longitude = (String) params[2];
            String placeName = (String) params[3];
            String tagName = (String) params[4];
            String comment = (String) params[5];
            String rating = (String) params[6];

            try {
                // Place Request
                URL url = new URL("http://"+getResources().getString(R.string.ip)+":3000"); // 192.168.1.24 --- 10.0.2.2
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "Place");
                jsonParam.put("location", latitude+"-"+longitude);
                jsonParam.put("placename", placeName);
                jsonParam.put("info", comment);

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
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    responseString =responseString.substring(1, response.length() - 1);

                    jsonParam = new JSONObject(responseString);
                    p_id = Integer.parseInt(jsonParam.getString("p_id"));
                    conn.disconnect();

                    // Tag Request
                    HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
                    conn2.setReadTimeout(10000);
                    conn2.setConnectTimeout(15000);
                    conn2.setRequestMethod("POST");
                    conn2.setDoInput(true);
                    conn2.setDoOutput(true);

                    conn2.setRequestProperty("Content-Type", "application/json");
                    conn2.connect();

                    jsonParam = new JSONObject();
                    jsonParam.put("type", "Tag");
                    jsonParam.put("tagname", tagName);

                    OutputStream os2 = conn2.getOutputStream();
                    BufferedWriter writer2 = new BufferedWriter(
                            new OutputStreamWriter(os2, "UTF-8"));
                    writer2.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                    writer2.flush();
                    writer2.close();
                    os2.close();

                    statusCode = conn2.getResponseCode();
                    is = null;

                    if (statusCode >= 200 && statusCode < 400) {
                        is = conn2.getInputStream();
                        BufferedReader rd2 = new BufferedReader(new InputStreamReader(is));
                        StringBuffer response2 = new StringBuffer();
                        while((line = rd2.readLine()) != null) {
                            response2.append(line);
                        }
                        rd2.close();
                        responseString = response2.toString();
                        responseString =responseString.substring(1, response2.length() - 1);

                        jsonParam = new JSONObject(responseString);
                        t_id = Integer.parseInt(jsonParam.getString("t_id"));
                        conn2.disconnect();

                        // Comment Request
                        HttpURLConnection conn3 = (HttpURLConnection) url.openConnection();
                        conn3.setReadTimeout(10000);
                        conn3.setConnectTimeout(15000);
                        conn3.setRequestMethod("POST");
                        conn3.setDoInput(true);
                        conn3.setDoOutput(true);

                        conn3.setRequestProperty("Content-Type", "application/json");
                        conn3.connect();

                        jsonParam = new JSONObject();
                        jsonParam.put("type", "Comment");
                        jsonParam.put("comment", comment);

                        OutputStream os3 = conn3.getOutputStream();
                        BufferedWriter writer3 = new BufferedWriter(
                                new OutputStreamWriter(os3, "UTF-8"));
                        writer3.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                        writer3.flush();
                        writer3.close();
                        os3.close();

                        statusCode = conn3.getResponseCode();
                        is = null;

                        if (statusCode >= 200 && statusCode < 400) {
                            is = conn3.getInputStream();
                            BufferedReader rd3 = new BufferedReader(new InputStreamReader(is));
                            StringBuffer response3 = new StringBuffer();
                            while((line = rd3.readLine()) != null) {
                                response3.append(line);
                            }
                            rd3.close();
                            responseString = response3.toString();
                            responseString =responseString.substring(1, response3.length() - 1);

                            jsonParam = new JSONObject(responseString);
                            c_id = Integer.parseInt(jsonParam.getString("c_id"));
                            conn3.disconnect();

                            HttpURLConnection conn4 = (HttpURLConnection) url.openConnection();
                            conn4.setReadTimeout(10000);
                            conn4.setConnectTimeout(15000);
                            conn4.setRequestMethod("POST");
                            conn4.setDoInput(true);
                            conn4.setDoOutput(true);

                            conn4.setRequestProperty("Content-Type", "application/json");
                            conn4.connect();

                            jsonParam = new JSONObject();
                            jsonParam.put("type", "Vote");
                            jsonParam.put("u_id", u_id);
                            jsonParam.put("p_id", p_id);
                            jsonParam.put("t_id", t_id);
                            jsonParam.put("c_id", c_id);
                            jsonParam.put("rating", rating);

                            OutputStream os4 = conn4.getOutputStream();
                            BufferedWriter writer4 = new BufferedWriter(
                                    new OutputStreamWriter(os4, "UTF-8"));
                            writer4.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                            writer4.flush();
                            writer4.close();
                            os4.close();

                            statusCode = conn4.getResponseCode();
                            Log.e("taggg", "adadasd" + statusCode);
                            is = null;

                            if (statusCode >= 200 && statusCode < 400) {

                                intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                conn4.disconnect();

                                return true;

                            } else {
                                is = conn4.getErrorStream();
                            }
                            conn4.disconnect();

                        } else {
                            is = conn3.getErrorStream();
                        }
                    } else {
                        is = conn2.getErrorStream();
                    }
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

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //progress.dismiss();
        }
    }

}