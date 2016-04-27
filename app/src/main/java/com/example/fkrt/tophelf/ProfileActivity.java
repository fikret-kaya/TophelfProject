package com.example.fkrt.tophelf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Intent intent;

    private RelativeLayout inner0;
    private ProfilePictureView profileImage;
    private SearchView searchView;
    private TextView friendsCount;
    private ListView votes, userSearchList, placeTagSearchList;

    private SharedPreferences sharedPref;
    private String u_id, user_name, fbID, numOfFriends;
    private boolean isFB;

    ArrayList<Relation> relations;
    ArrayList<Friend> friends;
    ArrayList<Place> placesSearched;
    ArrayList<Tag> tagsSearched;

    private String[] names, ids, places, tags, comments, ratings, relationTimes, emails, relation_ids;

    String[] temp = {"#ankara", "#antalya", "#adana", "#bursa", "#istanbul", "#izmir", "#mersin", "#malatya", "#rize", "#erzurum"};
    int images = R.drawable.logo64;

    private ListFriendRowAdapter listFriendRowAdapter;
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
                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                }else {
                    intent = new Intent(view.getContext(), VoteActivity.class);
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        u_id = sharedPref.getString("u_id", "N/A");
        user_name = sharedPref.getString("name", "N/A");
        isFB = sharedPref.getBoolean("isFB", false);
        fbID = sharedPref.getString("fbID", "N/A");
        numOfFriends = sharedPref.getString("numOfFriends", "N/A");

        friendsCount = (TextView) findViewById(R.id.friendsCount);
        friendsCount.setText("Number of Friends : " + numOfFriends);

        setTitle("Profile");

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
        ids = new String[relations.size()];
        places = new String[relations.size()];
        tags = new String[relations.size()];
        comments = new String[relations.size()];
        ratings = new String[relations.size()];
        relationTimes = new String[relations.size()];
        emails = new String[relations.size()];
        relation_ids = new String[relations.size()];

        for(int i = 0; i < relations.size(); i++) {
            names[i] = user_name;
            ids[i] = relations.get(i).getU_id();
            places[i] = relations.get(i).getP_id();
            tags[i] = relations.get(i).getT_id();
            comments[i] = relations.get(i).getC_id();
            ratings[i] = relations.get(i).getRating();
            relationTimes[i] = relations.get(i).getRelationTime();
            emails[i] = relations.get(i).getEmail();
            relation_ids = new String[relations.size()];
        }

        String[] ranksArr = {"profile_activity"};

        votes = (ListView) findViewById(R.id.votes);
        ListRowAdapter listRowAdapter = new ListRowAdapter(this, images, names, ids, places, tags, comments,
                                                                ratings, relationTimes, emails, relation_ids, ranksArr);
        votes.setAdapter(listRowAdapter);

        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, temp);
        placeTagSearchList = (ListView) findViewById(R.id.searchlist2);
        userSearchList = (ListView) findViewById(R.id.searchlist);
        searchView = (SearchView) findViewById(R.id.searchbox);
        //userSearchList.setAdapter(arrayAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(getApplicationContext(), "No one found!", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.equals("")) {
                    inner0.setVisibility(View.INVISIBLE);
                    userSearchList.setVisibility(View.INVISIBLE);
                    placeTagSearchList.setVisibility(View.INVISIBLE);

                    if (newText.charAt(0) == '@') { // Search User
                        userSearchList.setVisibility(View.VISIBLE);
                        String f_id = null;
                        try {
                            friends = new GetUsersSearchedConn().execute(newText.substring(1)).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        String[] unames = new String[friends.size()];
                        final String[] uids = new String[friends.size()];
                        String[] umails = new String[friends.size()];
                        for(int i = 0; i < friends.size(); i++) {
                            unames[i] = friends.get(i).getName();
                            uids[i] = friends.get(i).getId();
                            umails[i] = friends.get(i).getEmail();
                        }
                        listFriendRowAdapter = new ListFriendRowAdapter(getBaseContext(), unames, uids, umails);
                        userSearchList.setAdapter(listFriendRowAdapter);

                    } else if (newText.charAt(0) == '$') { // Search Place
                        placeTagSearchList.setVisibility(View.VISIBLE);
                        try {
                            placesSearched = new GetPlacesSearchedConn().execute(newText.substring(1)).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        String[] placenames = new String[placesSearched.size()];
                        for(int i = 0; i < placesSearched.size(); i++) {
                            placenames[i] = placesSearched.get(i).getName();
                        }
                        arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,placenames);
                        placeTagSearchList.setAdapter(arrayAdapter);

                        placeTagSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                intent = new Intent(getBaseContext(), TagForPlaceActivity.class);
                                intent.putExtra("place", placesSearched.get(position).getName());
                                intent.putExtra("placeID", placesSearched.get(position).getId());
                                intent.putExtra("placeInfo", placesSearched.get(position).getInfo());
                                intent.putExtra("placeLoc", placesSearched.get(position).getLoc());
                                startActivity(intent);
                            }
                        });
                    } else if (newText.charAt(0) == '#') { // Search Tag
                        placeTagSearchList.setVisibility(View.VISIBLE);
                        try {
                            tagsSearched = new GetTagsSearchedConn().execute(newText.substring(1)).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        String[] tagnames = new String[tagsSearched.size()];
                        for(int i = 0; i < tagsSearched.size(); i++) {
                            tagnames[i] = tagsSearched.get(i).getName();
                        }
                        arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,tagnames);
                        placeTagSearchList.setAdapter(arrayAdapter);

                        placeTagSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                intent = new Intent(getBaseContext(), PlaceForTagActivity.class);
                                intent.putExtra("tag", tagsSearched.get(position).getName());
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    userSearchList.setVisibility(View.INVISIBLE);
                    inner0.setVisibility(View.VISIBLE);
                    placeTagSearchList.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        View hView = navigationView.getHeaderView(0);
        TextView name = (TextView) hView.findViewById(R.id.name);
        name.setText(user_name);

        ProfilePictureView imgvw = (ProfilePictureView) hView.findViewById(R.id.profilePicture);
        if (isFB) {
            imgvw.setProfileId(fbID);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_friends) {
            intent = new Intent(this, FriendsListActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_votesComments) {
            intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_settings) {
            if(!isFB) {
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
            }
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
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                jsonParam.put("type", "GetRelations");
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
                        relation.add(new Relation(jsonParam.getString("username"), jsonParam.getString("u_id"), jsonParam.getString("placename"), jsonParam.getString("tagname"),
                                    jsonParam.getString("content"), jsonParam.getString("rating"), jsonParam.getString("relationtime"),
                                                                        jsonParam.getString("email"), jsonParam.getString("r_id")));
                    }

                    conn.disconnect();

                    return relation;
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
                conn.disconnect();

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
            if(!email.contains("@"))
                image.setProfileId(email);

            name = (TextView) findViewById(R.id.name);
            name.setText(username);
            rating = (TextView) findViewById(R.id.rating);
            rating.setText("Rating : " + ratings);
        }
    }

    //  Server connectıon
    class GetUsersSearchedConn extends AsyncTask<String, Void, ArrayList<Friend>>
    {
        ArrayList<Friend> users = new ArrayList<Friend>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Friend> doInBackground(String... params) {
            String name = params[0];

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
                jsonParam.put("type", "GetUsersSearched");
                jsonParam.put("username", name);

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
                        users.add(new Friend(jsonParam.getString("username"), jsonParam.getString("u_id"), jsonParam.getString("email")));
                    }

                    conn.disconnect();

                    return users;
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
            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            super.onPostExecute(friends);
        }
    }

    //  Server connectıon
    class GetPlacesSearchedConn extends AsyncTask<String, Void, ArrayList<Place>> {

        ArrayList<Place> places = new ArrayList<Place>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Place> doInBackground(String... params) {
            String placename = params[0];

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
                jsonParam.put("type", "GetPlacesSearched");
                jsonParam.put("placename", placename);

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
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    //responseString = responseString.substring(1, response.length() - 1);

                    JSONArray jsonarray = new JSONArray(responseString);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonParam = jsonarray.getJSONObject(i);
                        places.add(new Place(jsonParam.getString("placename"), jsonParam.getString("p_id"),
                                jsonParam.getString("info"), jsonParam.getString("location")));
                    }

                } else {
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
            return places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            super.onPostExecute(places);
        }
    }

    //  Server connectıon
    class GetTagsSearchedConn extends AsyncTask<String, Void, ArrayList<Tag>> {

        ArrayList<Tag> tags = new ArrayList<Tag>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Tag> doInBackground(String... params) {
            String tagname = params[0];

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
                jsonParam.put("type", "GetTagsSearched");
                jsonParam.put("tagname", tagname);

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
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    responseString = response.toString();
                    //responseString = responseString.substring(1, response.length() - 1);

                    JSONArray jsonarray = new JSONArray(responseString);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonParam = jsonarray.getJSONObject(i);
                        tags.add(new Tag(jsonParam.getString("tagname"), jsonParam.getString("t_id"),
                                jsonParam.getString("stamp")));
                    }

                } else {
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
            return tags;
        }

        @Override
        protected void onPostExecute(ArrayList<Tag> tags) {
            super.onPostExecute(tags);
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
                conn.disconnect();

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
