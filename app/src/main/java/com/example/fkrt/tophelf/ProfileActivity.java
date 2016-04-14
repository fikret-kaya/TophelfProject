package com.example.fkrt.tophelf;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

public class ProfileActivity extends AppCompatActivity {

    private Intent intent;

    private RelativeLayout inner0;
    private ImageView profileImage;
    private SearchView searchView;
    private ListView votes;
    private ListView searchList;

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

        inner0 = (RelativeLayout) findViewById(R.id.inner0);

        profileImage = (ImageView) findViewById(R.id.image);

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
}
