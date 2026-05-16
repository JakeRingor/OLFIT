package com.example.olfit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NutritionViewModel : ViewModel() {

    // Food Logs
    private val _breakfastItems = MutableLiveData<MutableList<FoodItem>>(mutableListOf())
    val breakfastItems: LiveData<MutableList<FoodItem>> = _breakfastItems

    private val _lunchItems = MutableLiveData<MutableList<FoodItem>>(mutableListOf())
    val lunchItems: LiveData<MutableList<FoodItem>> = _lunchItems

    private val _dinnerItems = MutableLiveData<MutableList<FoodItem>>(mutableListOf())
    val dinnerItems: LiveData<MutableList<FoodItem>> = _dinnerItems

    // User Stats
    private val _weight = MutableLiveData<String>("75")
    val weight: LiveData<String> = _weight

    private val _height = MutableLiveData<String>("175")
    val height: LiveData<String> = _height

    // User Profile
    private val _userName = MutableLiveData<String>("User Name")
    val userName: LiveData<String> = _userName

    // Nutrition Goals
    private val _calorieGoal = MutableLiveData<Int>(2000)
    val calorieGoal: LiveData<Int> = _calorieGoal

    private val _carbsGoal = MutableLiveData<Int>(500)
    val carbsGoal: LiveData<Int> = _carbsGoal

    private val _fatGoal = MutableLiveData<Int>(50)
    val fatGoal: LiveData<Int> = _fatGoal

    private val _proteinGoal = MutableLiveData<Int>(100)
    val proteinGoal: LiveData<Int> = _proteinGoal

    // Current session context
    var currentMealType: String = "Breakfast"

    // Centralized Food Library (to avoid static duplication in Fragments)
    val philippinesLibrary = listOf(
        FoodItem("Chicken adobo", 180, protein = 15, carbs = 5, fat = 10, R.drawable.adobo_itlog, "Philippines"),
        FoodItem("White Rice", 400, protein = 8, carbs = 88, fat = 1, R.drawable.whiterice, "Philippines"),
        FoodItem("Pork Sisig", 330, protein = 18, carbs = 2, fat = 28, R.drawable.pork_sisig, "Philippines"),
        FoodItem("Pinakbet", 256, protein = 6, carbs = 15, fat = 12, R.drawable.pinakbet, "Philippines")
    )

    val myFoodsLibrary = listOf(
        FoodItem("Tapsilog", 450, protein = 25, carbs = 60, fat = 15, R.drawable.tapsilog, "MyFoods"),
        FoodItem("Fish fillet", 350, protein = 30, carbs = 10, fat = 20, R.drawable.adobo_itlog, "MyFoods"),
        FoodItem("Salad", 120, protein = 2, carbs = 10, fat = 8, R.drawable.pinakbet, "MyFoods"),
        FoodItem("Oatmeal", 150, protein = 6, carbs = 25, fat = 3, R.drawable.breakfast, "MyFoods")
    )

    fun addFoodItem(item: FoodItem) {
        val targetLiveData = when (currentMealType) {
            "Breakfast" -> _breakfastItems
            "Lunch" -> _lunchItems
            "Dinner" -> _dinnerItems
            else -> _breakfastItems
        }
        val currentList = targetLiveData.value ?: mutableListOf()
        currentList.add(item)
        targetLiveData.value = currentList
    }

    fun updateWeight(newWeight: String) { _weight.value = newWeight }
    fun updateHeight(newHeight: String) { _height.value = newHeight }
    fun updateUserName(newName: String) { _userName.value = newName }
    fun updateCalorieGoal(newGoal: Int) { _calorieGoal.value = newGoal }

    fun getTotalFoodDone(): Int {
        return (_breakfastItems.value?.size ?: 0) + 
               (_lunchItems.value?.size ?: 0) + 
               (_dinnerItems.value?.size ?: 0)
    }
}
