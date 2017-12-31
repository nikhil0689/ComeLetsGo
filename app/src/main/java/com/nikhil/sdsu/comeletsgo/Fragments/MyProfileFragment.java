package com.nikhil.sdsu.comeletsgo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.sdsu.comeletsgo.Helpers.DatabaseHelper;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView name,emailId,contact;
    private EditText car,color,license;
    private Button back,update;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth auth;
    List<SignUpDetailsPOJO> userDetailsList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        name = view.findViewById(R.id.my_profile_name);
        contact = view.findViewById(R.id.my_profile_contact);
        emailId = view.findViewById(R.id.my_profile_email);
        car = view.findViewById(R.id.my_profile_car);
        color = view.findViewById(R.id.my_profile_car_color);
        license = view.findViewById(R.id.my_profile_license);
        update = view.findViewById(R.id.my_profile_update);
        String email = auth.getCurrentUser().getEmail().toString();
        String phNo = auth.getCurrentUser().getDisplayName().toString();
        emailId.setText(email);
        contact.setText(phNo);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0){
                    SignUpDetailsPOJO signUpDetailsPOJO = dataSnapshot.getValue(SignUpDetailsPOJO.class);
                    name.setText(signUpDetailsPOJO.getName());
                    car.setText(signUpDetailsPOJO.getCarName());
                    color.setText(signUpDetailsPOJO.getCarColor());
                    license.setText(signUpDetailsPOJO.getCarLicence());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("personal_data").child(contact.getText().toString());
        people.addValueEventListener(valueEventListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("rew","back button clicked");
                SignUpDetailsPOJO signUpDetailsPOJO = new SignUpDetailsPOJO();
                signUpDetailsPOJO.setContact(contact.getText().toString().trim());
                signUpDetailsPOJO.setCarName(car.getText().toString().trim());
                signUpDetailsPOJO.setCarColor(color.getText().toString().trim());
                signUpDetailsPOJO.setCarLicence(license.getText().toString().trim());
                signUpDetailsPOJO.setEmailId(emailId.getText().toString().trim());
                signUpDetailsPOJO.setName(name.getText().toString().trim());
                //boolean updated = databaseHelper.updateProfileData(signUpDetailsPOJO);
                try{
                    mDatabase.child("personal_data").child(contact.getText().toString()).setValue(signUpDetailsPOJO);
                    Log.d("rew","Data submitted successfully");
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }catch(Exception e){
                    Log.d("rew","Exception: "+e);
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
