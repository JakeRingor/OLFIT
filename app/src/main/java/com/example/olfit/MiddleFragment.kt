package com.example.olfit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.olfit.databinding.FragmentMiddleBinding

class MiddleFragment : Fragment() {

    private var _binding: FragmentMiddleBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NutritionViewModel by activityViewModels()
    
    private var isPhilippinesSelected = true
    private var currentSearchQuery = ""

    // Mock data updated with macros
    private val philippinesFoods = listOf(
        FoodItem("Chicken adobo", 180, protein = 15, carbs = 5, fat = 10, R.drawable.adobo_itlog, "Philippines"),
        FoodItem("White Rice", 400, protein = 8, carbs = 88, fat = 1, R.drawable.whiterice, "Philippines"),
        FoodItem("Pork Sisig", 330, protein = 18, carbs = 2, fat = 28, R.drawable.pork_sisig, "Philippines"),
        FoodItem("Pinakbet", 256, protein = 6, carbs = 15, fat = 12, R.drawable.pinakbet, "Philippines")
    )

    private val myFoods = listOf(
        FoodItem("Tapsilog", 450, protein = 25, carbs = 60, fat = 15, R.drawable.tapsilog, "MyFoods"),
        FoodItem("Fish fillet", 350, protein = 30, carbs = 10, fat = 20, R.drawable.adobo_itlog, "MyFoods"),
        FoodItem("Salad", 120, protein = 2, carbs = 10, fat = 8, R.drawable.pinakbet, "MyFoods"),
        FoodItem("Oatmeal", 150, protein = 6, carbs = 25, fat = 3, R.drawable.breakfast, "MyFoods")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiddleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupSearchFunctionality()
        updateTabUI(true) // Initial state
    }

    private fun setupClickListeners() {
        binding.ivClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAiScan.setOnClickListener { showToast("Opening AI Scanner...") }
        binding.btnGallery.setOnClickListener { showToast("Opening Gallery...") }
        binding.btnBarcode.setOnClickListener { showToast("Opening Barcode Scanner...") }

        binding.tabPhilippines.setOnClickListener { updateTabUI(true) }
        binding.tabMyFoods.setOnClickListener { updateTabUI(false) }

        binding.item1.setOnClickListener { onFoodItemClicked(0) }
        binding.item2.setOnClickListener { onFoodItemClicked(1) }
        binding.item3.setOnClickListener { onFoodItemClicked(2) }
        binding.item4.setOnClickListener { onFoodItemClicked(3) }
    }

    private fun onFoodItemClicked(index: Int) {
        val list = if (isPhilippinesSelected) philippinesFoods else myFoods
        val filteredList = list.filter { it.name.contains(currentSearchQuery, ignoreCase = true) }
        
        if (index < filteredList.size) {
            val selectedItem = filteredList[index]
            viewModel.addFoodItem(selectedItem)
            showToast("Added ${selectedItem.name} to ${viewModel.currentMealType}")
            // Go back to home after adding
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupSearchFunctionality() {
        binding.etSearchFood.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString()
                filterAndDisplayList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateTabUI(isPhilippines: Boolean) {
        isPhilippinesSelected = isPhilippines
        val context = requireContext()
        
        if (isPhilippinesSelected) {
            binding.tabPhilippines.setCardBackgroundColor(ContextCompat.getColor(context, R.color.calorie_progress))
            binding.tabMyFoods.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        } else {
            binding.tabPhilippines.setCardBackgroundColor(ContextCompat.getColor(context, R.color.tab_bg_color))
            binding.tabMyFoods.setBackgroundResource(R.drawable.edit_text_bg)
        }
        
        filterAndDisplayList()
    }

    private fun filterAndDisplayList() {
        val fullList = if (isPhilippinesSelected) philippinesFoods else myFoods
        val filteredList = fullList.filter { it.name.contains(currentSearchQuery, ignoreCase = true) }
        
        updateItemView(binding.item1, binding.tvFoodName1, binding.tvFoodCal1, binding.ivFood1, filteredList.getOrNull(0))
        updateItemView(binding.item2, binding.tvFoodName2, binding.tvFoodCal2, binding.ivFood2, filteredList.getOrNull(1))
        updateItemView(binding.item3, binding.tvFoodName3, binding.tvFoodCal3, binding.ivFood3, filteredList.getOrNull(2))
        updateItemView(binding.item4, binding.tvFoodName4, binding.tvFoodCal4, binding.ivFood4, filteredList.getOrNull(3))
    }

    private fun updateItemView(card: View, title: android.widget.TextView, cal: android.widget.TextView, img: android.widget.ImageView, item: FoodItem?) {
        if (item != null) {
            card.visibility = View.VISIBLE
            title.text = item.name
            cal.text = "${item.calories} cal"
            img.setImageResource(item.imageRes)
        } else {
            card.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}