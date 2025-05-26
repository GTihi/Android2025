package com.example.recipehub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipehub.R
import com.example.recipehub.adapter.RecipesAdapter
import com.example.recipehub.database.DatabaseHelper
import com.example.recipehub.databinding.FragmentMyRecipesBinding
import com.example.recipehub.models.FinalMealsModel

class MyRecipesFragment : Fragment() {
    private var _binding: FragmentMyRecipesBinding? = null
    private val binding get() = _binding!!
    lateinit var dbHelper: DatabaseHelper
    var recipesAdapter: RecipesAdapter? = null
    var listOfMeals: ArrayList<FinalMealsModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        recipesAdapter =
            RecipesAdapter(listOfMeals, requireContext(), "myRecipes")
        binding.rvRecipes.setLayoutManager(GridLayoutManager(requireContext(), 2))
        binding.rvRecipes.setAdapter(recipesAdapter)

        binding.addRecipe.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_navigation_my_recipes_to_addRecipeFragment)
        }

    }

    override fun onResume() {
        super.onResume()

        listOfMeals.clear()
        listOfMeals.addAll(dbHelper.getAllMyRecipes())
        if (listOfMeals.isEmpty()) {
            binding.tvNoRecipeFound.visibility = View.VISIBLE
            binding.rvRecipes.visibility = View.GONE
        } else {
            recipesAdapter?.setList(listOfMeals)
            binding.tvNoRecipeFound.visibility = View.GONE
            binding.rvRecipes.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}