package com.nikhil.sdsu.comeletsgo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.sdsu.comeletsgo.Helpers.ComeLetsGoConstants;
import com.nikhil.sdsu.comeletsgo.Pojo.MyRideDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.HashMap;
import java.util.Map;

public class RideHistoryDetailsActivity extends AppCompatActivity implements ComeLetsGoConstants{
    private TextView source,destination,date,time,joinees,poster,posterContact,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_details);
        source = findViewById(R.id.ride_history_source);
        destination = findViewById(R.id.ride_history_destination);
        date = findViewById(R.id.ride_history_date);
        time = findViewById(R.id.ride_history_time);
        joinees = findViewById(R.id.ride_history_people);
        poster = findViewById(R.id.ride_history_posted_by);
        posterContact = findViewById(R.id.ride_history_poster_contact);
        status = findViewById(R.id.ride_history_status);
        Button back = findViewById(R.id.ride_history_back);
        Bundle intent = getIntent().getExtras();
        String uidFromIntent = intent != null ? intent.getString(UID) : null;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                MyRideDetailsPOJO myRideDetailsPOJO = dataSnapshot.getValue(MyRideDetailsPOJO.class);
                if(myRideDetailsPOJO!=null){
                    source.setText(myRideDetailsPOJO.getSource());
                    destination.setText(myRideDetailsPOJO.getDestination());
                    date.setText(myRideDetailsPOJO.getDate());
                    time.setText(myRideDetailsPOJO.getTime());
                    poster.setText(myRideDetailsPOJO.getPosterName());
                    posterContact.setText(myRideDetailsPOJO.getPosterContact());
                    if(myRideDetailsPOJO.isApprovalStatus()){
                        status.setText(COMPLETED);
                    }else{
                        status.setText(WAITING);
                    }
                    String peopleInRide = myRideDetailsPOJO.getJoinee().values().toString().replace("[","").replace("]","");
                    joinees.setText(peopleInRide);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_MY_RIDES).child(uidFromIntent);
        people.addValueEventListener(valueEventListener);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
