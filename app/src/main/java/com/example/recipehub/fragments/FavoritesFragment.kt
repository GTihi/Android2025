package com.example.recipehub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipehub.adapter.RecipesAdapter
import com.example.recipehub.database.DatabaseHelper
import com.example.recipehub.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    var recipesAdapter: RecipesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onResume() {
        super.onResume()

        val dbHelper = DatabaseHelper(requireContext())
        val allFavorites = dbHelper.getAllFavorites()
        if (allFavorites.isEmpty()){
            binding.rvRecipes.visibility = View.GONE
            binding.tvNoRecipeFound.visibility = View.VISIBLE
        }else{
            recipesAdapter =
                RecipesAdapter(allFavorites, requireContext(), "favorites")
            binding.rvRecipes.setLayoutManager(GridLayoutManager(requireContext(), 2))
            binding.rvRecipes.setAdapter(recipesAdapter)
            binding.rvRecipes.visibility = View.VISIBLE
            binding.tvNoRecipeFound.visibility = View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}