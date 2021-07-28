package com.example.foodorderingsystem.retrofit;

import android.util.Log;

import com.example.foodorderingsystem.model.Allmenu;
import com.example.foodorderingsystem.model.FoodData;
import com.example.foodorderingsystem.model.Popular;
import com.example.foodorderingsystem.model.Recommended;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import android.util.Log;

public interface ApiInterface {

    @GET("allmenu")
    Call<List<Allmenu>> getAllData();

    @GET("recommended")
    Call<List<Recommended>> getAllRec();

    @GET("popular")
    Call<List<Popular>> getAllPopular();

    // lets make our model class of json data.

}
