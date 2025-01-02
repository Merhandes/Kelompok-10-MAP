package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
                        // Login successful
                        val currentUser = auth.currentUser
                        currentUser?.let {
                            // Get the UID and store/retrieve data from Firestore for this user
                            val userId = it.uid
                            // Check if the user's data exists in Firestore
                            db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // User exists, retrieve user data (for example: filled spots)
                                        val savedSpotNumber = document.getLong("spotNumber")?.toInt() ?: 0
                                        // You can store this in SharedViewModel or directly pass it to the next activity
                                    } else {
                                        // New user, create a document for them in Firestore
                                        val newUser = hashMapOf(
                                            "spotNumber" to 0,  // Example field for spot number
                                            "filledSpots" to emptyList<Int>() // Example list for filled spots
                                        )
                                        db.collection("users").document(userId)
                                            .set(newUser)
                                            .addOnSuccessListener {
                                                // Successfully added new user
                                            }
                                    }
                                }
                        }

                        // Navigate to MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Login failed
                        Toast.makeText(this, "Login failed: Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Navigate to signup activity
        btnSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Navigate to forgot password activity
        btnForgot.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }
    }
}
