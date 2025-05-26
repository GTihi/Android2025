package com.example.recipehub.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.recipehub.models.FinalMealsModel
import com.example.recipehub.models.Ingredients
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "recipe_favorites.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_NAME = "favorites"
        const val TABLE_MY_RECIPES = "myrecipes"

        const val COL_ID = "mealId"
        const val COL_NAME = "strMeal"
        const val COL_CATEGORY = "strCategory"
        const val COL_INSTRUCTIONS = "strInstructions"
        const val COL_IMAGE = "strMealThumb"
        const val COL_TAGS = "strTags"
        const val COL_YOUTUBE = "strYoutube"
        const val COL_INGREDIENTS_JSON = "ingredientsJson"
    }

    private val gson = Gson()

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NAME TEXT,
                $COL_CATEGORY TEXT,
                $COL_INSTRUCTIONS TEXT,
                $COL_IMAGE TEXT,
                $COL_TAGS TEXT,
                $COL_YOUTUBE TEXT,
                $COL_INGREDIENTS_JSON TEXT
            )
        """.trimIndent()

        val createMyRecipesQuery = """
            CREATE TABLE $TABLE_MY_RECIPES (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NAME TEXT,
                $COL_CATEGORY TEXT,
                $COL_INSTRUCTIONS TEXT,
                $COL_IMAGE TEXT,
                $COL_TAGS TEXT,
                $COL_YOUTUBE TEXT,
                $COL_INGREDIENTS_JSON TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
        db.execSQL(createMyRecipesQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MY_RECIPES")
        onCreate(db)
    }

    fun addToFavorites(meal: FinalMealsModel): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, meal.mealId)
            put(COL_NAME, meal.strMeal)
            put(COL_CATEGORY, meal.strCategory)
            put(COL_INSTRUCTIONS, meal.strInstructions)
            put(COL_IMAGE, meal.strMealThumb)
            put(COL_TAGS, meal.strTags)
            put(COL_YOUTUBE, meal.strYoutube)
            put(COL_INGREDIENTS_JSON, gson.toJson(meal.ingredients))
        }

        val result =
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result != -1L
    }

    fun removeFromFavorites(mealId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(mealId))
        db.close()
        return result > 0
    }

    fun getFavoriteByMealId(mealId: String): FinalMealsModel? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_ID = ?",
            arrayOf(mealId),
            null,
            null,
            null
        )

        var meal: FinalMealsModel? = null
        if (cursor.moveToFirst()) {
            val ingredientsJson =
                cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS_JSON))
            val ingredientsList: ArrayList<Ingredients> = gson.fromJson(
                ingredientsJson,
                object : TypeToken<ArrayList<Ingredients>>() {}.type
            )

            meal = FinalMealsModel(
                mealId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                strMeal = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                strCategory = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                strInstructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)),
                strMealThumb = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)),
                strTags = cursor.getString(cursor.getColumnIndexOrThrow(COL_TAGS)),
                strYoutube = cursor.getString(cursor.getColumnIndexOrThrow(COL_YOUTUBE)),
                ingredients = ingredientsList
            )
        }
        cursor.close()
        db.close()
        return meal
    }

    fun getAllFavorites(): ArrayList<FinalMealsModel> {
        val list = ArrayList<FinalMealsModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val ingredientsJson =
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS_JSON))
                val ingredientsList: ArrayList<Ingredients> = gson.fromJson(
                    ingredientsJson,
                    object : TypeToken<ArrayList<Ingredients>>() {}.type
                )

                val meal = FinalMealsModel(
                    mealId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    strMeal = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    strCategory = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    strInstructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)),
                    strMealThumb = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)),
                    strTags = cursor.getString(cursor.getColumnIndexOrThrow(COL_TAGS)),
                    strYoutube = cursor.getString(cursor.getColumnIndexOrThrow(COL_YOUTUBE)),
                    ingredients = ingredientsList
                )
                list.add(meal)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun addToMyRecipes(meal: FinalMealsModel): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, meal.mealId)
            put(COL_NAME, meal.strMeal)
            put(COL_CATEGORY, meal.strCategory)
            put(COL_INSTRUCTIONS, meal.strInstructions)
            put(COL_IMAGE, meal.strMealThumb)
            put(COL_TAGS, meal.strTags)
            put(COL_YOUTUBE, meal.strYoutube)
            put(COL_INGREDIENTS_JSON, gson.toJson(meal.ingredients))
        }

        val result =
            db.insertWithOnConflict(TABLE_MY_RECIPES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result != -1L
    }

    fun getAllMyRecipes(): List<FinalMealsModel> {
        val list = mutableListOf<FinalMealsModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MY_RECIPES", null)

        if (cursor.moveToFirst()) {
            do {
                val ingredientsJson =
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS_JSON))
                val ingredientsList: ArrayList<Ingredients> = gson.fromJson(
                    ingredientsJson,
                    object : TypeToken<ArrayList<Ingredients>>() {}.type
                )

                val meal = FinalMealsModel(
                    mealId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    strMeal = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    strCategory = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    strInstructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)),
                    strMealThumb = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)),
                    strTags = cursor.getString(cursor.getColumnIndexOrThrow(COL_TAGS)),
                    strYoutube = cursor.getString(cursor.getColumnIndexOrThrow(COL_YOUTUBE)),
                    ingredients = ingredientsList
                )
                list.add(meal)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }


    fun removeFromMyRecipes(mealId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_MY_RECIPES, "$COL_ID = ?", arrayOf(mealId))
        db.close()
        return result > 0
    }


}
