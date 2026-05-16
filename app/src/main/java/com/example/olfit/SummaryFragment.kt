package com.example.olfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.olfit.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NutritionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Observe all meal lists and update the summary
        viewModel.breakfastItems.observe(viewLifecycleOwner) { updateSummary() }
        viewModel.lunchItems.observe(viewLifecycleOwner) { updateSummary() }
        viewModel.dinnerItems.observe(viewLifecycleOwner) { updateSummary() }
    }

    private fun updateSummary() {
        val breakfast = viewModel.breakfastItems.value ?: emptyList()
        val lunch = viewModel.lunchItems.value ?: emptyList()
        val dinner = viewModel.dinnerItems.value ?: emptyList()

        // Breakfast Summary
        binding.tvSummaryBreakfast.text = if (breakfast.isEmpty()) {
            "Breakfast - None"
        } else {
            val names = breakfast.joinToString(", ") { it.name }
            val totalCal = breakfast.sumOf { it.calories }
            val totalP = breakfast.sumOf { it.protein }
            val totalF = breakfast.sumOf { it.fat }
            val totalC = breakfast.sumOf { it.carbs }
            "Breakfast - $names\nCalories: $totalCal | P: ${totalP}g | F: ${totalF}g | C: ${totalC}g"
        }

        // Lunch Summary
        binding.tvSummaryLunch.text = if (lunch.isEmpty()) {
            "Lunch - None"
        } else {
            val names = lunch.joinToString(", ") { it.name }
            val totalCal = lunch.sumOf { it.calories }
            val totalP = lunch.sumOf { it.protein }
            val totalF = lunch.sumOf { it.fat }
            val totalC = lunch.sumOf { it.carbs }
            "Lunch - $names\nCalories: $totalCal | P: ${totalP}g | F: ${totalF}g | C: ${totalC}g"
        }

        // Dinner Summary
        binding.tvSummaryDinner.text = if (dinner.isEmpty()) {
            "Dinner - None"
        } else {
            val names = dinner.joinToString(", ") { it.name }
            val totalCal = dinner.sumOf { it.calories }
            val totalP = dinner.sumOf { it.protein }
            val totalF = dinner.sumOf { it.fat }
            val totalC = dinner.sumOf { it.carbs }
            "Dinner - $names\nCalories: $totalCal | P: ${totalP}g | F: ${totalF}g | C: ${totalC}g"
        }

        // Total Remaining
        val totalConsumed = (breakfast + lunch + dinner).sumOf { it.calories }
        val remaining = 2000 - totalConsumed
        binding.tvSummaryTotalCalories.text = "$remaining Calories Remaining"
    }

    private fun setupListeners() {
        binding.ivSummaryProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}