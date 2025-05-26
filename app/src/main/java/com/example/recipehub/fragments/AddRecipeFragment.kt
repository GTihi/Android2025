package com.example.recipehub.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipehub.R
import com.example.recipehub.adapter.IngredientsAdapter
import com.example.recipehub.database.DatabaseHelper
import com.example.recipehub.databinding.FragmentAddRecipeBinding
import com.example.recipehub.databinding.FragmentMyRecipesBinding
import com.example.recipehub.models.FinalMealsModel
import com.example.recipehub.models.Ingredients
import java.io.File
import java.util.UUID

class AddRecipeFragment : Fragment() {
    private var _binding: FragmentAddRecipeBinding? = null
    private val binding get() = _binding!!
    lateinit var ingredientsAdapter: IngredientsAdapter
    var ingredientsList: ArrayList<Ingredients> = ArrayList()
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 101
    private val CAMERA_PERMISSION_CODE = 102
    private var imageUriPath: String = ""
    lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRecipeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        ingredientsAdapter = IngredientsAdapter(ingredientsList, requireContext())
        binding.rvIngredients.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvIngredients.adapter = ingredientsAdapter

        binding.ivBack.setOnClickListener { Navigation.findNavController(it).navigateUp() }

        binding.btnAddIngredient.setOnClickListener {
            val ingredient = binding.etIngredient.text.toString()
            val quantity = binding.etQuantity.text.toString()
            if (ingredient.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill both ingredient and quantity",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                ingredientsList.add(Ingredients(ingredient, quantity))
                ingredientsAdapter.setIngredientsList(ingredientsList)
                binding.etIngredient.setText("")
                binding.etQuantity.setText("")
            }
        }

        binding.ivImage.setOnClickListener {
            val options = arrayOf("Open Camera", "Open Gallery")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            builder.show()
        }

        binding.btnAddRecipe.setOnClickListener {
            val recipeName = binding.etName.text.toString()
            val instructions = binding.etInstructions.text.toString()

            if (imageUriPath.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Picture should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (recipeName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Recipe name should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (instructions.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Instructions should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (ingredientsList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Ingredients should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val id = UUID.randomUUID().toString()
            dbHelper.addToMyRecipes(
                FinalMealsModel(
                    mealId = id,
                    strMeal = recipeName,
                    strCategory = "",
                    strInstructions = instructions,
                    strMealThumb = imageUriPath,
                    strTags = "",
                    strYoutube = "",
                    ingredients = ingredientsList
                )
            )
            Toast.makeText(requireContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(it).navigateUp()
        }

    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activity?.applicationContext?.packageManager!!) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        binding.ivImage.setImageBitmap(bitmap)
                        val uri = saveBitmapToCacheAndGetUri(bitmap)
                        imageUriPath = uri.toString()
                    }
                }

                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data
                    if (uri != null) {
                        imageUriPath = uri.toString()
                        binding.ivImage.setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun saveBitmapToCacheAndGetUri(bitmap: Bitmap): Uri {
        val file = File(activity?.cacheDir, "note_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${activity?.packageName}.fileprovider",
            file
        )
    }

}