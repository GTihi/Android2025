package com.example.recipehub.models

import java.io.Serializable

data class Ingredients(
    var ingredients: String = "",
    var measures: String = ""
) : Serializable
