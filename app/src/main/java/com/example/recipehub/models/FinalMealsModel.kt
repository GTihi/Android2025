package com.example.recipehub.models
import java.io.Serializable

data class FinalMealsModel(
    var mealId: String = "",
    var strMeal: String,
    var strCategory: String,
    var strInstructions: String,
    var strMealThumb: String,
    var strTags: String,
    var strYoutube: String,
    var ingredients: ArrayList<Ingredients>
) : Serializable

