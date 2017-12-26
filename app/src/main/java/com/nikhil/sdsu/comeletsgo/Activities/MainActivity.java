package com.nikhil.sdsu.comeletsgo.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.nikhil.sdsu.comeletsgo.Fragments.AddTripFragment;
import com.nikhil.sdsu.comeletsgo.Fragments.HomeFragment;
import com.nikhil.sdsu.comeletsgo.Fragments.MyProfileFragment;
import com.nikhil.sdsu.comeletsgo.Fragments.MyTripsFragment;
import com.nikhil.sdsu.comeletsgo.Helpers.DatabaseHelper;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;
import com.nikhil.sdsu.comeletsgo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        AddTripFragment.OnFragmentInteractionListener,MyTripsFragment.OnFragmentInteractionListener,
        MyProfileFragment.OnFragmentInteractionListener{
    List<SignUpDetailsPOJO> userDetailsList = new ArrayList<>();
    private TextView navigation_header_caption;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private FirebaseAuth auth;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View header;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_ADD_TRIP = "add_trip";
    private static final String TAG_MY_TRIPS = "my_trips";
    private static final String TAG_MY_PROFILE = "my_profile";
    public static String CURRENT_TAG = TAG_HOME;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getEmail().toString();
        String name = auth.getCurrentUser().getDisplayName().split("-")[1];
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fab = findViewById(R.id.fab);
        navigationView = findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        navigation_header_caption = header.findViewById(R.id.nav_header_small_text);
        navigation_header_caption.setText(name);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Log.d("rew","inside main activity oncreate");
        String sql = "SELECT * FROM sign_up_table WHERE emailId="+"'"+email+"'";
        userDetailsList = databaseHelper.getUserData(sql);
        Log.d("rew","contact: "+userDetailsList.get(0).getContact());
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
        // show or hide the fab button
            toggleFab();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.screen_area, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                AddTripFragment AddTripFragment = new AddTripFragment();
                return AddTripFragment;
            case 2:
                // movies fragment
                MyTripsFragment myTripsFragment = new MyTripsFragment();
                return myTripsFragment;
            case 3:
                // notifications fragment
                MyProfileFragment myProfileFragment = new MyProfileFragment();
                return myProfileFragment;

            default:
                return new HomeFragment();
        }
    }
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }
    @Override
    public void onBackPressed() {
        Log.d("rew","in back pressed");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_add_trip:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ADD_TRIP;
                        break;
                    case R.id.nav_my_trips:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MY_TRIPS;
                        break;
                    case R.id.nav_my_profile:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_MY_PROFILE;
                        break;
                    default:
                        navItemIndex = 0;
                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
}
