package com.razi.majdoor_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class home extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private EditText search_edittext;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private boolean locationPermissionGranted;
    private MarkerOptions markerOptions;
    private Marker marker;
    private SearchView mapSearchView;
    private EditText workDescriptionEditText;
    private Button descriptionButton;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        search_edittext = findViewById(R.id.search_edittext);
        Button goButton = findViewById(R.id.search_button);
        mapSearchView=findViewById(R.id.search_bar);
        workDescriptionEditText = new EditText(this);
        descriptionButton = findViewById(R.id.descriptionBtn);

        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a custom dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                builder.setTitle("Enter Work Description!");
                builder.setView(R.layout.work_description);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the text from the EditText and do something with it
                        String workDescription = workDescriptionEditText.getText().toString();
                        // Do something with the work description
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Get the EditText from the dialog's layout
                workDescriptionEditText = dialog.findViewById(R.id.work_description);
            }
        });

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location= mapSearchView.getQuery().toString();
                List<Address> addresslist=null;

                if(location !=null)
                {
                    Geocoder geocoder= new Geocoder(home.this);
                    try {
                        addresslist= geocoder.getFromLocationName(location,1);
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Address address= addresslist.get(0);
                    LatLng latLng= new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!locationPermissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
      /*  search_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, SearchLocationActivittty.class);
                startActivity(intent);
            }
        });*/
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = search_edittext.getText().toString().trim();
                LatLng latLng;

                if (location.isEmpty()) {
                    latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                } else {
                    latLng = getLocationFromAddress(location);
                }

                if (latLng != null) {
                    mMap.clear();
                    markerOptions = new MarkerOptions().position(latLng).title(location).draggable(true);
                    marker = mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnMarkerDragListener(this);

        if (locationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastKnownLocation = location;
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        markerOptions = new MarkerOptions().position(currentLocation).title("Current Location");
                        marker = mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                    }
                }
            });
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 1f));
        }
    }

    private LatLng getLocationFromAddress(String location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }

    @Override
    public void onCameraMove() {
        if (marker != null) {
            LatLng latLng = mMap.getCameraPosition().target;
            marker.setPosition(latLng);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
    }

    @Override
    public void onCameraIdle() {
        if (marker != null) {
            LatLng latLng = mMap.getCameraPosition().target;
            marker.setVisible(true);
            marker.setPosition(latLng);
            getAddressFromLocation(latLng);
        }
    }

    private void getAddressFromLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String address = "";

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditText searchEditText = findViewById(R.id.search_edittext);
        searchEditText.setText(address);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }
}