package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.valetparking.LoginActivity
import com.example.valetparking.R
import com.example.valetparking.VerificationActivity
import com.example.valetparking.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handling the "Next" button in the SignUp process
        binding.btnRegister.setOnClickListener {
            validateAndProceedToOtp()
        }


        // Login button leading to LoginActivity
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateAndProceedToOtp() {
        val email = binding.textEmail.editText?.text.toString()
        val password = binding.textPassword.editText?.text.toString()
        val confirmPassword = binding.textConfirmationPassword.editText?.text.toString()
        val phoneNumber = binding.textPhoneNumber.editText?.text.toString()

        // Validation checks
        when {
            email.isEmpty() -> binding.textEmail.error = "Enter Email"
            password.isEmpty() -> binding.textPassword.error = "Enter Password"
            confirmPassword.isEmpty() -> binding.textConfirmationPassword.error = "Enter Password Again"
            password != confirmPassword -> binding.textConfirmationPassword.error = "Passwords must match"
            phoneNumber.isEmpty() -> binding.textPhoneNumber.error = "Enter Phone Number"
            else -> {
                // Proceed to OTP Activity
                val intent = Intent(this, VerificationActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("pass", password)
                intent.putExtra("phone", phoneNumber)
                startActivity(intent)
            }
        }
    }
}
