package com.example.a277project;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StoreAdapter extends ArrayAdapter<Store> {
    public StoreAdapter(Context context, List<Store> object){
        super(context,0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.store_layout,parent,false);
        }

        TextView storeidTextView = (TextView) convertView.findViewById(R.id.storeID);
        TextView storenameTextView = (TextView) convertView.findViewById(R.id.storeName);
        TextView latitudeTextView = (TextView) convertView.findViewById(R.id.latitude);
        TextView longitudeTextView = (TextView) convertView.findViewById(R.id.longitude);
        TextView employeeTextView = (TextView) convertView.findViewById(R.id.employee);

        Store store = getItem(position);

        storeidTextView.setText(store.getId());
        storenameTextView.setText(store.getName());
        latitudeTextView.setText(store.getLatitude());
        longitudeTextView.setText(store.getLongitude());
        employeeTextView.setText(store.getEmployee().toString());
//        locationTextView.setText(user.getLocation());
//        worktimeTextView.setText(user.getWorktime());


        return convertView;
    }
}
