package com.example.fkrt.tophelf;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by FKRT on 20.04.2016.
 */
public class ListRowAdapter extends ArrayAdapter<String> {

    Intent intent;

    Context context;
    int images;
    String[] names;
    String[] places;
    String[] tags;
    String[] ratings;
    String[] relationTimes;
    String[] emails;

    ListRowAdapter(Context context, int images, String[] names, String[] places, String[] tags, String[] ratings, String[] relationTimes, String[] emails) {
        super(context, R.layout.single_row, R.id.place, places);
        this.context = context;
        this.images = images;
        this.names = names;
        this.places = places;
        this.tags = tags;
        this.ratings = ratings;
        this.relationTimes = relationTimes;
        this.emails = emails;
    }

    /*placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), "fikret", Toast.LENGTH_LONG);
        }
    });*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.single_row, parent, false);

        ProfilePictureView myImage = (ProfilePictureView) row.findViewById(R.id.image);
        TextView myName = (TextView) row.findViewById(R.id.name);
        TextView myPlace = (TextView) row.findViewById(R.id.place);
        TextView myTag = (TextView) row.findViewById(R.id.tag);
        TextView myRating = (TextView) row.findViewById(R.id.rating);
        final Button myMinus = (Button) row.findViewById(R.id.minus);
        final Button myPlus = (Button) row.findViewById(R.id.plus);

        if( emails[position].contains("@")){
            myImage.setProfileId("10209196878817858");
        }else {
            myImage.setProfileId(emails[position]);
        }

        myName.setText(names[position]);
        myPlace.setText(places[position]);
        myTag.setText(tags[position]);
        myRating.setText(ratings[position]);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nn = names[position];
                String pp = places[position];
                String tt = tags[position];
                String rr = ratings[position];
                String p_id = null, p_info = null, p_loc = null;

                String placeIDinfoArray[] = null;
                try {
                    placeIDinfoArray = new GetPlaceIDinfo().execute(pp).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                p_id = placeIDinfoArray[0];
                p_info = placeIDinfoArray[1];
                p_loc = placeIDinfoArray[2];

                if(p_id != null) {
                    intent = new Intent(context, TagForPlace.class);
                    intent.putExtra("name", nn);
                    intent.putExtra("place", pp);
                    intent.putExtra("placeID", p_id);
                    intent.putExtra("placeInfo", p_info);
                    intent.putExtra("placeLoc", p_loc);
                    intent.putExtra("tag", tt);
                    intent.putExtra("rating", rr);
                    context.startActivity(intent);
                }
            }
        });

        myMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMinus.setBackgroundResource(R.drawable.minusf);
                myPlus.setBackgroundResource(R.drawable.pluse);
            }
        });

        myPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPlus.setBackgroundResource(R.drawable.plusf);
                myMinus.setBackgroundResource(R.drawable.minuse);
            }
        });

        return row;
    }

    //  Server connectıon
    class GetPlaceIDinfo extends AsyncTask<String, Void, String[]>
    {
        ArrayList<Relation> relation = new ArrayList<Relation>();
        String returns[] = new String[3];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String p_name = params[0];

            try {
                URL url = new URL("http://"+context.getString(R.string.ip)+":3000/"); // 192.168.1.24 --- 10.0.2.2 --- 139.179.211.68
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "GetPlace");
                jsonParam.put("placename", p_name);

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
                    responseString =responseString.substring(1, response.length() - 1);

                    jsonParam = new JSONObject(responseString);
                    returns[0] = jsonParam.getString("p_id");
                    returns[1] = jsonParam.getString("info");
                    returns[2] = jsonParam.getString("location");
                    conn.disconnect();

                    return returns;
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
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
        }
    }

}
