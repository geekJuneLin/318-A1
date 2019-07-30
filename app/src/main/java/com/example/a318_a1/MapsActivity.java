package com.example.a318_a1;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location location;
    private double lat, lon, activityLat, activityLon;
    private Button buttonCreate, buttonDismiss, buttonSetTime, buttonTimeSelected, buttonRaidCancel;
    private RelativeLayout hiddenView, timeView, raidInfoView;
    private TimePicker timer;
    private Spinner diffList;
    private String[] list = {"Level 1 *","Level 2 **","Level 3 ***","Level 4 ****","Level 5 *****"};
    private ArrayList<RaidActivity> activityArray;
    private ArrayList<MarkerOptions> markerArray;
    private TextView raidTime, raidDiff, raidNum;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activityArray = new ArrayList<>();
        markerArray  = new ArrayList<>();

        setUpViews();

        setOnclickListener();

        getLocation();

    }

    /**
     *  set up the components
     */
    private void setUpViews(){
        hiddenView = findViewById(R.id.hiddenView);
        timeView = findViewById(R.id.timerView);
        raidInfoView = findViewById(R.id.raidInfoView);

        timer = findViewById(R.id.timer);

        raidTime = findViewById(R.id.raidTime);
        raidDiff = findViewById(R.id.raidDiff);
        raidNum = findViewById(R.id.raidNum);

        diffList = findViewById(R.id.diffList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        diffList.setAdapter(adapter);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonDismiss = findViewById(R.id.dismiss);
        buttonSetTime = findViewById(R.id.setTime);
        buttonTimeSelected = findViewById(R.id.timeSet);
        buttonRaidCancel = findViewById(R.id.raidCancelButton);
    }

    /**
     *  set up the buttonOnClickListeners
     */
    private void setOnclickListener(){
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markerArray.size() != 2){
                    Toast.makeText(MapsActivity.this, "Please select the location of the activity first!", Toast.LENGTH_LONG).show();
                    dismiss();
                    return;
                }else{
                    Log.d("LatLng of activity", markerArray.get(1).getPosition().latitude +
                            ", " + markerArray.get(1).getPosition().longitude);
                    activityArray.add(new RaidActivity(1,
                                        diffList.getSelectedItem().toString(),
                                        String.valueOf(timer.getHour() + ":" + timer.getMinute()),
                                        markerArray.get(1).getPosition().latitude,
                                        markerArray.get(1).getPosition().longitude));

                    for(MarkerOptions m : markerArray){
                        mMap.addMarker(m);
                    }
                    MarkerOptions marker;
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    if(activityArray.size() > 0){
                        for(RaidActivity r : activityArray){
                            marker = new MarkerOptions().position(new LatLng(r.lat, r.lon)).title("Existed activity!").icon(bitmapDescriptor);
                            mMap.addMarker(marker);
                        }
                    }
                    dismiss();
                    Toast.makeText(MapsActivity.this, "Activity has been created successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayView();
            }
        });
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(timeView, "translationY", 1800f);
                animation.setDuration(500);
                animation.start();
                buttonDismiss.setEnabled(false);
            }
        });
        buttonTimeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(timeView, "translationY", -1800f);
                animation.setDuration(500);
                animation.start();
                buttonDismiss.setEnabled(true);
                Log.d("Time selected",String.valueOf(timer.getHour() + ", " + timer.getMinute() + ", " + diffList.getSelectedItem()));
            }
        });
        buttonRaidCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(raidInfoView, "translationY", 1200f);
                animation.setDuration(500);
                animation.start();
                buttonCreate.setEnabled(true);
            }
        });
    }

    /**
     *  dismiss the hiddenView
     */
    private void dismiss(){
        ObjectAnimator animation = ObjectAnimator.ofFloat(hiddenView, "translationY", 1200f);
        animation.setDuration(500);
        animation.start();
    }

    /**
     *  display the hiddenView
     */
    private void displayView(){
        ObjectAnimator animation = ObjectAnimator.ofFloat(hiddenView, "translationY", -1200f);
        animation.setDuration(500);
        animation.start();
    }

    /**
     *  this method will check the permission first then get the current location of the user
     */
    private void getLocation(){
        if(checkPermission()){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.d("location", lat + ", " + lon);
            }else{
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null){
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Log.d("location", lat + ", " + lon);
                }else{
                    Toast.makeText(this, "Unable to get your location!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     *  check the GPS location permission
     * @return true: permission granted
     *         false: permission denied
     */
    private boolean checkPermission(){
        try{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Unable to get your location!", Toast.LENGTH_LONG).show();
                Thread.sleep(3000);
                ActivityCompat.requestPermissions(this,
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                                    10);
                return false;
            }else{
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng current;

        // Add a marker in Sydney and move the camera
        if(lat == 0 && lon == 0){
            current  = new LatLng(-37.7770, 175.2515);
        }else{
            current = new LatLng(lat, lon);
        }

        MarkerOptions marker = new MarkerOptions().position(current).title("Your current location");
        mMap.addMarker(marker);
        markerArray.add(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));

        // Map click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE);
                MarkerOptions marker = new MarkerOptions().position(latLng).title("Activity is here").icon(bitmapDescriptor);
                mMap.clear();

                if(markerArray.size() == 1){
                    markerArray.add(1, marker);
                }else{
                    markerArray.remove(1);
                    markerArray.add(1, marker);
                }

                for(MarkerOptions m : markerArray){
                    mMap.addMarker(m);
                }

                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                if(activityArray.size() > 0){
                    for(RaidActivity r : activityArray){
                        marker = new MarkerOptions().position(new LatLng(r.lat, r.lon)).title("Existed activity!").icon(bitmapDescriptor);
                        mMap.addMarker(marker);
                    }
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(RaidActivity r : activityArray){
                    if(marker.getPosition().longitude == r.lon && marker.getPosition().latitude == r.lat){
                        buttonCreate.setEnabled(false);

                        ObjectAnimator animation = ObjectAnimator.ofFloat(raidInfoView, "translationY", -1200f);
                        animation.setDuration(500);
                        animation.start();

                        // Display raid information
                        raidTime.setText(r.time);
                        raidDiff.setText(r.difficulty);
                        raidNum.setText(String.valueOf(r.playerNum));

                        Log.d("Display the info", "Show the info");
                    }
                }
                return false;
            }
        });
    }

}
