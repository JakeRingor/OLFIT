package com.example.olfit

import android.content.Intent
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
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private val supabaseUrl = "https://gelbjpuqwagtzmrbcsyo.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdlbGJqcHVxd2FndHptcmJjc3lvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3NDc2ODcsImV4cCI6MjA5MjMyMzY4N30.RJ73dVqFAeoU5nrJSIFg6BTAR9rI3O5FKzWfnAS2sYE"

    private val supabaseClient = createSupabaseClient(supabaseUrl, supabaseKey) {
        install(Auth)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_signin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSignIn = findViewById<Button>(R.id.btn_sign_in_submit)

        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        supabaseClient.auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                        }
                        Toast.makeText(this@SignInActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        // Navigate to Home/MainActivity here
                        // val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        // startActivity(intent)
                        // finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@SignInActivity, "Wrong Credentials.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val tvSignUp = findViewById<TextView>(R.id.tv_sign_up)
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
