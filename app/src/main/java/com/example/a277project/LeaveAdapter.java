package com.example.a277project;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class LeaveAdapter extends ArrayAdapter<Leave> {
    public LeaveAdapter(Context context, List<Leave> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.listview_leave,parent,false);
        }

        TextView line1 = (TextView) convertView.findViewById(R.id.item_username);
        TextView line2 = (TextView) convertView.findViewById(R.id.item_duration);
        TextView line3 = (TextView) convertView.findViewById(R.id.item_reason);
        TextView line4 = (TextView) convertView.findViewById(R.id.leave_state);

        Leave leave = getItem(position);

//        line1.setText(leave.getDateFrom() + "-" + leave.getDateTo());
        line1.setText(leave.getUsername());
        line2.setText(leave.getDateFrom() + "-" + leave.getDateTo());
        line3.setText(leave.getReason());

        if (leave.getApproved()) {
            line4.setText("Approved");
        } else {
            line4.setText("Pending");
        }


        return convertView;
    }


}
