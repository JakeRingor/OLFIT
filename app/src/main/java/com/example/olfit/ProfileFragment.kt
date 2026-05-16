package com.example.olfit

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.olfit.databinding.FragmentProfileBinding
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NutritionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        loadUserData()
        setupListeners()
    }

    private fun setupObservers() {
        // Observe Name
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.tvUsername.text = name
        }

        // Observe weight and height from ViewModel (Reactive, not static)
        viewModel.weight.observe(viewLifecycleOwner) { weight ->
            binding.tvWeightValue.text = "$weight kg"
        }
        viewModel.height.observe(viewLifecycleOwner) { height ->
            binding.tvHeightValue.text = "$height cm"
        }
        
        // Observe food lists to keep "Food Done" count updated reactively
        viewModel.breakfastItems.observe(viewLifecycleOwner) { updateFoodCount() }
        viewModel.lunchItems.observe(viewLifecycleOwner) { updateFoodCount() }
        viewModel.dinnerItems.observe(viewLifecycleOwner) { updateFoodCount() }
    }

    private fun updateFoodCount() {
        binding.tvFoodDoneValue.text = viewModel.getTotalFoodDone().toString()
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val user = Supabase.client.auth.currentUserOrNull()
                if (user != null && (viewModel.userName.value == "User Name" || viewModel.userName.value.isNullOrBlank())) {
                    val defaultName = user.email?.substringBefore("@")?.uppercase() ?: "USER"
                    viewModel.updateUserName(defaultName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                try {
                    Supabase.client.auth.signOut()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Click listener to edit Name
        binding.tvUsername.setOnClickListener {
            showEditDialog("Name", viewModel.userName.value ?: "", InputType.TYPE_CLASS_TEXT) { newValue ->
                viewModel.updateUserName(newValue)
            }
        }

        // Click listeners to edit stats dynamically
        binding.llWeightCard.setOnClickListener {
            showEditDialog("Weight", viewModel.weight.value ?: "75", InputType.TYPE_CLASS_NUMBER) { newValue ->
                viewModel.updateWeight(newValue)
            }
        }

        binding.llHeightCard.setOnClickListener {
            showEditDialog("Height", viewModel.height.value ?: "175", InputType.TYPE_CLASS_NUMBER) { newValue ->
                viewModel.updateHeight(newValue)
            }
        }

        binding.llFoodDoneCard.setOnClickListener {
            showToast("You have added ${viewModel.getTotalFoodDone()} items to your food log today.")
        }
    }

    private fun showEditDialog(title: String, currentValue: String, inputType: Int, onConfirm: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit $title")

        val input = EditText(requireContext())
        input.inputType = inputType
        input.setText(currentValue)
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val newValue = input.text.toString()
            if (newValue.isNotEmpty()) {
                onConfirm(newValue)
                showToast("$title updated successfully")
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}