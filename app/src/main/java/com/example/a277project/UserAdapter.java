package com.example.a277project;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, List<User> object){
        super(context,0, object);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.user_layout,parent,false);
        }

        TextView usernameTextView = (TextView) convertView.findViewById(R.id.username);
        TextView roleTextView = (TextView) convertView.findViewById(R.id.role);
        TextView locationTextView = (TextView) convertView.findViewById(R.id.location);
        TextView worktimeTextView = (TextView) convertView.findViewById(R.id.worktime);
        TextView workshiftTextView = (TextView) convertView.findViewById(R.id.workshift);

        User user = getItem(position);

        usernameTextView.setText(user.getusername());
        roleTextView.setText(user.getrole());
        locationTextView.setText(user.getLocation());
        worktimeTextView.setText(user.getWorkday());
        workshiftTextView.setText(user.getShift());


        return convertView;
    }
}
