package com.chikuruz.empts.fragments;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chikuruz.empts.MainActivity;
import com.chikuruz.empts.R;
import com.chikuruz.empts.services.Services;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.chikuruz.empts.MainActivity.myLocation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends BaseFragment {
    @BindView(R.id.empcode)
    EditText empcode;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_button)
    Button login_button;
    @BindView(R.id.login_error)
    TextView login_error;

    Vibrator v;


    private OnFragmentInteractionListener mListener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
           // v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,root);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmptsApp();
            }
        });

        return  root;
    }


    private void loginEmptsApp() {
        String e_empCode = empcode.getText().toString();
        String upasswords = password.getText().toString();

        login_button.setText("Loading...");
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().client(client) .baseUrl(baseUrl).build();
        Services service = retrofit.create(Services.class);

        Call<ResponseBody> login = service.employeeLogin(e_empCode,upasswords);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseData = response.body().string().toString();
                    if (!responseData.equals("Failed")){
                        JSONObject data = new JSONObject(responseData);
                        JSONObject logIn = data.getJSONObject("response");

                       // Log.d("my data", data.getString("fullname"));
                        MainActivity.loginObject = logIn;
                        UpdateEmployee(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));


                        getActivity().getSupportFragmentManager().
                                beginTransaction().
                                add(R.id.fragement_container, new HomeFragment()).
                                commit();
                    }else {
                        login_error.setText("Login failed. Please check your EC Number or password.");
                        login_error.setVisibility(View.VISIBLE);
                    }

                } catch (Exception ex) {
                    ex.getMessage();

                    login_error.setText("Login failed. Please check your EC Number or password.");
                    login_error.setVisibility(View.VISIBLE);
                    login_button.setText("Login");
                }
            }
            

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                login_error.setText("Cannot Connect to the remote server!!");
                login_error.setVisibility(View.VISIBLE);
                login_button.setText("Login");
            }
        });


    }
    public static void UpdateEmployee(LatLng latLng) {
        Location myLocation_ = myLocation;

        if (myLocation_ != null) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .build();

                Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseUrl).build();
                Services service = retrofit.create(Services.class);
                Call<ResponseBody> getMenu = service.employeeCurrentLocation(MainActivity.loginObject.getString("user_id"), myLocation_.getLatitude(), myLocation_.getLongitude(),"at Buse");
                getMenu.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            String serverResponse = response.body().string().toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
              //  employeeCurrentLocation = service.employeeCurrentLocation(MainActivity.loginObject.getString("id"), myLocation_.getLatitude(), myLocation_.getLongitude(),"at Buse");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
       void onFragmentInteraction(Uri uri);
    }
}