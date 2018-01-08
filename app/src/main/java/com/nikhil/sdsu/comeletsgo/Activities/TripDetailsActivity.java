package com.nikhil.sdsu.comeletsgo.Activities;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.nikhil.sdsu.comeletsgo.Pojo.MyRideDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.RequestDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TripDetailsActivity extends AppCompatActivity {
    private TextView source,destination,date,time,seats,poster,posterContact,car,carColor,license;
    private Button back,join,delete,update;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String requestorName="";
    private String requestorContact="";
    List<String> joineeList = new ArrayList<>();
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle intent = getIntent().getExtras();
        final AddTripDetailsPOJO addTripDetails = (AddTripDetailsPOJO) intent.getSerializable("tripDetailsFromPoster");
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
        update = findViewById(R.id.trip_update_button);
        delete = findViewById(R.id.trip_delete_button);
        posterContact.setText(addTripDetails.getContact());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildren() + " trips available in tripdetails activity");
                AddTripDetailsPOJO addTripDetailsPOJO = dataSnapshot.getValue(AddTripDetailsPOJO.class);
                if(addTripDetailsPOJO!=null){
                    source.setText(addTripDetailsPOJO.getSource());
                    destination.setText(addTripDetailsPOJO.getDestination());
                    date.setText(addTripDetailsPOJO.getDate());
                    time.setText(addTripDetailsPOJO.getTime());
                    seats.setText(""+addTripDetailsPOJO.getSeatsAvailable());
                    poster.setText(addTripDetailsPOJO.getPostedBy());
                    uid = addTripDetailsPOJO.getUid();
                    Log.d("rew","contact from cloud: "+posterContact.getText().toString());
                    car.setText(addTripDetailsPOJO.getCar());
                    carColor.setText(addTripDetailsPOJO.getCarColor());
                    license.setText(addTripDetailsPOJO.getLicense());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("rew","data error in trip details activity: "+databaseError);
            }
        };
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("trip_details").child(posterContact.getText().toString());
        people.addValueEventListener(valueEventListener);

        requestedPassengersData(phNo);
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
                        public void onClick(final DialogInterface dialogInterface, int n) {
                            for(int i=0;i<joineeList.size();i++) {
                                ValueEventListener valueEventListener1 = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                            Log.d("rew", "key: " + snapshot.getKey());
                                            Log.d("rew", "value: " + snapshot.getValue());
                                            DatabaseReference ref = snapshot.getRef();
                                            if(snapshot.getValue().equals(uid)){
                                                Log.d("rew","reference: "+ref);
                                                Log.d("rew","reference key: "+ref.getKey());
                                                ref.removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("rew","database error: "+databaseError);
                                    }
                                };
                                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                DatabaseReference people1 = database1.getReference("current_rides").child(joineeList.get(i));
                                people1.addValueEventListener(valueEventListener1);
                            }
                            try {
                                Log.d("rew","delay");
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mDatabase.child("requests").child(posterContact.getText().toString()).removeValue();
                            mDatabase.child("trip_details").child(posterContact.getText().toString()).removeValue();
                            mDatabase.child("my_rides").child(uid).removeValue();
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
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(TripDetailsActivity.this,UpdateRideActivity.class);
                    startActivity(intent1);
                }
            });

        }else{
            update.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                    if (dataSnapshot.getChildrenCount() > 0) {
                        SignUpDetailsPOJO signUpDetailsPOJO = dataSnapshot.getValue(SignUpDetailsPOJO.class);
                        requestorName = signUpDetailsPOJO.getName();
                        requestorContact = signUpDetailsPOJO.getContact();
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

    private void requestedPassengersData(final String phNo) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildren() + " trips available in tripdetails activity");
                LinearLayout linearLayout = findViewById(R.id.text_programatically);
                TextView passengers1 = new TextView(TripDetailsActivity.this);
                passengers1.setText("People in the Ride");
                passengers1.setGravity(Gravity.CENTER);
                linearLayout.addView(passengers1);
                joineeList.add(posterContact.getText().toString());
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        RequestDetailsPOJO requestDetailsPOJO = msgSnapshot.getValue(RequestDetailsPOJO.class);
                        if(requestDetailsPOJO.isApprovalStatus()){
                            requestorContact = requestDetailsPOJO.getRequestorContact();
                            Log.d("rew","Approved for: "+requestorContact);
                            TextView passengers = new TextView(TripDetailsActivity.this);
                            passengers.setText(requestDetailsPOJO.getRequestorName());
                            joineeList.add(requestDetailsPOJO.getRequestorContact());
                            passengers.setGravity(Gravity.CENTER);
                            passengers.setTypeface(Typeface.DEFAULT_BOLD);
                            linearLayout.addView(passengers);
                            if(phNo.equalsIgnoreCase(requestDetailsPOJO.getRequestorContact())){
                                join.setEnabled(false);
                            }
                        }
                        Log.d("rew", requestDetailsPOJO.getRequestorName());
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("requests").child(posterContact.getText().toString());
        people.addValueEventListener(valueEventListener);
    }
}
