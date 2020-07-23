package com.chikuruz.empts.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Services {
    @POST("login.php")
    @FormUrlEncoded
    Call<ResponseBody> employeeLogin(@Field("empCode") String empCode,
                                     @Field("password") String password);

    @POST("GetCurrentEmployeeLocation.php")
    @FormUrlEncoded
    Call<ResponseBody> employeeCurrentLocation(@Field("empCode") String empCode,
                                                      @Field("lat") Double latitude,
                                                      @Field("lng") Double longitude,
                                                      @Field("nameplace") String nameplace);
    @POST("updateLocation.php")
    @FormUrlEncoded
    Call<ResponseBody> updateemployeeCurrentLocation(@Field("empCode") String empCode,
                                               @Field("lat") Double latitude,
                                               @Field("lng") Double longitude,
                                               @Field("nameplace") String nameplace);
    @POST("employeeLogout.php")
    @FormUrlEncoded
    Call<ResponseBody> employeeLogout(@Field("empCode")
                                              String empCode);
}
