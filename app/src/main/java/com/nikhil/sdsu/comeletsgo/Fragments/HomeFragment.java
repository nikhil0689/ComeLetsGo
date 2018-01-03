package com.nikhil.sdsu.comeletsgo.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.sdsu.comeletsgo.Activities.MainActivity;
import com.nikhil.sdsu.comeletsgo.Activities.TripDetailsActivity;
import com.nikhil.sdsu.comeletsgo.Helpers.TripDetailsAdapter;
import com.nikhil.sdsu.comeletsgo.Pojo.AddTripDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ListView tripDetailsListView;
    private FirebaseAuth auth;
    TripDetailsAdapter listadapter;
    List<AddTripDetailsPOJO> tripDataList = new ArrayList<>();
    private DatabaseReference mDatabase;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("rew","inoncreate view");
        tripDetailsListView = view.findViewById(R.id.trip_list_home);
        checkForMyProfile();
        getRideDetailsOntoTheList();
        return view;
    }

    private void getRideDetailsOntoTheList() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripDataList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        AddTripDetailsPOJO addTripDetailsPOJO = msgSnapshot.getValue(AddTripDetailsPOJO.class);
                        Log.d("rew", addTripDetailsPOJO.getUid());
                        tripDataList.add(addTripDetailsPOJO);
                    }
                    Collections.reverse(tripDataList);
                    if(getActivity() != null){
                        listadapter = new TripDetailsAdapter(getActivity(), 0, tripDataList);
                        tripDetailsListView.setAdapter(listadapter);
                    }

                } else {
                    if (listadapter != null) {
                        listadapter.notifyDataSetChanged();
                    }
                    Log.d("rew","No Data yet");
                }
                tripDetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent intent = new Intent(getActivity(), TripDetailsActivity.class);
                        AddTripDetailsPOJO addTripDetailsPOJO = (AddTripDetailsPOJO) adapterView.getItemAtPosition(position);
                        intent.putExtra("tripDetailsFromPoster",addTripDetailsPOJO);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("rew","database error: "+databaseError);
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("trip_details");
        people.addValueEventListener(valueEventListener);
    }

    private void checkForMyProfile() {
        String phNo = auth.getCurrentUser().getDisplayName().toString();
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "in the personal details section: "+dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount()<1){
                    Fragment updateProfileFragment = new UpdateProfileFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.screen_area,updateProfileFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference("personal_data").child(phNo);
        people1.addValueEventListener(valueEventListener1);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("rew","in on view created");


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
