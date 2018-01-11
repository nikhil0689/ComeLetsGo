package com.nikhil.sdsu.comeletsgo.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.sdsu.comeletsgo.Helpers.ComeLetsGoConstants;
import com.nikhil.sdsu.comeletsgo.Pojo.AddTripDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateRideActivity extends AppCompatActivity implements ComeLetsGoConstants{
    private EditText source,destination,seats;
    private TextView date,time;
    private Button datePicker,timePicker,back,update;
    private int mYear,mMonth,mDay,mHour,mMinute;
    private DatabaseReference mDatabase;
    private String name,contact,car,color,license,uid;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ride);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("rew","firebase ref: "+mDatabase.toString());
        source = findViewById(R.id.update_trip_from);
        destination = findViewById(R.id.update_trip_to);
        date = findViewById(R.id.update_trip_date_text);
        time = findViewById(R.id.update_trip_time_text);
        seats = findViewById(R.id.update_trip_number_of_seats);
        datePicker = findViewById(R.id.update_trip_date_button);
        timePicker = findViewById(R.id.update_trip_time_button);
        back = findViewById(R.id.update_trip_back_button);
        update = findViewById(R.id.update_trip_submit);
        if(auth.getCurrentUser()!=null){
            contact = auth.getCurrentUser().getDisplayName();
        }
        selectDate();
        selectTime();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                AddTripDetailsPOJO addTripDetailsPOJO = dataSnapshot.getValue(AddTripDetailsPOJO.class);
                if(addTripDetailsPOJO != null){
                    name=addTripDetailsPOJO.getPostedBy();
                    car=addTripDetailsPOJO.getCar();
                    color=addTripDetailsPOJO.getCarColor();
                    license=addTripDetailsPOJO.getLicense();
                    source.setText(addTripDetailsPOJO.getSource());
                    destination.setText(addTripDetailsPOJO.getDestination());
                    date.setText(addTripDetailsPOJO.getDate());
                    time.setText(addTripDetailsPOJO.getTime());
                    seats.setText(String.valueOf(addTripDetailsPOJO.getSeatsAvailable()));
                    uid=addTripDetailsPOJO.getUid();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("trip_details").child(contact);
        people.addValueEventListener(valueEventListener);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInput()){
                    AddTripDetailsPOJO addTripDetailsPOJO = new AddTripDetailsPOJO();
                    addTripDetailsPOJO.setPostedBy(name);
                    addTripDetailsPOJO.setCar(car);
                    addTripDetailsPOJO.setCarColor(color);
                    addTripDetailsPOJO.setLicense(license);
                    addTripDetailsPOJO.setSource(source.getText().toString());
                    addTripDetailsPOJO.setDestination(destination.getText().toString());
                    addTripDetailsPOJO.setDate(date.getText().toString());
                    addTripDetailsPOJO.setTime(time.getText().toString());
                    addTripDetailsPOJO.setContact(contact);
                    addTripDetailsPOJO.setUid(uid);
                    addTripDetailsPOJO.setSeatsAvailable(Integer.parseInt(seats.getText().toString()));
                    try{
                        mDatabase.child(FIREBASE_TRIP_DETAILS).child(contact).setValue(addTripDetailsPOJO);
                        Log.d("rew","Data updated successfully");
                        finish();
                    }catch(Exception e){
                        Log.d("rew","Exception: "+e);
                    }
                }else{
                    Toast.makeText(UpdateRideActivity.this,ENTER_REQUIRED_FIELDS,Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(source.getText().toString())) {
            source.setError(ENTER_SOURCE);
            dataValid = false;
        }
        if (TextUtils.isEmpty(destination.getText().toString())) {
            destination.setError(ENTER_DESTINATION);
            dataValid = false;
        }
        if (TextUtils.isEmpty(date.getText().toString())) {
            date.setError(ENTER_DATE);
            dataValid = false;
        }
        if (TextUtils.isEmpty(time.getText().toString())) {
            time.setError(ENTER_TIME);
            dataValid = false;
        }
        if (TextUtils.isEmpty(seats.getText().toString())) {
            seats.setError(ENTER_SEATS);
            dataValid = false;
        }
        if (Integer.parseInt(seats.getText().toString())<1) {
            seats.setError(SEATS_ZERO);
            dataValid = false;
        }
        String dateString=date.getText().toString().concat(" ").concat(time.getText().toString());
        DateFormat formatter ;
        Date date ;
        formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        try {
            date = (Date) formatter.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Log.d("rew", "formatted time: " + date);
            Log.d("rew", "current time: " + Calendar.getInstance().getTime());
            if(date.before(Calendar.getInstance().getTime())){
                dataValid = false;
            }
        }catch (Exception e){
            Log.d("rew","Exception: "+e);
        }
        return dataValid;
    }



    private void selectTime() {
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateRideActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String formatTime = hourOfDay + ":" + minute;
                                time.setText(formatTime);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private void selectDate() {
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateRideActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formatDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                date.setText(formatDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
