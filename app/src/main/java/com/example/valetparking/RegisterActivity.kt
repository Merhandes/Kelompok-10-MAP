package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.valetparking.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val email = binding.emailET.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            val confirmPass = binding.confirmPassET.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailLayout.error = "Enter Email"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = "Enter a valid email address"
            } else if (pass.isEmpty()) {
                binding.passLayout.error = "Enter Password"
            } else if (pass.length < 6) {
                binding.passLayout.error = "Password must be at least 6 characters"
            } else if (confirmPass.isEmpty()) {
                binding.confirmPassLayout.error = "Enter Password Again"
            } else if (pass != confirmPass) {
                binding.confirmPassLayout.error = "Passwords must match"
            } else {
                // Clear any error messages before moving to the next screen
                binding.emailLayout.error = null
                binding.passLayout.error = null
                binding.confirmPassLayout.error = null

                // Proceed to verification activity
                val intent = Intent(this@RegisterActivity, VerificationActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("pass", pass)
                startActivity(intent)
            }
        }
    }
}
