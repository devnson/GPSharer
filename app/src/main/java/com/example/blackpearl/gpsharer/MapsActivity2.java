package com.example.blackpearl.gpsharer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    FirebaseAuth mAuth;

    FirebaseDatabase mdb;
    private DatabaseReference mDatabaseReference;


    //FAB
    FloatingActionMenu fam;
    FloatingActionButton activeBtn;
    FloatingActionButton createCircleBtn;
    FloatingActionButton joinCircleBtn;
    FloatingActionButton logoutBtn;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        getLocationPermission();

        mAuth = FirebaseAuth.getInstance();
        mdb = FirebaseDatabase.getInstance();
        mDatabaseReference = mdb.getReference("Users");

        //referencing fab
        fam = findViewById(R.id.fam_id);
        activeBtn = findViewById(R.id.activeFAB_id);
        createCircleBtn = findViewById(R.id.createCircleFAB_id);
        joinCircleBtn = findViewById(R.id.joinCircleFAB_id);
        logoutBtn = findViewById(R.id.logutFAB_id);
        ;


        //getting user data
        //getting user data
        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
            username = b.getString("username");
            fullname = b.getString("fullname");

            Log.d("mapsid", "this is id" + id);
            Log.d("mapsusername", "this is a username " + username);
        }


        Log.d("Latitude", String.valueOf(mLatitude));
        Log.d("Longitude", String.valueOf(mLongitude));


        createCircleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MapsActivity2.this, CreateCircle.class);
                Bundle b = new Bundle();
                //String mid= id.replace("@gmail.com","");
                b.putString("id", id);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        joinCircleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity2.this, JoinCircleActivity.class);
                Bundle b = new Bundle();
                //String mid= id.replace("@gmail.com","");
                b.putString("id", id);
                intent.putExtras(b);
                startActivity(intent);

            }
        });

        activeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity2.this, FriendListActivity.class);
                Bundle b = new Bundle();
                //String mid= id.replace("@gmail.com","");
                b.putString("id", id);
                intent.putExtras(b);
                startActivityForResult(intent, 0);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(MapsActivity2.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(MapsActivity2.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });


    }

    public String id;
    public String username;
    public String fullname;

    private void getUserDataFromIntent() {

        //getting user data
        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
            username = b.getString("username");
            fullname = b.getString("fullname");

            Log.d("mapsid", "this is id" + id);
            Log.d("mapsusername", "this is a username " + username);
        }
    }


    Boolean yesclicked;

    @Override
    protected void onStart() {
        super.onStart();
        getUserDataFromIntent();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            yesclicked = b.getBoolean("islistitemclicked");
        }
        Log.d("isListItemClicked", yesclicked.toString());

        if (yesclicked) {
            moveCameraToFriendLocation();
        } else {
            getDeviceLocation();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    private static String mLatitude;
    private static String mLongitude;
    private static Double nLatitude;
    private static Double nLongitude;
    private static float ZOOM_LEVEL = 15f;
    LatLng latLng;

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLatitude = Double.toString(location.getLatitude());
                    mLongitude = Double.toString(location.getLongitude());
                    nLatitude = location.getLatitude();
                    nLongitude = location.getLongitude();
                    latLng = new LatLng(nLatitude, nLongitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL);
                    mMap.moveCamera(cameraUpdate);
                    addLocationData();

                }
            }
        });

    }


    private void addLocationData() {

        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
            username = b.getString("username");
            fullname = b.getString("fullname");

            Log.d("mapsid", "this is id" + id);
            Log.d("mapsusername", "this is a username " + username);
        }


        String aid = id;
        Log.d("aid", "This is a id to add location data " + aid);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Latitude", nLatitude);
        userMap.put("Longitude", nLongitude);
        Log.d("aldLatitude", nLatitude.toString());
        Log.d("aldLatitude", nLongitude.toString());

        mDatabaseReference.child(aid).child("GeoPoint").updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(MapsActivity2.this, "Location updated", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;

    public void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mLocationGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationGranted = false;
                            break;
                        }
                    }
                    mLocationGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    Double friendLat;
    Double friendLng;
    String selectedItem;

    private void moveCameraToFriendLocation() {

        fam.setVisibility(View.INVISIBLE);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            selectedItem = b.getString("selecteditem");
        }
        Log.d("selectedItem", selectedItem);

        mDatabaseReference.child(selectedItem).child("GeoPoint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    friendLat = (Double) dataSnapshot.child("Latitude").getValue();
                    friendLng = (Double) dataSnapshot.child("Longitude").getValue();

                    LatLng coord = new LatLng(friendLat, friendLng);
                    mMap.addMarker(new MarkerOptions()
                            .position(coord)
                            .title(selectedItem));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(14.0f).build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.moveCamera(cameraUpdate);

                } catch (Exception e) {
                    Log.d("friendlocation", "Moving friend location error" + e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
