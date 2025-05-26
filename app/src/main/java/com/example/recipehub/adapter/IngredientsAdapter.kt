package com.example.recipehub.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipehub.R
import com.example.recipehub.models.Ingredients

class IngredientsAdapter(
    private var list: List<Ingredients>,
    private val context: Context
) : RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    fun setIngredientsList(list: List<Ingredients>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ingredients, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredients = list[position]
        holder.tvIngredients.text = "${ingredients.ingredients}, ${ingredients.measures}"
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIngredients: TextView = itemView.findViewById(R.id.tvIngredients)
    }
}
