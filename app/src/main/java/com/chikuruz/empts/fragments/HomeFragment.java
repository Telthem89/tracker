package com.chikuruz.empts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chikuruz.empts.MainActivity;
import com.chikuruz.empts.R;
import com.chikuruz.empts.services.Services;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.chikuruz.empts.MainActivity.loginObject;
import static com.chikuruz.empts.MainActivity.myLocation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, SensorEventListener {

    /***********Retrofit******************************************************************************/
    static OkHttpClient client = new OkHttpClient.Builder()
            .build();

    @BindView(R.id.btnActivate)
    Button btnActivate;

    static Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseUrl).build();
    static Services service = retrofit.create(Services.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Bitmap bpc;


    GoogleMap map;
    public static GoogleMap staticMap;
    public static boolean cameraMoved;

    @BindView(R.id.empdaysN)
    TextView empdaysN;


    float[] mGravity;
    float[] mGeomagnetic;
    float azimut = 0;
    static Context context;
    static FragmentActivity activity;
    JSONArray requestss;
    public static int requests_count = 0;
    static Call<ResponseBody> employeeCurrentLocation;
    static Call<ResponseBody> updateemployeeCurrentLocation;


    @BindView(R.id.map)
    MapView mVmap;

    public static Intent navigationIntent;
    public static Activity myActivity;

    static Location myLocation_;

    private OnFragmentInteractionListener mListener;
    Handler handler = new Handler();
    Handler rerouteHandler = new Handler();
    Runnable runnable;
    public static Marker marker;
    Bitmap bmp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        //GetEmployeeLoginTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);
        activity = getActivity();
        mVmap.onCreate(savedInstanceState);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.emp);
        bpc = bmp;
        context = getContext();
        try {
            empdaysN.setText(MainActivity.loginObject.getString("fullname"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        myActivity = getActivity();
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginoutEmptsApp();
            }
        });

        return root;
    }

    private void loginoutEmptsApp() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Retrofit retrofit = new Retrofit.Builder().client(client) .baseUrl(baseUrl).build();
        Services service = retrofit.create(Services.class);
        Call<ResponseBody> login = null;
        try {
            login = service.employeeLogout(loginObject.getString("user_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        };
        loginObject= null;

        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseData = response.body().string().toString();
                    if (!responseData.equals("Failed")){
                        getActivity().getSupportFragmentManager().
                                beginTransaction().
                                add(R.id.fragement_container, new LoginFragment()).
                                commit();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String error = t.getMessage();
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degree = Math.round(sensorEvent.values[0]);
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values;

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                azimut = orientation[0];
            }
        }
        float rotation = azimut * 360 / (2 * 3.14159f);


        try {
            marker.setRotation(rotation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Onmap Ready /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            staticMap = map;
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            staticMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-19.0154, 29.1549), 13.06f));
            if (myLocation != null) {
                moveMapCamera(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), myLocation.getBearing());
                cameraMoved = true;

            }
            if (ActivityCompat.checkSelfPermission(MainActivity.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    void initialiseMap() {
        mVmap.getMapAsync(this);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialiseMap();
    }
    @Override
    public void onResume() {
        super.onResume();
        mVmap.onResume();

    }
    @Override
    public void onPause() {
        mVmap.onPause();
        super.onPause();

    }

    @Override
    public void onDestroy() {
        mVmap.onDestroy();
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mVmap.onLowMemory();
    }
    //Move map on initialisation//////////////////////////////////////////////////////////////////////
    public static void moveMapCamera(LatLng mylocation, float bearing) {

        if (staticMap != null && !cameraMoved) {

            staticMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 17.06f));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mylocation);

            bpc = bpc.createScaledBitmap(bpc, 60, 100, false);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bpc);

            markerOptions.icon(icon);
            if (marker != null)
                marker.remove();

            marker = staticMap.addMarker(markerOptions);
        } else
            MainActivity.zoom_Once = false;
    }
    //Moving car marker to follow the employee////////////////////////////////////////////////////////////////
    public static void moveMarker(LatLng mylocation, float bearing) {
        myLocation_ = myLocation;

        if (myLocation_ != null && updateemployeeCurrentLocation == null) {
            try {
                updateemployeeCurrentLocation = service.updateemployeeCurrentLocation(MainActivity.loginObject.getString("user_id"), myLocation_.getLatitude(), myLocation_.getLongitude(),"At Buse");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (staticMap != null){
            marker.setPosition(mylocation);
            //        marker.setRotation(bearing);
            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(mylocation)
                    .bearing(bearing)/*.tilt(65.5f)*/.zoom(17.06f).build();
            staticMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
            if (!updateemployeeCurrentLocation.isExecuted()){
                updateemployeeCurrentLocation.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try{
//                            String responseData = response.body().string().toString();
                            if (response.code() !=404){
                                    try {
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        String error = t.getMessage();
                        t.printStackTrace();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }
        @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}