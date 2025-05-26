package com.example.recipehub.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipehub.R
import com.example.recipehub.adapter.RecipesAdapter
import com.example.recipehub.api.MealDbService
import com.example.recipehub.api.RetrofitClient
import com.example.recipehub.databinding.FragmentExploreBinding
import com.example.recipehub.models.FinalMealsModel
import com.example.recipehub.models.Ingredients
import com.example.recipehub.models.Meal
import com.example.recipehub.models.MealsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    var progressDialog: ProgressDialog? = null
    var service: MealDbService? = null
    var recipesAdapter: RecipesAdapter? = null
    var listOfMeals: ArrayList<FinalMealsModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        progressDialog!!.setTitle(getString(R.string.app_name))
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)

        service = RetrofitClient.retrofitInstance.create(MealDbService::class.java)

        recipesAdapter =
            RecipesAdapter(listOfMeals, requireContext(), "explore")
        binding.rvRecipes.setLayoutManager(GridLayoutManager(requireContext(), 2))
        binding.rvRecipes.setAdapter(recipesAdapter)

        if (listOfMeals.isEmpty()) {
            callRecipeApi()
        } else {
            binding.tvNoRecipeFound.visibility = View.GONE
            binding.rvRecipes.visibility = View.VISIBLE
        }

        // Set up the SearchView listener for filtering products
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    recipesAdapter?.setList(listOfMeals)
                } else {
                    filter(newText)
                }
                return false
            }
        })

    }

    private fun callRecipeApi() {
        progressDialog?.show()
        val call: Call<MealsModel> = service!!.getMealsByArea("British")
        call.enqueue(object : Callback<MealsModel?> {
            override fun onResponse(call: Call<MealsModel?>, response: Response<MealsModel?>) {
                if (response.isSuccessful) {
                    listOfMeals.clear()
                    val mealsModel = response.body()
                    val meals = mealsModel?.meals
                    if (meals != null) {
                        for (meal in meals) {
                            // Fetch full details for each meal by ID
                            service!!.getMealDetailsById(meal.idMeal ?: "")
                                .enqueue(object : Callback<MealsModel?> {
                                    override fun onResponse(
                                        call: Call<MealsModel?>,
                                        response: Response<MealsModel?>
                                    ) {
                                        if (response.isSuccessful) {
                                            val detailedMeal = response.body()?.meals?.getOrNull(0)
                                            if (detailedMeal != null) {
                                                val ingredientsList =
                                                    createIngredientsList(detailedMeal)
                                                val finalMeal = FinalMealsModel(
                                                    detailedMeal.idMeal ?: "0",
                                                    detailedMeal.strMeal ?: "",
                                                    detailedMeal.strCategory ?: "",
                                                    detailedMeal.strInstructions ?: "",
                                                    detailedMeal.strMealThumb ?: "",
                                                    detailedMeal.strTags ?: "",
                                                    detailedMeal.strYoutube ?: "",
                                                    ingredientsList
                                                )
                                                listOfMeals.add(finalMeal)
                                                recipesAdapter?.setList(listOfMeals)
                                                binding.tvNoRecipeFound.visibility = View.GONE
                                                binding.rvRecipes.visibility = View.VISIBLE
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<MealsModel?>, t: Throwable) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Detail fetch failed: ${t.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        progressDialog?.dismiss()
                                    }
                                })
                        }
                        progressDialog?.dismiss()
                    } else {
                        progressDialog?.dismiss()
                        binding.tvNoRecipeFound.visibility = View.VISIBLE
                        binding.rvRecipes.visibility = View.GONE
                        Toast.makeText(requireContext(), "No meals found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch meals: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MealsModel?>, t: Throwable) {
                progressDialog?.dismiss()
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun createIngredientsList(meal: Meal): ArrayList<Ingredients> {
        val ingredientsList = ArrayList<Ingredients>()
        try {
            for (i in 1..20) {
                val ingredientField = Meal::class.java.getDeclaredField("strIngredient$i")
                val measureField = Meal::class.java.getDeclaredField("strMeasure$i")
                ingredientField.isAccessible = true
                measureField.isAccessible = true

                val ingredient = ingredientField.get(meal) as? String
                val measure = measureField.get(meal) as? String

                if (!ingredient.isNullOrBlank()) {
                    ingredientsList.add(Ingredients(ingredient, measure ?: ""))
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return ingredientsList
    }

    fun filter(text: String) {
        val filteredList: MutableList<FinalMealsModel> = ArrayList()
        for (model in listOfMeals) {
            if (model.strMeal.lowercase(Locale.ROOT)
                    .contains(text.lowercase())
            ) {
                filteredList.add(model)
            }
        }
        if (filteredList.isEmpty()) {
            binding.tvNoRecipeFound.visibility = View.VISIBLE
            binding.rvRecipes.visibility = View.GONE
        } else {
            binding.tvNoRecipeFound.visibility = View.GONE
            binding.rvRecipes.visibility = View.VISIBLE
        }

        recipesAdapter?.setList(filteredList)    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}