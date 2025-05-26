package com.example.recipehub.models
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MealsModel(
    @SerializedName("meals")
    @Expose
    var meals: ArrayList<Meal>? = null
)
