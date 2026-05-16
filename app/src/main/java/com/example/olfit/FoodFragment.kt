package com.example.olfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.olfit.databinding.FragmentFoodBinding

class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NutritionViewModel by activityViewModels()

    // Pre-defined food library with macros
    private val foodLibrary = mapOf(
        "Adobo" to FoodItem("Adobo (Itlog)", 192, protein = 8, carbs = 15, fat = 7, R.drawable.adobo_itlog, "Philippines"),
        "Sisig" to FoodItem("Pork Sisig (Grill)", 330, protein = 23, carbs = 26, fat = 15, R.drawable.pork_sisig, "Philippines"),
        "Pinakbet" to FoodItem("Pinakbet (Gulay)", 256, protein = 10, carbs = 25, fat = 8, R.drawable.pinakbet, "Philippines"),
        "Tapsilog" to FoodItem("Tapsilog (Beef)", 450, protein = 20, carbs = 50, fat = 7, R.drawable.tapsilog, "Philippines"),
        "Rice" to FoodItem("Puting Kanin (Rice)", 242, protein = 4, carbs = 44, fat = 0, R.drawable.whiterice, "Philippines")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
    }

    private fun setupListeners() {
        // Profile icon click
        binding.ivFoodProfile.setOnClickListener {
            navigateTo(ProfileFragment())
        }

        // Food item click listeners - Now adds to the current log
        binding.itemAdobo.setOnClickListener {
            addFoodToLog("Adobo")
        }

        binding.itemSisig.setOnClickListener {
            addFoodToLog("Sisig")
        }

        binding.itemPinakbet.setOnClickListener {
            addFoodToLog("Pinakbet")
        }

        binding.itemTapsilog.setOnClickListener {
            addFoodToLog("Tapsilog")
        }

        binding.itemRice.setOnClickListener {
            addFoodToLog("Rice")
        }
    }

    private fun addFoodToLog(key: String) {
        val item = foodLibrary[key]
        if (item != null) {
            viewModel.addFoodItem(item)
            showToast("Added ${item.name} to ${viewModel.currentMealType}")
            
            // Optionally navigate back to Home to see the update
            navigateTo(HomeFragment())
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