package com.example.fkrt.tophelf;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by FKRT on 26.04.2016.
 */
public class ListFriendRowAdapter extends ArrayAdapter<String> {

    Intent intent;

    Context context;

    String[] names;
    String[] ids;
    String[] emails;

    public ListFriendRowAdapter(Context context, String[] names, String[] ids, String[] emails) {
        super(context, R.layout.single_friend_row, R.id.name, names);

        this.context = context;
        this.names = names;
        this.ids = ids;
        this.emails = emails;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.single_friend_row, parent, false);

        ProfilePictureView myImage = (ProfilePictureView) row.findViewById(R.id.image);
        TextView myName = (TextView) row.findViewById(R.id.name);

        if( !emails[position].contains("@") )
            myImage.setProfileId(emails[position]);

        myName.setText(names[position]);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, FriendActivity.class);
                intent.putExtra("friend_id", ids[position]);
                context.startActivity(intent);
            }
        });

        return row;
    }
}
