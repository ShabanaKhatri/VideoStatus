package com.ploodi.api;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Sugnesh on 2/19/2018.
 */

public interface ApiInterface {

    @GET
    Call<JsonObject> methodCallGet(@Url String url, @QueryMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST
    Call<JsonObject> methodCallPost(@Url String url, @FieldMap HashMap<String, String> params);
}
