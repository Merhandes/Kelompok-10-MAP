package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignup: TextView = findViewById(R.id.btnSignup)
        val btnForgot: TextView = findViewById(R.id.btnForgot)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim() // Trim whitespace
            val password = etPassword.text.toString().trim() // Trim whitespace

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proceed to sign in if email and password are valid
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful, navigate to HomeActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Failed to log in
                        Toast.makeText(this, "Login failed: Email atau Password Salah", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        btnSignup.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnForgot.setOnClickListener{
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }
    }
}