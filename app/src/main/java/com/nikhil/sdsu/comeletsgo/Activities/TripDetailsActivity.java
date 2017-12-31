package com.nikhil.sdsu.comeletsgo.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.sdsu.comeletsgo.Helpers.RequestsAdapter;
import com.nikhil.sdsu.comeletsgo.Pojo.AddTripDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.RequestDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.Collections;

public class TripDetailsActivity extends AppCompatActivity {
    private TextView source,destination,date,time,seats,poster,posterContact,car,carColor,license;
    private Button back,join;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle intent = getIntent().getExtras();
        AddTripDetailsPOJO addTripDetails = (AddTripDetailsPOJO) intent.getSerializable("tripDetailsFromPoster");
        Log.d("rew","Contact from Poster: "+addTripDetails.getContact());
        final String phNo = auth.getCurrentUser().getDisplayName().toString();
        Log.d("rew","phno: "+phNo);
        source = findViewById(R.id.trip_source);
        destination = findViewById(R.id.trip_destination);
        date = findViewById(R.id.trip_date);
        time = findViewById(R.id.trip_time);
        seats = findViewById(R.id.trip_seats);
        poster = findViewById(R.id.trip_poster);
        posterContact = findViewById(R.id.trip_contact);
        car = findViewById(R.id.trip_car);
        carColor = findViewById(R.id.trip_car_color);
        license = findViewById(R.id.trip_car_license);
        back = findViewById(R.id.trip_cancel_button);
        join = findViewById(R.id.trip_join_button);
        source.setText(addTripDetails.getSource().concat(" TO "));
        destination.setText(addTripDetails.getDestination());
        date.setText(addTripDetails.getDate().concat(" TIME: "));
        time.setText(addTripDetails.getTime());
        seats.setText("Seats Available: "+addTripDetails.getSeatsAvailable());
        poster.setText(addTripDetails.getPostedBy());
        posterContact.setText(addTripDetails.getContact());
        Log.d("rew","contact from cloud: "+posterContact.getText().toString());
        car.setText(addTripDetails.getCar().concat(" COLOR: "));
        carColor.setText(addTripDetails.getCarColor());
        license.setText(addTripDetails.getLicense());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        RequestDetailsPOJO requestDetailsPOJO = msgSnapshot.getValue(RequestDetailsPOJO.class);
                        if(requestDetailsPOJO.isApprovalStatus()){
                            Log.d("rew","Approved for: "+requestDetailsPOJO.getRequestorContact());
                            LinearLayout linearLayout = findViewById(R.id.text_programatically);
                            TextView textView = new TextView(TripDetailsActivity.this);
                            textView.setText(requestDetailsPOJO.getRequestorContact().concat(" joined"));
                            linearLayout.addView(textView);
                            if(phNo.equalsIgnoreCase(requestDetailsPOJO.getRequestorContact())){
                                join.setEnabled(false);
                            }
                        }
                        Log.d("rew", requestDetailsPOJO.getRequestorName());
                    }
                } else {
                    Log.d("rew","No Data yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("requests").child(posterContact.getText().toString());
        people.addValueEventListener(valueEventListener);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(phNo.equals(posterContact.getText().toString())){
            join.setEnabled(false);
            Log.d("rew","Join Disabled");

        }else{
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Push notification
                    Log.d("rew","Different contact");
                    RequestDetailsPOJO requestDetails = new RequestDetailsPOJO();
                    requestDetails.setRequestorName(auth.getCurrentUser().getEmail().toString());
                    requestDetails.setRequestorContact(phNo);
                    requestDetails.setPosterName(poster.getText().toString());
                    requestDetails.setPosterContact(posterContact.getText().toString());
                    try{
                        mDatabase.child("requests").child(posterContact.getText().toString()).child(phNo).setValue(requestDetails);
                        Log.d("rew","request data submitted");
                        finish();
                    }catch (Exception e){
                        Log.d("rew","Exception: "+e);
                    }
                }
            });
        }


    }
}
