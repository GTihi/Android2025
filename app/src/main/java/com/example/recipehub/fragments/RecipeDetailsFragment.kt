package com.example.recipehub.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.recipehub.R
import com.example.recipehub.adapter.IngredientsAdapter
import com.example.recipehub.database.DatabaseHelper
import com.example.recipehub.databinding.FragmentRecipeDetailsBinding
import com.example.recipehub.models.FinalMealsModel
import com.example.recipehub.models.Ingredients

class RecipeDetailsFragment : Fragment() {
    lateinit var binding: FragmentRecipeDetailsBinding
    var finalMealsModel: FinalMealsModel? = null
    var ingredientsList: ArrayList<Ingredients> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            finalMealsModel = it.getSerializable("data") as FinalMealsModel?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener { Navigation.findNavController(it).navigateUp() }

        if (finalMealsModel != null) {
            val dbHelper = DatabaseHelper(requireContext())
            val favorite = dbHelper.getFavoriteByMealId(finalMealsModel?.mealId?:"")
            if (favorite != null){
                binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_24)
            }else{
                binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
            }
            if (finalMealsModel?.ingredients?.isNotEmpty() == true) {
                ingredientsList.addAll(finalMealsModel?.ingredients!!)
            }
            binding.tvRecipeName.text = finalMealsModel?.strMeal
            binding.tvInstructions.text = finalMealsModel?.strInstructions
            Glide.with(binding.root)
                .asBitmap()
                .load(Uri.parse(finalMealsModel?.strMealThumb))
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        binding.ivImage.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

            if (ingredientsList.isNotEmpty()) {
                val recipesAdapter =
                    IngredientsAdapter(ingredientsList, requireContext())
                binding.rvIngredients.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvIngredients.adapter = recipesAdapter
            }

            binding.ivFavorite.setOnClickListener {
                val favorite = dbHelper.getFavoriteByMealId(finalMealsModel?.mealId?:"")
                if (favorite != null){
                    dbHelper.removeFromFavorites(favorite.mealId)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(binding.root).navigateUp()
                }else{
                    dbHelper.addToFavorites(finalMealsModel!!)
                    binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_24)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }

        }


    }

}