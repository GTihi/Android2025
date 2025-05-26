package com.example.recipehub.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.recipehub.R
import com.example.recipehub.database.DatabaseHelper
import com.example.recipehub.models.FinalMealsModel

class RecipesAdapter(
    private var list: MutableList<FinalMealsModel>,
    private val context: Context,
    private val from: String
) : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

    fun setList(list: MutableList<FinalMealsModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_meals, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mealsModel = list[position]

        if (from == "myRecipes"){
            holder.cvDelete.visibility = View.VISIBLE
        }else{
            holder.cvDelete.visibility = View.GONE
        }
        holder.tvMealsName.text = mealsModel.strMeal
        Glide.with(context)
            .asBitmap()
            .load(Uri.parse(mealsModel.strMealThumb))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.ivImage.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // No action needed
                }
            })

        holder.cvDelete.setOnClickListener{
            val databaseHelper = DatabaseHelper(context)
            databaseHelper.removeFromMyRecipes(mealsModel.mealId)
            list.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            notifyItemRangeChanged(holder.adapterPosition, list.size)
        }

        holder.itemView.setOnClickListener { view ->
            val bundle = Bundle().apply {
                putSerializable("data", mealsModel)
            }
            if (from == "explore"){
                Navigation.findNavController(view).navigate(
                    R.id.action_navigation_explore_to_recipeDetailsFragment,
                    bundle
                )
            }else if (from == "favorites"){
                Navigation.findNavController(view).navigate(
                    R.id.action_navigation_favorites_to_recipeDetailsFragment,
                    bundle
                )
            }else if (from == "myRecipes"){
                Navigation.findNavController(view).navigate(
                    R.id.action_navigation_my_recipes_to_recipeDetailsFragment,
                    bundle
                )
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMealsName: TextView = itemView.findViewById(R.id.tvMealsName)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val cvDelete: CardView = itemView.findViewById(R.id.cvDelete)
    }
}
