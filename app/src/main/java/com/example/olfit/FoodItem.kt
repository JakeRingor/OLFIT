package com.example.olfit

data class FoodItem(
    val name: String,
    val calories: Int,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val imageRes: Int,
    val category: String // "Philippines" or "MyFoods"
)