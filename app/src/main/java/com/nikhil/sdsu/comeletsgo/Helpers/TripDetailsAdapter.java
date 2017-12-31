package com.nikhil.sdsu.comeletsgo.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nikhil.sdsu.comeletsgo.Pojo.AddTripDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.List;

/**
 * Created by nikhilc on 12/28/2017.
 */

public class TripDetailsAdapter extends ArrayAdapter {
    private Activity context;
    private List<AddTripDetailsPOJO> userProperties;
    public TripDetailsAdapter(Activity context, int resource, List list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.trip_list_item, null);
        TextView destination = view.findViewById(R.id.trip_list_destination);
        TextView source = view.findViewById(R.id.trip_list_source);
        TextView date = view.findViewById(R.id.trip_list_date);
        TextView time = view.findViewById(R.id.trip_list_time);
        TextView seats = view.findViewById(R.id.trip_list_seats);
        TextView name = view.findViewById(R.id.trip_list_name);
        TextView contact = view.findViewById(R.id.trip_list_contact);
        TextView car = view.findViewById(R.id.trip_list_car);
        TextView color = view.findViewById(R.id.trip_list_color);
        TextView license = view.findViewById(R.id.trip_list_license);
        source.setText(""+userProperties.get(position).getSource().concat(" to "));
        destination.setText(""+userProperties.get(position).getDestination());
        date.setText(""+userProperties.get(position).getDate().concat(" - "));
        time.setText(""+userProperties.get(position).getTime());
        seats.setText("Seats Available: "+userProperties.get(position).getSeatsAvailable());
        name.setText("Posted By: "+userProperties.get(position).getPostedBy());
        contact.setText(" Contact: "+userProperties.get(position).getContact());
        car.setText(" Car: "+userProperties.get(position).getCar());
        color.setText(" Color: "+userProperties.get(position).getCarColor());
        license.setText(" License: "+userProperties.get(position).getLicense());
        return view;
    }
}
