package com.chikuruz.empts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.chikuruz.empts.fragments.HomeFragment;
import com.chikuruz.empts.fragments.LoginFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    public static boolean zoom_Once = false;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    public  static Location myLocation;
    public static Context context;
    public static boolean startPage = true;
    public static JSONObject loginObject = null;
    public static boolean checked = true;






    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateLocation();
            if (findViewById(R.id.fragement_container) != null) {
            if (savedInstanceState == null && loginObject != null) {
                getSupportFragmentManager().
                        beginTransaction().
                        add(R.id.fragement_container, new HomeFragment()).
                        commit();
            } else {
                getSupportFragmentManager().
                        beginTransaction().
                        add(R.id.fragement_container, new LoginFragment()).
                        commit();

            }
                context=MainActivity.this;

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initiateLocation() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest.setInterval(0);    //10 seconds
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.01f);    //50 meters
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        googleApiClient.connect();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    myLocation=location;
                }
            }
        });
        locationUpdate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void locationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    myLocation=location;
//                    Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                }




            };
        };
        checkPermission();
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper());

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private  void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //initiateLocation();

            } else {
                Toast.makeText(MainActivity.this, "Permission denied to access you location", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }

        }
    }


}