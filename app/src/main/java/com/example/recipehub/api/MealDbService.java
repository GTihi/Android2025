package com.example.recipehub.api;

import com.example.recipehub.models.MealsModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealDbService {

    @GET("filter.php")
    Call<MealsModel> getMealsByArea(@Query("a") String area);

    @GET("lookup.php")
    Call<MealsModel> getMealDetailsById(@Query("i") String id);

}
