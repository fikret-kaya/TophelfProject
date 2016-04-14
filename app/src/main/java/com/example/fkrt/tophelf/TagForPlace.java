package com.example.fkrt.tophelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class TagForPlace extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Intent intent;

    Bundle bundle;
    private RelativeLayout inner0;
    private SearchView searchView;
    private TextView place, tag, rating, placeInfoV;
    private Button placeInfo, comments, map;
    private ListView commentsV, searchList;
    private ImageView mapV;

    String[] temp = {"#ankara", "#antalya", "#adana", "#bursa", "#istanbul", "#izmir", "#mersin", "#malatya", "#rize", "#erzurum"};
    String[] names = {"Name Surname 1", "Name Surname 2", "Name Surname 3", "Name Surname 4", "Name Surname 5", "Name Surname 6", "Name Surname 7", "Name Surname 8", "Name Surname 9", "Name Surname 10"};
    String[] places = {"Place 1", "Place 2", "Place 3", "Place 4", "Place 5", "Place 6", "Place 7", "Place 8", "Place 9", "Place 10"};
    String[] tags = {"Tag 1", "Tag 2", "Tag 3", "Tag 4", "Tag 5", "Tag 6", "Tag 7", "Tag 8", "Tag 9", "Tag 10"};
    String[] ratings = {"3/5", "4/5", "5/5", "4/5", "3/5", "3/5", "1/5", "4/5", "2/5", "4/5"};
    int[] images = {R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo,
            R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo};

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        String nn = bundle.getString("name");
        String pp = bundle.getString("place");
        String tt = bundle.getString("tag");
        String rr = bundle.getString("rating");
        setTitle(nn);

        setContentView(R.layout.activity_tag_for_place);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        inner0 = (RelativeLayout) findViewById(R.id.inner0);

        place = (TextView) findViewById(R.id.place);
        place.setText(pp);
        tag = (TextView) findViewById(R.id.tag);
        tag.setText(tt);
        rating = (TextView) findViewById(R.id.rating);
        rating.setText("Overall : " + rr);

        placeInfo = (Button) findViewById(R.id.placeinfo);
        comments = (Button) findViewById(R.id.comments);
        comments.setTextColor(Color.parseColor("#2D96C4"));
        comments.setTypeface(null, Typeface.BOLD);
        map = (Button) findViewById(R.id.map);

        placeInfoV = (TextView) findViewById(R.id.placeinfoV);
        commentsV = (ListView) findViewById(R.id.commentsV);
        CommentsListRowAdapter listRowAdapter = new CommentsListRowAdapter(this, images, names, places, tags, ratings);
        commentsV.setAdapter(listRowAdapter);
        mapV = (ImageView) findViewById(R.id.mapV);

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

    // Place Info
    public void onClick(View v) {
        placeInfoV.setVisibility(View.VISIBLE);
        placeInfo.setEnabled(false);
        commentsV.setVisibility(View.INVISIBLE);
        comments.setEnabled(true);
        mapV.setVisibility(View.INVISIBLE);
        map.setEnabled(true);

        placeInfo.setTextColor(Color.parseColor("#2D96C4"));
        placeInfo.setTypeface(null, Typeface.BOLD);
        comments.setTextColor(Color.parseColor("#7cc3e1"));
        comments.setTypeface(null, Typeface.NORMAL);
        map.setTextColor(Color.parseColor("#7cc3e1"));
        map.setTypeface(null, Typeface.NORMAL);
    }
    // Comments
    public void onClick2(View v) {
        placeInfoV.setVisibility(View.INVISIBLE);
        placeInfo.setEnabled(true);
        commentsV.setVisibility(View.VISIBLE);
        comments.setEnabled(false);
        mapV.setVisibility(View.INVISIBLE);
        map.setEnabled(true);

        placeInfo.setTextColor(Color.parseColor("#7cc3e1"));
        placeInfo.setTypeface(null, Typeface.NORMAL);
        comments.setTextColor(Color.parseColor("#2D96C4"));
        comments.setTypeface(null, Typeface.BOLD);
        map.setTextColor(Color.parseColor("#7cc3e1"));
        map.setTypeface(null, Typeface.NORMAL);

    }
    // Map
    public void onClick3(View v) {
       /* placeInfoV.setVisibility(View.INVISIBLE);
        placeInfo.setEnabled(true);
        commentsV.setVisibility(View.INVISIBLE);
        comments.setEnabled(true);
        mapV.setVisibility(View.VISIBLE);
        map.setEnabled(false);

        placeInfo.setTextColor(Color.parseColor("#7cc3e1"));
        placeInfo.setTypeface(null, Typeface.NORMAL);
        comments.setTextColor(Color.parseColor("#7cc3e1"));
        comments.setTypeface(null, Typeface.NORMAL);
        map.setTextColor(Color.parseColor("#2D96C4"));
        map.setTypeface(null, Typeface.BOLD);*/

        intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tag_for_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_votesComments) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_helpfeedback) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

class CommentsListRowAdapter extends ArrayAdapter<String> {

    Intent intent;

    Context context;
    int[] images;
    String[] names;
    String[] places;
    String[] tags;
    String[] ratings;
    CommentsListRowAdapter(Context context, int images[], String[] names, String[] places, String[] tags, String[] ratings) {
        super(context, R.layout.single_row, R.id.place, places);
        this.context = context;
        this.images = images;
        this.names = names;
        this.places = places;
        this.tags = tags;
        this.ratings = ratings;
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

        ImageView myImage = (ImageView) row.findViewById(R.id.image);
        TextView myName = (TextView) row.findViewById(R.id.name);
        TextView myPlace = (TextView) row.findViewById(R.id.place);
        TextView myTag = (TextView) row.findViewById(R.id.tag);
        TextView myRating = (TextView) row.findViewById(R.id.rating);
        final Button myMinus = (Button) row.findViewById(R.id.minus);
        final Button myPlus = (Button) row.findViewById(R.id.plus);

        myImage.setImageResource(images[position]);
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
                intent = new Intent(context, TagForPlace.class);
                intent.putExtra("name", nn);
                intent.putExtra("place", pp);
                intent.putExtra("tag", tt);
                intent.putExtra("rating", rr);
                context.startActivity(intent);
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
}