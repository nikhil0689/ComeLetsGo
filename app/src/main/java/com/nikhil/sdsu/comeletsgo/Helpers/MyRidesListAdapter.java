package com.nikhil.sdsu.comeletsgo.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nikhil.sdsu.comeletsgo.Pojo.MyRideDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.List;

/**
 * Created by nikhilc on 1/8/2018.
 */

public class MyRidesListAdapter extends ArrayAdapter implements ComeLetsGoConstants{

    private Activity context;
    private List<MyRideDetailsPOJO> userProperties;
    public MyRidesListAdapter(Activity context, int resource, List list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.my_rides_list_item, null);
        TextView source = view.findViewById(R.id.my_ride_source);
        TextView destination = view.findViewById(R.id.my_ride_destination);
        TextView date = view.findViewById(R.id.my_ride_date);
        TextView time = view.findViewById(R.id.my_ride_time);
        TextView uid = view.findViewById(R.id.my_ride_uid);
        TextView status = view.findViewById(R.id.my_ride_status);
        source.setText(userProperties.get(position).getSource());
        destination.setText(userProperties.get(position).getDestination());
        date.setText(userProperties.get(position).getDate());
        time.setText(userProperties.get(position).getTime());
        uid.setText(userProperties.get(position).getUid());
        if(userProperties.get(position).isApprovalStatus()){
            status.setText(RIDE_STATUS_COMPLETED);
        }else{
            status.setText(RIDE_SCHEDULED);
        }

        return view;
    }




}
