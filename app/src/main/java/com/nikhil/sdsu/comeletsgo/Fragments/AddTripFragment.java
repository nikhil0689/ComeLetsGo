package com.nikhil.sdsu.comeletsgo.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nikhil.sdsu.comeletsgo.Helpers.Utilities;
import com.nikhil.sdsu.comeletsgo.Pojo.AddTripDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.MyRideDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTripFragment extends Fragment implements ComeLetsGoConstants {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private EditText source,destination,seats;
    private TextView date,time;
    private Button datePicker,timePicker,reset,submit;
    private int mYear,mMonth,mDay,mHour,mMinute;
    private DatabaseReference mDatabase;
    private String name,contact,car,color,license;
    List userDetailsList = new ArrayList<>();
    private FirebaseAuth auth;
    public AddTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTripFragment newInstance(String param1, String param2) {
        AddTripFragment fragment = new AddTripFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_trip,null);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            contact = auth.getCurrentUser().getDisplayName();
            Log.d("rew","contact from fire: "+contact);
        }
        Utilities utilities = new Utilities(getFragmentManager());
        utilities.checkProfile();
        utilities.checkForExistingRide(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("rew","firebase ref: "+mDatabase.toString());
        source = view.findViewById(R.id.add_trip_from);
        destination = view.findViewById(R.id.add_trip_to);
        date = view.findViewById(R.id.add_trip_date_text);
        time = view.findViewById(R.id.add_trip_time_text);
        seats = view.findViewById(R.id.add_trip_number_of_seats);
        datePicker = view.findViewById(R.id.add_trip_date_button);
        timePicker = view.findViewById(R.id.add_trip_time_button);
        reset = view.findViewById(R.id.add_trip_reset_button);
        submit = view.findViewById(R.id.add_trip_submit);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                source.setText(EMPTY_STRING);
                destination.setText(EMPTY_STRING);
                date.setText(EMPTY_STRING);
                time.setText(EMPTY_STRING);
                seats.setText(EMPTY_STRING);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectDate();
        selectTime();
        checkForExistingRide();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                SignUpDetailsPOJO signUpDetailsPOJO = dataSnapshot.getValue(SignUpDetailsPOJO.class);
                if(signUpDetailsPOJO!=null){
                    name = signUpDetailsPOJO.getName();
                    car = signUpDetailsPOJO.getCarName();
                    color = signUpDetailsPOJO.getCarColor();
                    license = signUpDetailsPOJO.getCarLicence();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_PERSONAL_DATA).child(contact);
        people.addValueEventListener(valueEventListener);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInput()){
                    AddTripDetailsPOJO addTripDetailsPOJO = new AddTripDetailsPOJO();
                    addTripDetailsPOJO.setSource(source.getText().toString().trim());
                    addTripDetailsPOJO.setDestination(destination.getText().toString().trim());
                    addTripDetailsPOJO.setDate(date.getText().toString().trim());
                    addTripDetailsPOJO.setTime(time.getText().toString().trim());
                    int noOfSeats = Integer.parseInt(seats.getText().toString());
                    addTripDetailsPOJO.setSeatsAvailable(noOfSeats);
                    addTripDetailsPOJO.setPostedBy(name);
                    addTripDetailsPOJO.setContact(contact);
                    addTripDetailsPOJO.setCar(car);
                    addTripDetailsPOJO.setCarColor(color);
                    addTripDetailsPOJO.setLicense(license);
                    addTripDetailsPOJO.setUid(mDatabase.child(FIREBASE_TRIP_DETAILS).push().getKey());

                    MyRideDetailsPOJO myRideDetailsPOJO = new MyRideDetailsPOJO();
                    myRideDetailsPOJO.setSource(source.getText().toString().trim());
                    myRideDetailsPOJO.setDestination(destination.getText().toString().trim());
                    myRideDetailsPOJO.setTime(time.getText().toString().trim());
                    myRideDetailsPOJO.setDate(date.getText().toString().trim());
                    myRideDetailsPOJO.setPosterName(name);
                    myRideDetailsPOJO.setPosterContact(contact);
                    myRideDetailsPOJO.setUid(addTripDetailsPOJO.getUid());
                    myRideDetailsPOJO.setApprovalStatus(false);
                    Log.d("rew","uid: "+addTripDetailsPOJO.getUid());
                    try{
                        mDatabase.child(FIREBASE_TRIP_DETAILS).child(contact).setValue(addTripDetailsPOJO);
                        mDatabase.child(FIREBASE_MY_RIDES).child(addTripDetailsPOJO.getUid()).setValue(myRideDetailsPOJO);
                        mDatabase.child(FIREBASE_CURRENT_RIDES).child(contact).push().setValue(addTripDetailsPOJO.getUid());
                        Log.d("rew","Data submitted successfully");
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }catch(Exception e){
                        Log.d("rew","Exception: "+e);
                    }
                }else{
                    Toast.makeText(getContext(),VALIDATION_FAILURE,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(source.getText().toString())) {
            source.setError(ENTER_SOURCE);
            dataValid = false;
        }else if(TextUtils.isEmpty(destination.getText().toString())) {
            destination.setError(ENTER_DESTINATION);
            dataValid = false;
        }else if(TextUtils.isEmpty(date.getText().toString())) {
            date.setError(ENTER_DATE);
            dataValid = false;
        }else if(TextUtils.isEmpty(time.getText().toString())) {
            time.setError(ENTER_TIME);
            dataValid = false;
        }else if(TextUtils.isEmpty(seats.getText().toString())) {
            seats.setError(ENTER_SEATS);
            dataValid = false;
        }else if(!TextUtils.isEmpty(time.getText().toString()) && Integer.parseInt(seats.getText().toString())<1) {
            seats.setError(SEATS_ZERO);
            dataValid = false;
        }
        String dateString=date.getText().toString().concat(" ").concat(time.getText().toString());
        DateFormat formatter ;
        Date date ;
        formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        try {
            date = formatter.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Log.d("rew", "formatted time: " + date);
            Log.d("rew", "current time: " + Calendar.getInstance().getTime());
            if(date.before(Calendar.getInstance().getTime())){
                Toast.makeText(getContext(),DATE_VALIDATION_FAILURE_TOAST,Toast.LENGTH_SHORT).show();
                dataValid = false;
            }
        }catch (Exception e){
            Log.d("rew","Exception: "+e);
        }
        return dataValid;
    }

    private void checkForExistingRide() {
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0 && getActivity()!=null){
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference(FIREBASE_TRIP_DETAILS).child(contact);
        people1.addValueEventListener(valueEventListener1);
    }

    private void selectTime() {
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String timeFormat = hourOfDay + ":" + minute;
                                time.setText(timeFormat);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String dateFormat = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                date.setText(dateFormat);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
