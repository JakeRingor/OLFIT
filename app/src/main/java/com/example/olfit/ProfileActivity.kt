package com.example.olfit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.olfit.databinding.ProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupBottomNavigation()
    }

    private fun setupListeners() {
        binding.rlEditProfile.setOnClickListener {
            Toast.makeText(this, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rlNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rlHelpSupport.setOnClickListener {
            Toast.makeText(this, "Help & Support Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rlDarkMode.setOnClickListener {
            Toast.makeText(this, "Dark Mode Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rlLanguage.setOnClickListener {
            Toast.makeText(this, "Language Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rlLogout.setOnClickListener {
            // Add logout logic here (e.g., clear preferences, go to sign in)
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.switchEditProfile.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(this, "Edit Profile $status", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.nav_profile
        
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to Home
                    true
                }
                R.id.nav_nutrients -> {
                    // Navigate to Nutrients
                    true
                }
                R.id.nav_profile -> {
                    // Already on Profile
                    true
                }
                else -> false
            }
        }

        binding.fab.setOnClickListener {
            Toast.makeText(this, "Central Action Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}