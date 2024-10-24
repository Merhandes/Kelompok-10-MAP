package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputLayout

class SetNewPassActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnResetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_pass)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind views
        val newPasswordInputLayout: TextInputLayout = findViewById(R.id.etNewPassword)
        val confirmPasswordInputLayout: TextInputLayout = findViewById(R.id.etConfirmNewPassword)

        etNewPassword = newPasswordInputLayout.editText!!
        etConfirmNewPassword = confirmPasswordInputLayout.editText!!

        btnResetPassword = findViewById(R.id.btnResetPassword)

        // Set up the reset password button click listener
        btnResetPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmNewPassword.text.toString().trim()

        // Check if password fields are empty
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the passwords match
        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Bypass authentication and go directly to ResetCompleteActivity
        val intent = Intent(this, ResetCompleteActivity::class.java)
        startActivity(intent)
        finish() // Optionally close this activity
    }
}
