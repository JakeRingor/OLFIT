package com.example.olfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.olfit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NutritionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        // Observe food items
        viewModel.breakfastItems.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
        viewModel.lunchItems.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
        viewModel.dinnerItems.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
        
        // Observe goals
        viewModel.calorieGoal.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
        viewModel.carbsGoal.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
        viewModel.fatGoal.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
        viewModel.proteinGoal.observe(viewLifecycleOwner) { calculateAndDisplayNutrition() }
    }

    private fun calculateAndDisplayNutrition() {
        val breakfast = viewModel.breakfastItems.value ?: emptyList()
        val lunch = viewModel.lunchItems.value ?: emptyList()
        val dinner = viewModel.dinnerItems.value ?: emptyList()
        
        val allItems = breakfast + lunch + dinner
        
        val totalCalories = allItems.sumOf { it.calories }
        val totalProtein = allItems.sumOf { it.protein }
        val totalCarbs = allItems.sumOf { it.carbs }
        val totalFat = allItems.sumOf { it.fat }

        updateNutritionDashboard(
            consumed = totalCalories,
            goal = viewModel.calorieGoal.value ?: 2000,
            carbs = totalCarbs, carbsGoal = viewModel.carbsGoal.value ?: 500,
            fat = totalFat, fatGoal = viewModel.fatGoal.value ?: 50,
            protein = totalProtein, proteinGoal = viewModel.proteinGoal.value ?: 100
        )
        
        updateMealUI(breakfast, lunch, dinner)
    }

    private fun updateNutritionDashboard(
        consumed: Int, goal: Int,
        carbs: Int, carbsGoal: Int,
        fat: Int, fatGoal: Int,
        protein: Int, proteinGoal: Int
    ) {
        binding.apply {
            // Main Calorie Progress
            progressBar.max = goal
            progressBar.progress = consumed
            tvConsumedCalories.text = consumed.toString()
            tvCalorieGoal.text = "🔥/$goal kcal"

            // Macro Progress
            pbCarbs.max = carbsGoal
            pbCarbs.progress = carbs
            tvCarbsValue.text = "${carbs}g"

            pbFat.max = fatGoal
            pbFat.progress = fat
            tvFatValue.text = "${fat}g"

            pbProtein.max = proteinGoal
            pbProtein.progress = protein
            tvProteinValue.text = "${protein}g"

            // Summary section
            val remaining = goal - consumed
            tvRemainingCalories.text = "$remaining kcal available today"
            
            val mealNames = (viewModel.breakfastItems.value.orEmpty() + 
                            viewModel.lunchItems.value.orEmpty() + 
                            viewModel.dinnerItems.value.orEmpty())
                .map { it.name }
                .distinct()
                .joinToString(", ")
            tvMealsEatenList.text = mealNames.ifEmpty { "No meals yet" }
        }
    }

    private fun updateMealUI(breakfast: List<FoodItem>, lunch: List<FoodItem>, dinner: List<FoodItem>) {
        binding.apply {
            // Breakfast
            breakfast.firstOrNull()?.let {
                tvBreakfastName.text = it.name
                tvBreakfastDetails.text = "${breakfast.size} item(s) (${breakfast.sumOf { i -> i.calories }} cal)"
                ivBreakfast.setImageResource(it.imageRes)
            } ?: run {
                tvBreakfastName.text = "No Breakfast"
                tvBreakfastDetails.text = "Tap to add"
                ivBreakfast.setImageResource(R.drawable.breakfast)
            }

            // Lunch
            lunch.firstOrNull()?.let {
                tvLunchName1.text = it.name
                tvLunchDetails1.text = "${lunch.sumOf { i -> i.calories }} cal"
                ivLunch.setImageResource(it.imageRes)
                
                if (lunch.size > 1) {
                    dividerLunch.visibility = View.VISIBLE
                    layoutLunch2.visibility = View.VISIBLE
                    tvLunchName2.text = lunch[1].name
                    tvLunchDetails2.text = "and more"
                } else {
                    dividerLunch.visibility = View.GONE
                    layoutLunch2.visibility = View.GONE
                }
            } ?: run {
                tvLunchName1.text = "No Lunch"
                tvLunchDetails1.text = "Tap to add"
                ivLunch.setImageResource(R.drawable.lunch)
                dividerLunch.visibility = View.GONE
                layoutLunch2.visibility = View.GONE
            }

            // Dinner
            dinner.firstOrNull()?.let {
                tvDinnerName.text = it.name
                tvDinnerDetails.text = "${dinner.size} item(s) (${dinner.sumOf { i -> i.calories }} cal)"
                ivDinner.setImageResource(it.imageRes)
            } ?: run {
                tvDinnerName.text = "No Dinner"
                tvDinnerDetails.text = "Tap to add"
                ivDinner.setImageResource(R.drawable.dinner)
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            ivProfile.setOnClickListener { navigateTo(ProfileFragment()) }

            dateSelector.setOnClickListener { showToast("Date selection not implemented yet") }

            cardBreakfast.setOnClickListener {
                viewModel.currentMealType = "Breakfast"
                navigateTo(MiddleFragment())
            }
            cardLunch.setOnClickListener {
                viewModel.currentMealType = "Lunch"
                navigateTo(MiddleFragment())
            }
            cardDinner.setOnClickListener {
                viewModel.currentMealType = "Dinner"
                navigateTo(MiddleFragment())
            }
            layoutTotalSummary.setOnClickListener { navigateTo(SummaryFragment()) }
        }
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
