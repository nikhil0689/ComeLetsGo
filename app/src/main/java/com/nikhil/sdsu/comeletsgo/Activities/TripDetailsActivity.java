package com.nikhil.sdsu.comeletsgo.Activities;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
    private Button back,join,delete;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String requestorName="";
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
        delete = findViewById(R.id.trip_delete_button);
        source.setText(addTripDetails.getSource());
        destination.setText(addTripDetails.getDestination());
        date.setText(addTripDetails.getDate());
        time.setText(addTripDetails.getTime());
        seats.setText(""+addTripDetails.getSeatsAvailable());
        poster.setText(addTripDetails.getPostedBy());
        posterContact.setText(addTripDetails.getContact());
        Log.d("rew","contact from cloud: "+posterContact.getText().toString());
        car.setText(addTripDetails.getCar());
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
                            TextView passengers1 = new TextView(TripDetailsActivity.this);
                            passengers1.setText("People in the Ride");
                            passengers1.setGravity(Gravity.CENTER);
                            linearLayout.addView(passengers1);
                            TextView passengers = new TextView(TripDetailsActivity.this);
                            passengers.setText(requestDetailsPOJO.getRequestorName());
                            passengers.setGravity(Gravity.CENTER);
                            passengers.setTypeface(Typeface.DEFAULT_BOLD);
                            linearLayout.addView(passengers);
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
            Log.d("rew","phno: "+phNo);
            Log.d("rew","poster contact: "+posterContact.getText().toString());
            Log.d("rew","Join Disabled");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("rew","delete onclick");
                    AlertDialog.Builder alert = new AlertDialog.Builder(TripDetailsActivity.this);
                    alert.setTitle("Delete");
                    alert.setMessage("Delete Ride");
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDatabase.child("requests").child(posterContact.getText().toString()).removeValue();
                            mDatabase.child("trip_details").child(posterContact.getText().toString()).removeValue();
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();

                }
            });

        }else{
            delete.setVisibility(View.INVISIBLE);
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                    if (dataSnapshot.getChildrenCount() > 0) {
                        SignUpDetailsPOJO signUpDetailsPOJO = dataSnapshot.getValue(SignUpDetailsPOJO.class);
                        requestorName = signUpDetailsPOJO.getName();
                        Log.d("rew", requestorName);
                    } else {
                        Log.d("rew","No Data yet");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
            DatabaseReference people1 = database1.getReference("personal_data").child(phNo);
            people1.addValueEventListener(valueEventListener1);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Push notification
                    Log.d("rew","Different contact");
                    RequestDetailsPOJO requestDetails = new RequestDetailsPOJO();
                    requestDetails.setRequestorName(requestorName);
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
