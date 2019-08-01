package com.example.a318_a1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Button createButton, buttonSend, buttonPlace, buttonSetTime, buttonSaveTime;
    private RelativeLayout hiddenView, createView, timeView;
    private LocationManager locationManager;
    private Location location;
    private double lat, lon;
    private ArrayList<MarkerOptions> markerArray;
    private ArrayList<RaidActivity> activityArray;
    private ArrayList<Message> msgList;
    private Spinner diffList;
    private String[] list = {"Level 1 *","Level 2 **","Level 3 ***","Level 4 ****","Level 5 *****"};
    private TextView raidTime, raidDiff, showNum, showTime, showDiff;
    private TimePicker timer;
    private EditText msg;
    private Message msgContent;
    private ListView parent;
    private ListViewAdapter listAdapter;
    private RaidActivity currentAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        activityArray = new ArrayList<>();
        markerArray  = new ArrayList<>();

        parent = findViewById(R.id.message_view);
        listAdapter = new ListViewAdapter(this);

        setUpComponents();

        setUpListeners();

        getLocation();
    }

    private void getLocation(){
        if(checkPermission()){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.d("location", lat + ", " + lon);
            }else{
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
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

    private void setUpComponents(){
        msg = findViewById(R.id.msgContent);

        // Time picker set up
        timer = findViewById(R.id.timer);

        // Text view set up
        raidTime = findViewById(R.id.setTimeLabel);
        raidDiff = findViewById(R.id.setDiffLabel);
        showTime = findViewById(R.id.timeLabel);
        showDiff = findViewById(R.id.diffLabel);
        showNum = findViewById(R.id.countLabel);

        // Spinner data
        diffList = findViewById(R.id.diffList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        diffList.setAdapter(adapter);

        // Set up maps
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        // Set buttons
        createButton = findViewById(R.id.bc);
        buttonSend = findViewById(R.id.buttonSend);
        buttonPlace = findViewById(R.id.buttonPlace);
        buttonSetTime = findViewById(R.id.setTime);
        buttonSaveTime = findViewById(R.id.timeSet);

        // Set views
        hiddenView = findViewById(R.id.hv);
        createView = findViewById(R.id.cv);
        timeView = findViewById(R.id.timeParentView);
    }

    // Listeners
    private void setUpListeners(){
        hiddenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(hiddenView, "translationY", 2000f);
                animator.setDuration(500);
                animator.start();
            }
        });

        createView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(createView, "translationY", 2000f);
                animator.setDuration(500);
                animator.start();
            }
        });

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(timeView, "translationY", -1800f);
                animator.setDuration(500);
                animator.start();
            }
        });

        // Create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(createView, "translationY", -8f);
                animator.setDuration(500);
                animator.start();
            }
        });

        // Send button
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send message method here
                if(msg.length() > 0){
                    Log.d("MSG", "Sending msg!");
                    msgContent = new Message(msg.getText().toString(), false);
                    Message m = new Message(msg.getText().toString(), true);
                    msgList = new ArrayList<>();
                    msgList.add(msgContent);
                    msgList.add(m);
                    if(!currentAc.getList()){
                        currentAc.setList(msgList);
                    } else {
                        currentAc.msgList.add(msgContent);
                        currentAc.msgList.add(m);
                    }
                    listAdapter.addMessage(msgContent);
                    listAdapter.addMessage(m);
                    parent.setAdapter(listAdapter);
                    msg.getText().clear();
                }
            }
        });

        // Place button
        buttonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markerArray.size() != 2){
                    Toast.makeText(MapActivity.this, "Please select the location of the activity first!", Toast.LENGTH_LONG).show();
                    dismiss();
                    return;
                }else{
                    Log.d("LatLng of activity", markerArray.get(1).getPosition().latitude +
                            ", " + markerArray.get(1).getPosition().longitude);

                    RaidActivity ac = new RaidActivity(1, diffList.getSelectedItem().toString(),
                            timer.getHour() + ":" + timer.getMinute(),
                            markerArray.get(1).getPosition().latitude,
                            markerArray.get(1).getPosition().longitude);
                    activityArray.add(ac);

                    mMap.clear();
                    mMap.addMarker(markerArray.get(0));
                    MarkerOptions marker;
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    if(activityArray.size() > 0){
                        for(RaidActivity r : activityArray){
                            marker = new MarkerOptions().position(new LatLng(r.lat, r.lon)).title("Existed activity!").icon(bitmapDescriptor);
                            mMap.addMarker(marker);
                        }
                    }
                    dismiss();
                    Toast.makeText(MapActivity.this, "Activity has been created successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set time button
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(timeView, "translationY", 6f);
                animator.setDuration(500);
                animator.start();
            }
        });

        // Save time button
        buttonSaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(timeView, "translationY", -1800f);
                animator.setDuration(500);
                animator.start();
                raidTime.setText("Set time: " + timer.getHour() + ":" + timer.getMinute());
            }
        });

        // Spinner listener
        diffList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                raidDiff.setText("Set difficulty: "
                        + diffList.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void dismiss(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(createView, "translationY", 2000f);
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng current = new LatLng(lat, lon);
        MarkerOptions marker = new MarkerOptions().position(current).title("Your current location!");

        mMap.addMarker(marker);
        markerArray.add(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));

        setMarkerListener();

        setMapListener();
    }

    private void setMapListener(){
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
    }

    private void setMarkerListener(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Remove the messages from the list view
                listAdapter.removeMessage();

                // Check which activity has been selected
                for(RaidActivity r : activityArray){
                    if(marker.getPosition().longitude == r.lon && marker.getPosition().latitude == r.lat){
                        // Set the current ac
                        currentAc = r;

                        if(currentAc.getList()){
                            Toast.makeText(MapActivity.this, String.valueOf(currentAc.msgList.size()),Toast.LENGTH_LONG).show();
                            if(currentAc.msgList.size() > 0){
                                for(Message m : currentAc.msgList){
                                    listAdapter.addMessage(m);
                                }
                            }
                        }

                        // Display the info of the activity
                        showTime.setText("Time: " + r.time);
                        showDiff.setText("Difficulty: " + r.difficulty);
                        showNum.setText("Joined: " + String.valueOf(r.playerNum));

                        // Display the raid view
                        ObjectAnimator animator = ObjectAnimator.ofFloat(hiddenView, "translationY", -8f);
                        animator.setDuration(500);
                        animator.start();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
