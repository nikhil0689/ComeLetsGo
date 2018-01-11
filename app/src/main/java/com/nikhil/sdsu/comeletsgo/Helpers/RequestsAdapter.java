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
import com.nikhil.sdsu.comeletsgo.Pojo.RequestDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.List;

/**
 * Created by nikhilc on 12/28/2017.
 */

public class RequestsAdapter extends ArrayAdapter implements ComeLetsGoConstants{
    private Activity context;
    private List<RequestDetailsPOJO> userProperties;
    public RequestsAdapter(Activity context, int resource, List list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.request_list_item, null);
        TextView requestorName = view.findViewById(R.id.request_list_requestor_name);
        TextView requestorContact = view.findViewById(R.id.request_list_requestor_contact);
        TextView status = view.findViewById(R.id.request_list_approval);
        requestorName.setText(userProperties.get(position).getRequestorName());
        requestorContact.setText(userProperties.get(position).getRequestorContact());
        if(userProperties.get(position).isApprovalStatus()){
            status.setText(REQUEST_STATUS_APPROVED);
        }else{
            status.setText(REQUEST_STATUS_WAITING);
        }

        return view;
    }
}
