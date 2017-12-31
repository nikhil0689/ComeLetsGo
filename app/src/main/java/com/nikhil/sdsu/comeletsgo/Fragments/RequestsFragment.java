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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.sdsu.comeletsgo.Activities.MainActivity;
import com.nikhil.sdsu.comeletsgo.Activities.TripDetailsActivity;
import com.nikhil.sdsu.comeletsgo.Helpers.RequestsAdapter;
import com.nikhil.sdsu.comeletsgo.Helpers.TripDetailsAdapter;
import com.nikhil.sdsu.comeletsgo.Pojo.AddTripDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.Pojo.RequestDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView requestsListView;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    RequestsAdapter listadapter;
    List<RequestDetailsPOJO> requestList = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int seatsAvailable;
    private OnFragmentInteractionListener mListener;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
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
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("rew","in oncreate view");
        requestsListView = view.findViewById(R.id.requests_list_view);
        checkForMyProfile();
        checkNumberOfSeats();
        final String phNo = auth.getCurrentUser().getDisplayName().toString();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        RequestDetailsPOJO requestDetailsPOJO = msgSnapshot.getValue(RequestDetailsPOJO.class);
                        Log.d("rew", requestDetailsPOJO.getRequestorName());
                        requestList.add(requestDetailsPOJO);
                    }
                    Collections.reverse(requestList);
                    if(getActivity() != null){
                        listadapter = new RequestsAdapter(getActivity(), 0, requestList);
                        requestsListView.setAdapter(listadapter);
                    }

                } else {
                    if (listadapter != null) {
                        listadapter.notifyDataSetChanged();
                    }
                    Log.d("rew","No Data yet");
                }
                requestsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final RequestDetailsPOJO requestDetailsPOJO = (RequestDetailsPOJO) adapterView.getItemAtPosition(i);
                        final String requestorContact = ((RequestDetailsPOJO) adapterView.getItemAtPosition(i)).getRequestorContact();
                        if(requestDetailsPOJO.isApprovalStatus() == false && seatsAvailable > 0){
                            requestDetailsPOJO.setApprovalStatus(true);
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Add Passenger");
                            alert.setMessage("Accept");
                            alert.setPositiveButton("ADD", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child("requests")
                                            .child(auth.getCurrentUser()
                                                    .getDisplayName())
                                            .child(requestorContact).setValue(requestDetailsPOJO);
                                    seatsAvailable = seatsAvailable-1;
                                    mDatabase.child("trip_details")
                                            .child(phNo).child("seatsAvailable").setValue(seatsAvailable);
                                    listadapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });

                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                            return true;
                        }else{
                            return true;
                        }

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("requests").child(auth.getCurrentUser().getDisplayName().toString());
        people.addValueEventListener(valueEventListener);
        return view;
    }

    private void checkNumberOfSeats() {
        final String phNo = auth.getCurrentUser().getDisplayName().toString();
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()!=0){
                    final AddTripDetailsPOJO addTripDetailsPOJO = dataSnapshot.getValue(AddTripDetailsPOJO.class);
                    seatsAvailable = addTripDetailsPOJO.getSeatsAvailable();
                    if(getContext()!=null){
                        Toast.makeText(getContext(),"Seats Available: "+seatsAvailable,Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference("trip_details").child(phNo);
        people1.addValueEventListener(valueEventListener1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkForMyProfile() {
        String phNo = auth.getCurrentUser().getDisplayName().toString();
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()<1){
                    Fragment myProfileFragment = new MyProfileFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.screen_area,myProfileFragment);
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