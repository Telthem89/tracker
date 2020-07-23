package com.chikuruz.empts.fragments;

import androidx.fragment.app.Fragment;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.chikuruz.empts.fragments.HomeFragment.retrofit;

public class BaseFragment extends Fragment {
    //http://192.168.43.220/empts/api/
    //https://panel.freehosting.com/phpMyAdmin/db_export.php?db=mfxcozw_cmsapp for database
    //http://mfx.co.zw/empts/api/GetCurrentEmployeeLocation.php
    //http://mfx.co.zw/empts/api/employeeLogout.php
    //http://mfx.co.zw/empts/api/login.php
    //http://192.168.43.220/empts/api/employeeLogout.php

    public  static  String baseUrl = "http://www.mfx.co.zw/empts/api/";


}
