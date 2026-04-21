package com.example.olfit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String,
    val email: String
)

class SignUpActivity : AppCompatActivity() {

    private val supabaseUrl = "https://gelbjpuqwagtzmrbcsyo.supabase.co"
    // Note: Keep your keys secure! For production, consider using BuildConfig or secrets.xml
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdlbGJqcHVxd2FndHptcmJjc3lvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3NDc2ODcsImV4cCI6MjA5MjMyMzY4N30.RJ73dVqFAeoU5nrJSIFg6BTAR9rI3O5FKzWfnAS2sYE"

    private val supabaseClient = createSupabaseClient(supabaseUrl, supabaseKey) {
        install(Auth)
        install(Postgrest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_signup)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSignUp = findViewById<Button>(R.id.btn_sign_up_submit)

        btnSignUp.setOnClickListener {
            val username = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Check for empty fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Check for password length (Requirement: at least 8 characters)
            if (password.length < 8) {
                Toast.makeText(this, "Weak password. Password should be at least 8 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Proceed with Registration
            lifecycleScope.launch {
                try {
                    // Sign up the user in Supabase Auth
                    supabaseClient.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }

                    // Get the newly created user ID
                    val userId = supabaseClient.auth.currentUserOrNull()?.id

                    // Save extra user info to the profiles table
                    userId?.let { id ->
                        val profile = Profile(id = id, username = username, email = email)
                        supabaseClient.from("profiles").insert(profile)
                    }

                    Toast.makeText(this@SignUpActivity, "Registration Successful!", Toast.LENGTH_LONG).show()
                    finish()

                } catch (e: Exception) {
                    val errorMessage = e.message ?: ""

                    when {
                        // Server-side weak password catch (if dashboard settings vary)
                        errorMessage.contains("signup_password_too_weak", ignoreCase = true) ||
                                errorMessage.contains("should be at least 8 characters", ignoreCase = true) -> {
                            Toast.makeText(this@SignUpActivity, "Weak password. Password should be at least 8 characters.", Toast.LENGTH_SHORT).show()
                        }

                        // Handle duplicate accounts
                        errorMessage.contains("already registered", ignoreCase = true) ||
                                errorMessage.contains("User already exists", ignoreCase = true) -> {
                            Toast.makeText(this@SignUpActivity, "Email already used", Toast.LENGTH_SHORT).show()
                        }

                        else -> {
                            Toast.makeText(this@SignUpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    e.printStackTrace()
                }
            }
        }

        findViewById<TextView>(R.id.tv_sign_in).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btn_back).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}